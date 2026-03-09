package com.foodtech.ms_factura.infrastructure.adapters.output.file;

import com.foodtech.ms_factura.application.ports.output.TxtFacturaGeneratorPort;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("PMD.LawOfDemeter")
public class FileTxtFacturaGenerator implements TxtFacturaGeneratorPort {

    private static final String FACTURAS_DIR = "/tmp/facturas/";

    @Override
    public Path generar(Factura factura) {
        try {
            Path dirPath = Paths.get(FACTURAS_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = "factura_" + UUID.randomUUID() + ".txt";
            Path filePath = dirPath.resolve(fileName);

            String content = buildFacturaContent(factura);
            Files.write(filePath, content.getBytes());

            log.info("Factura generada y guardada en: {}", filePath);
            return filePath;

        } catch (IOException e) {
            log.error("Error al generar la factura", e);
            throw new IllegalStateException("Error al generar la factura", e);
        }
    }

    private String buildFacturaContent(Factura factura) {
        String customerName = factura.getNombreCliente();
        double totalAmount = factura.getTotal();
        var productos = factura.getListaProductos();
        StringBuilder stringBuilder = new StringBuilder(256);
        stringBuilder.append("Factura para: ").append(customerName).append("\n\n");
        
        String headerLine = String.format("%-20s %-10s %-10s %-10s\n", "Producto", "Cantidad", "Precio", "Subtotal");
        stringBuilder.append(headerLine);
        
        String separator = "-".repeat(50).concat("\n");
        stringBuilder.append(separator);

        for (Producto producto : productos) {
            double subtotal = producto.getCantidad() * producto.getPrecio();
            String productLine = String.format("%-20s %-10d %-10.2f %-10.2f\n",
                    producto.getNombre(),
                    producto.getCantidad(),
                    producto.getPrecio(),
                    subtotal);
            stringBuilder.append(productLine);
        }

        stringBuilder.append(separator);
        String totalLine = String.format("%40s %.2f\n", "Total:", totalAmount);
        stringBuilder.append(totalLine);

        return stringBuilder.toString();
    }
}
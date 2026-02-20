package com.foodtech.ms_factura.infrastructure;

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
public class FileTxtFacturaGenerator implements FacturaGeneratorStrategy {

    private static final String FACTURAS_DIR = "/tmp/facturas/";

    @Override
    public void generar(Factura factura) {
        try {
            // Crear directorio si no existe
            Path dirPath = Paths.get(FACTURAS_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // Generar nombre único para el archivo
            String fileName = "factura_" + UUID.randomUUID() + ".txt";
            Path filePath = dirPath.resolve(fileName);

            // Construir el contenido en formato de tabla
            String content = buildFacturaContent(factura);

            // Escribir al archivo
            Files.write(filePath, content.getBytes());

            log.info("Factura generada y guardada en: {}", filePath.toString());

        } catch (IOException e) {
            log.error("Error al generar la factura", e);
            throw new RuntimeException("Error al generar la factura", e);
        }
    }

    private String buildFacturaContent(Factura factura) {
        StringBuilder sb = new StringBuilder();
        sb.append("Factura para: ").append(factura.getNombreCliente()).append("\n\n");
        sb.append(String.format("%-20s %-10s %-10s %-10s\n", "Producto", "Cantidad", "Precio", "Subtotal"));
        sb.append("-".repeat(50)).append("\n");

        for (Producto producto : factura.getListaProductos()) {
            double subtotal = producto.getCantidad() * producto.getPrecio();
            sb.append(String.format("%-20s %-10d %-10.2f %-10.2f\n",
                    producto.getNombre(),
                    producto.getCantidad(),
                    producto.getPrecio(),
                    subtotal));
        }

        sb.append("-".repeat(50)).append("\n");
        sb.append(String.format("%40s %.2f\n", "Total:", factura.getTotal()));

        return sb.toString();
    }
}
package com.foodtech.ms_factura.infrastructure.adapters.output.file;

import com.foodtech.ms_factura.application.ports.output.PdfFacturaGeneratorPort;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
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
public class FilePdfFacturaGenerator implements PdfFacturaGeneratorPort {

    private static final String FACTURAS_DIR = "/tmp/facturas/";

    @Override
    public void generar(Factura factura) {
        try {
            Path dirPath = Paths.get(FACTURAS_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = "factura_" + UUID.randomUUID() + ".pdf";
            Path filePath = dirPath.resolve(fileName);

            PdfWriter writer = new PdfWriter(filePath.toString());
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Factura para: " + factura.getNombreCliente())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(18));

            Table table = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Producto")));
            table.addHeaderCell(new Cell().add(new Paragraph("Cantidad")));
            table.addHeaderCell(new Cell().add(new Paragraph("Precio")));
            table.addHeaderCell(new Cell().add(new Paragraph("Subtotal")));

            for (Producto producto : factura.getListaProductos()) {
                double subtotal = producto.getCantidad() * producto.getPrecio();
                table.addCell(new Cell().add(new Paragraph(producto.getNombre())));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(producto.getCantidad()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", producto.getPrecio()))));
                table.addCell(new Cell().add(new Paragraph(String.format("%.2f", subtotal))));
            }

            document.add(table);

            Paragraph total = new Paragraph("Total: " + String.format("%.2f", factura.getTotal()))
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14);
            document.add(total);

            document.close();

            log.info("Factura PDF generada y guardada en: {}", filePath);

        } catch (IOException e) {
            log.error("Error al generar la factura PDF", e);
            throw new RuntimeException("Error al generar la factura PDF", e);
        }
    }
}
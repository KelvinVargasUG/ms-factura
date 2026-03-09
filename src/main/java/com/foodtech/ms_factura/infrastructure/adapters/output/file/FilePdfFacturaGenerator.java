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
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CyclomaticComplexity"})
public class FilePdfFacturaGenerator implements PdfFacturaGeneratorPort {

    private static final String FACTURAS_DIR = "/tmp/facturas/";

    @Override
    public Path generar(Factura factura) {
        try {
            Path filePath = createFilePath();
            generatePdfDocument(factura, filePath);
            if (log.isInfoEnabled()) {
                log.info("Factura PDF generada y guardada en: {}", filePath);
            }
            return filePath;
        } catch (IOException ioException) {
            log.error("Error al generar la factura PDF", ioException);
            throw new IllegalStateException("Error al generar la factura PDF", ioException);
        }
    }

    private Path createFilePath() throws IOException {
        Path dirPath = Paths.get(FACTURAS_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        String fileName = "factura_" + UUID.randomUUID() + ".pdf";
        return dirPath.resolve(fileName);
    }

    private void generatePdfDocument(Factura factura, Path filePath) throws IOException {
        String customerName = factura.getNombreCliente();
        double total = factura.getTotal();
        List<Producto> productos = factura.getListaProductos();
        try (PdfWriter writer = new PdfWriter(filePath.toString());
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document document = new Document(pdfDoc)) {
            
            addTitle(document, customerName);
            addProductTable(document, productos);
            addTotal(document, total);
        }
    }

    private void addTitle(Document document, String customerName) {
        Paragraph title = new Paragraph("Factura para: " + customerName);
        title.setTextAlignment(TextAlignment.CENTER);
        title.setFontSize(18);
        document.add(title);
    }

    private void addProductTable(Document document, List<Producto> productos) {
        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 1, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        addTableHeaders(table);
        addTableRows(table, productos);
        
        document.add(table);
    }

    private void addTableHeaders(Table table) {
        table.addHeaderCell(createTextCell("Producto"));
        table.addHeaderCell(createTextCell("Cantidad"));
        table.addHeaderCell(createTextCell("Precio"));
        table.addHeaderCell(createTextCell("Subtotal"));
    }

    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void addTableRows(Table table, List<Producto> productos) {
        Paragraph productParagraph;
        Paragraph quantities;
        Paragraph prices;
        Paragraph subtotals;
        
        for (Producto producto : productos) {
            double subtotal = producto.getCantidad() * producto.getPrecio();
            
            productParagraph = new Paragraph(producto.getNombre());
            quantities = new Paragraph(String.valueOf(producto.getCantidad()));
            prices = new Paragraph(String.format("%.2f", producto.getPrecio()));
            subtotals = new Paragraph(String.format("%.2f", subtotal));
            
            table.addCell(createCellWithParagraph(productParagraph));
            table.addCell(createCellWithParagraph(quantities));
            table.addCell(createCellWithParagraph(prices));
            table.addCell(createCellWithParagraph(subtotals));
        }
    }

    private Cell createTextCell(String text) {
        Paragraph paragraph = new Paragraph(text);
        return createCellWithParagraph(paragraph);
    }

    private Cell createCellWithParagraph(Paragraph paragraph) {
        Cell cell = new Cell();
        cell.add(paragraph);
        return cell;
    }

    private void addTotal(Document document, double totalAmount) {
        Paragraph total = new Paragraph("Total: " + String.format("%.2f", totalAmount));
        total.setTextAlignment(TextAlignment.RIGHT);
        total.setFontSize(14);
        document.add(total);
    }
}
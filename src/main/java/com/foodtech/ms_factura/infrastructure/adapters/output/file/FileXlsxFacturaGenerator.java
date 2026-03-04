package com.foodtech.ms_factura.infrastructure.adapters.output.file;

import com.foodtech.ms_factura.application.ports.output.XlsxFacturaGeneratorPort;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CyclomaticComplexity"})
public class FileXlsxFacturaGenerator implements XlsxFacturaGeneratorPort {

    private static final String FACTURAS_DIR = "/tmp/facturas/";

    @Override
    public Path generar(Factura factura) {
        try {
            Path filePath = createDirectory();
            generateWorkbook(factura, filePath);
            if (log.isInfoEnabled()) {
                log.info("Factura XLSX generada y guardada en: {}", filePath);
            }
            return filePath;
        } catch (IOException ioException) {
            log.error("Error al generar la factura XLSX", ioException);
            throw new IllegalStateException("Error al generar la factura XLSX", ioException);
        }
    }

    private Path createDirectory() throws IOException {
        Path dirPath = Paths.get(FACTURAS_DIR);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
        String fileName = "factura_" + UUID.randomUUID() + ".xlsx";
        return dirPath.resolve(fileName);
    }

    private void generateWorkbook(Factura factura, Path filePath) throws IOException {
        String customerName = factura.getNombreCliente();
        double totalAmount = factura.getTotal();
        var productos = factura.getListaProductos();
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             OutputStream outputStream = Files.newOutputStream(filePath)) {
            
            XSSFSheet sheet = workbook.createSheet("Factura");
            CellStyle moneyStyle = createMoneyCellStyle(workbook);
            
            int rowIndex = addCustomerInfo(sheet, customerName, 0);
            rowIndex = addTableHeaders(sheet, rowIndex + 1);
            rowIndex = addProductRows(sheet, productos, moneyStyle, rowIndex);
            addTotalRow(sheet, totalAmount, moneyStyle, rowIndex);
            autoSizeColumns(sheet);
            
            workbook.write(outputStream);
        }
    }

    private CellStyle createMoneyCellStyle(XSSFWorkbook workbook) {
        DataFormat dataFormat = workbook.createDataFormat();
        CellStyle moneyStyle = workbook.createCellStyle();
        moneyStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));
        return moneyStyle;
    }

    private int addCustomerInfo(XSSFSheet sheet, String customerName, int rowIndex) {
        Row customerRow = sheet.createRow(rowIndex);
        Cell labelCell = customerRow.createCell(0);
        labelCell.setCellValue("Cliente");
        Cell valueCell = customerRow.createCell(1);
        valueCell.setCellValue(customerName);
        return rowIndex + 1;
    }

    private int addTableHeaders(XSSFSheet sheet, int rowIndex) {
        Row headerRow = sheet.createRow(rowIndex);
        Cell productCell = headerRow.createCell(0);
        productCell.setCellValue("Producto");
        Cell quantityCell = headerRow.createCell(1);
        quantityCell.setCellValue("Cantidad");
        Cell priceCell = headerRow.createCell(2);
        priceCell.setCellValue("Precio");
        Cell subtotalCell = headerRow.createCell(3);
        subtotalCell.setCellValue("Subtotal");
        return rowIndex + 1;
    }

    private int addProductRows(XSSFSheet sheet, Iterable<Producto> productos, CellStyle moneyStyle, int rowIndex) {
        int currentRow = rowIndex;
        for (Producto producto : productos) {
            Row row = sheet.createRow(currentRow++);
            Cell nameCell = row.createCell(0);
            nameCell.setCellValue(producto.getNombre());
            Cell countCell = row.createCell(1);
            countCell.setCellValue(producto.getCantidad());
            
            addMoneyCell(row, 2, producto.getPrecio(), moneyStyle);
            double subtotal = producto.getCantidad() * producto.getPrecio();
            addMoneyCell(row, 3, subtotal, moneyStyle);
        }
        return currentRow;
    }

    private void addMoneyCell(Row row, int column, double value, CellStyle moneyStyle) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(moneyStyle);
    }

    private void addTotalRow(XSSFSheet sheet, double totalAmount, CellStyle moneyStyle, int rowIndex) {
        Row totalRow = sheet.createRow(rowIndex + 1);
        Cell labelCell = totalRow.createCell(2);
        labelCell.setCellValue("Total");
        addMoneyCell(totalRow, 3, totalAmount, moneyStyle);
    }

    private void autoSizeColumns(XSSFSheet sheet) {
        for (int column = 0; column <= 3; column++) {
            sheet.autoSizeColumn(column);
        }
    }
}

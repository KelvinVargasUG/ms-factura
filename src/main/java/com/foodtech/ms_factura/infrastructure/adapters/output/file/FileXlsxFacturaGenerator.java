package com.foodtech.ms_factura.infrastructure.adapters.output.file;

import com.foodtech.ms_factura.application.ports.output.XlsxFacturaGeneratorPort;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Row;
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
public class FileXlsxFacturaGenerator implements XlsxFacturaGeneratorPort {

    private static final String FACTURAS_DIR = "/tmp/facturas/";

    @Override
    public void generar(Factura factura) {
        try {
            Path dirPath = Paths.get(FACTURAS_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String fileName = "factura_" + UUID.randomUUID() + ".xlsx";
            Path filePath = dirPath.resolve(fileName);

            try (XSSFWorkbook workbook = new XSSFWorkbook();
                 OutputStream outputStream = Files.newOutputStream(filePath)) {

                XSSFSheet sheet = workbook.createSheet("Factura");
                DataFormat dataFormat = workbook.createDataFormat();

                CellStyle moneyStyle = workbook.createCellStyle();
                moneyStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));

                int rowIndex = 0;
                Row customerRow = sheet.createRow(rowIndex++);
                customerRow.createCell(0).setCellValue("Cliente");
                customerRow.createCell(1).setCellValue(factura.getNombreCliente());

                rowIndex++;
                Row headerRow = sheet.createRow(rowIndex++);
                headerRow.createCell(0).setCellValue("Producto");
                headerRow.createCell(1).setCellValue("Cantidad");
                headerRow.createCell(2).setCellValue("Precio");
                headerRow.createCell(3).setCellValue("Subtotal");

                for (Producto producto : factura.getListaProductos()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(producto.getNombre());
                    row.createCell(1).setCellValue(producto.getCantidad());

                    var precioCell = row.createCell(2);
                    precioCell.setCellValue(producto.getPrecio());
                    precioCell.setCellStyle(moneyStyle);

                    var subtotalCell = row.createCell(3);
                    subtotalCell.setCellValue(producto.getCantidad() * producto.getPrecio());
                    subtotalCell.setCellStyle(moneyStyle);
                }

                Row totalRow = sheet.createRow(rowIndex + 1);
                totalRow.createCell(2).setCellValue("Total");
                var totalCell = totalRow.createCell(3);
                totalCell.setCellValue(factura.getTotal());
                totalCell.setCellStyle(moneyStyle);

                for (int column = 0; column <= 3; column++) {
                    sheet.autoSizeColumn(column);
                }

                workbook.write(outputStream);
            }

            log.info("Factura XLSX generada y guardada en: {}", filePath);
        } catch (IOException e) {
            log.error("Error al generar la factura XLSX", e);
            throw new RuntimeException("Error al generar la factura XLSX", e);
        }
    }
}

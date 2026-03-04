package com.foodtech.ms_factura.infrastructure.adapters.output.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;

@SuppressWarnings({ "PMD.AtLeastOneConstructor", "PMD.JUnitTestContainsTooManyAsserts", "PMD.LawOfDemeter",
        "PMD.AvoidDuplicateLiterals", "PMD.SignatureDeclareThrowsException", "PMD.OnlyOneReturn",
        "PMD.AvoidThrowingRawExceptionTypes" })
@Tag("component")
class FileXlsxFacturaGeneratorTest {

    private static final String FACTURA_PATH = "/tmp/facturas/";

    private final FileXlsxFacturaGenerator generator = new FileXlsxFacturaGenerator();

    @Test
    void testGenerar() throws IOException {
        Producto producto1 = new Producto("plato fuerte", 2, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 25.0, "XLSX");

        generator.generar(factura);

        Path xlsxFile = findLatestXlsxFile();

        try (InputStream inputStream = Files.newInputStream(xlsxFile);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            XSSFSheet sheet = workbook.getSheet("Factura");
            assertThat(sheet).isNotNull();

            assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("Cliente");
            assertThat(sheet.getRow(0).getCell(1).getStringCellValue()).isEqualTo("Kelvin");

            assertThat(sheet.getRow(2).getCell(0).getStringCellValue()).isEqualTo("Producto");
            assertThat(sheet.getRow(2).getCell(1).getStringCellValue()).isEqualTo("Cantidad");
            assertThat(sheet.getRow(2).getCell(2).getStringCellValue()).isEqualTo("Precio");
            assertThat(sheet.getRow(2).getCell(3).getStringCellValue()).isEqualTo("Subtotal");

            assertThat(sheet.getRow(3).getCell(0).getStringCellValue()).isEqualTo("plato fuerte");
            assertThat(sheet.getRow(3).getCell(1).getNumericCellValue()).isEqualTo(2.0);
            assertThat(sheet.getRow(4).getCell(0).getStringCellValue()).isEqualTo("plato entrada");
            assertThat(sheet.getRow(4).getCell(1).getNumericCellValue()).isEqualTo(1.0);
            assertThat(sheet.getRow(6).getCell(2).getStringCellValue()).isEqualTo("Total");
            assertThat(sheet.getRow(6).getCell(3).getNumericCellValue()).isEqualTo(25.0);
        }
    }

    @Test
    void shouldWriteMoneyAsNumericCellsWithTwoDecimalFormat() throws IOException {
        Producto producto = new Producto("plato fuerte", 2, 10.0);
        Factura factura = new Factura("Kelvin", List.of(producto), 20.0, "XLSX");

        generator.generar(factura);

        Path xlsxFile = findLatestXlsxFile();
        try (InputStream inputStream = Files.newInputStream(xlsxFile);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            XSSFSheet sheet = workbook.getSheet("Factura");

            var priceCell = sheet.getRow(3).getCell(2);
            var subtotalCell = sheet.getRow(3).getCell(3);
            var totalCell = sheet.getRow(5).getCell(3);

            assertThat(priceCell.getCellType()).isEqualTo(CellType.NUMERIC);
            assertThat(subtotalCell.getCellType()).isEqualTo(CellType.NUMERIC);
            assertThat(totalCell.getCellType()).isEqualTo(CellType.NUMERIC);

            assertThat(priceCell.getCellStyle().getDataFormatString()).contains("0.00");
            assertThat(subtotalCell.getCellStyle().getDataFormatString()).contains("0.00");
            assertThat(totalCell.getCellStyle().getDataFormatString()).contains("0.00");
        }
    }

    @Test
    void shouldCreateDirectoryWhenItDoesNotExist() throws IOException {
        // Arrange
        deleteFacturasPath();

        Producto producto = new Producto("plato fuerte", 1, 10.0);
        Factura factura = new Factura("Kelvin", List.of(producto), 10.0, "XLSX");

        // Act
        Path generatedFile = generator.generar(factura);

        // Assert
        assertThat(Files.exists(Path.of(FACTURA_PATH))).isTrue();
        assertThat(generatedFile).exists();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFacturaPathIsAFile() throws IOException {
        // Arrange
        deleteFacturasPath();
        Path facturasPath = Path.of(FACTURA_PATH);
        Files.writeString(facturasPath, "not-a-directory");

        Producto producto = new Producto("plato fuerte", 1, 10.0);
        Factura factura = new Factura("Kelvin", List.of(producto), 10.0, "XLSX");

        // Act & Assert
        assertThatThrownBy(() -> generator.generar(factura))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error al generar la factura XLSX")
                .hasCauseInstanceOf(IOException.class);

        deleteFacturasPath();
    }

    private Path findLatestXlsxFile() throws IOException {
        Path facturasDir = Path.of(FACTURA_PATH);
        assertThat(Files.exists(facturasDir)).isTrue();

        return Files.list(facturasDir)
                .filter(path -> path.toString().endsWith(".xlsx"))
                .max(Comparator.comparingLong(this::lastModifiedSafe))
                .orElseThrow();
    }

    private long lastModifiedSafe(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException exception) {
            return 0L;
        }
    }

    private void deleteFacturasPath() throws IOException {
        Path facturasPath = Path.of(FACTURA_PATH);
        if (!Files.exists(facturasPath)) {
            return;
        }

        try (Stream<Path> pathStream = Files.walk(facturasPath)) {
            pathStream
                    .sorted((left, right) -> right.getNameCount() - left.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    });
        }
    }
}

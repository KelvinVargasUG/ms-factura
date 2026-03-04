package com.foodtech.ms_factura.infrastructure.adapters.input.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * End-to-end tests for the Kafka invoice-generation flow.
 *
 * Verifies that a raw JSON message entering the Kafka consumer
 * produces the expected invoice file on disk (TXT, PDF or XLSX).
 *
 * Uses the full Spring context — no mocks, no reflection.
 */
@SpringBootTest(properties = {
        "notification.enabled=false"
})
@Tag("blackbox")
@DisplayName("Kafka Consumer · Invoice File Output")
@SuppressWarnings({
        "PMD.AtLeastOneConstructor",
        "PMD.JUnitTestContainsTooManyAsserts",
        "PMD.LawOfDemeter",
        "PMD.AvoidDuplicateLiterals",
        "PMD.SignatureDeclareThrowsException",
        "PMD.CyclomaticComplexity",
        "PMD.LongVariable",
        "PMD.ShortVariable",
        "PMD.OnlyOneReturn"
})
class KafkaConsumerInvoiceOutputTest {

    private static final Path FACTURAS_DIR = Path.of("/tmp/facturas");

    @Autowired
    private KafkaConsumerAdapter kafkaConsumer;

    private long testStartTimestamp;

    @BeforeEach
    void recordStartTime() {
        testStartTimestamp = System.currentTimeMillis();
    }

    @AfterEach
    void cleanupGeneratedFiles() throws IOException {
        if (Files.exists(FACTURAS_DIR)) {
            try (Stream<Path> files = Files.list(FACTURAS_DIR)) {
                files.filter(p -> lastModifiedSafe(p) >= testStartTimestamp)
                        .forEach(p -> {
                            try {
                                Files.deleteIfExists(p);
                            } catch (IOException ignored) {
                                /* best-effort */ }
                        });
            }
        }
    }

    @Test
    @DisplayName("formato=txt → generates TXT file with correct content")
    void shouldGenerateTxtFileFromKafkaMessage() throws IOException {
        String message = buildKafkaMessage("Juan Pérez", "Pizza Margherita", 2, 15.50, 31.00, "txt");

        String result = kafkaConsumer.consume(message);

        assertThat(result).contains("exitosamente").contains("Juan Pérez");

        Path txtFile = findLatestFile(".txt");
        String content = Files.readString(txtFile);
        assertThat(content)
                .contains("Juan Pérez")
                .contains("Pizza Margherita")
                .containsPattern("Total.*31");
    }

    @Test
    @DisplayName("formato=pdf → generates non-empty PDF file")
    void shouldGeneratePdfFileFromKafkaMessage() throws IOException {
        String message = buildKafkaMessage("María López", "Ensalada César", 1, 12.00, 12.00, "pdf");

        String result = kafkaConsumer.consume(message);

        assertThat(result).contains("exitosamente").contains("María López");

        Path pdfFile = findLatestFile(".pdf");
        assertThat(Files.size(pdfFile)).isGreaterThan(0);
    }

    @Test
    @DisplayName("formato=xlsx → generates XLSX spreadsheet with correct cells")
    void shouldGenerateXlsxFileFromKafkaMessage() throws IOException {
        String message = buildKafkaMessage("Carlos Ruiz", "Sopa de Tomate", 3, 8.50, 25.50, "xlsx");

        String result = kafkaConsumer.consume(message);

        assertThat(result).contains("exitosamente").contains("Carlos Ruiz");

        Path xlsxFile = findLatestFile(".xlsx");
        try (InputStream inputStream = Files.newInputStream(xlsxFile);
                XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            XSSFSheet sheet = workbook.getSheet("Factura");
            assertThat(sheet).isNotNull();
            assertThat(sheet.getRow(0).getCell(1).getStringCellValue())
                    .isEqualTo("Carlos Ruiz");
            assertThat(sheet.getRow(3).getCell(0).getStringCellValue())
                    .isEqualTo("Sopa de Tomate");
            assertThat(sheet.getRow(3).getCell(1).getNumericCellValue())
                    .isEqualTo(3.0);
        }
    }

    @Test
    @DisplayName("formato=null → falls back to TXT generation")
    void shouldFallbackToTxtWhenFormatIsNull() throws IOException {
        String payload = String.format(Locale.US,
                "{\\\"nombreCliente\\\":\\\"Ana Torres\\\","
                        + "\\\"listaProductos\\\":[{\\\"nombre\\\":\\\"Café Americano\\\","
                        + "\\\"cantidad\\\":1,\\\"precio\\\":3.50}],"
                        + "\\\"total\\\":3.50,"
                        + "\\\"formato\\\":null}");

        String message = String.format(
                "{\"eventId\":\"evt-002\","
                        + "\"eventType\":\"FACTURA_CREATED\","
                        + "\"payload\":\"%s\","
                        + "\"timestamp\":\"2026-03-08T10:00:00\"}",
                payload);

        String result = kafkaConsumer.consume(message);

        assertThat(result).contains("exitosamente");

        Path txtFile = findLatestFile(".txt");
        String content = Files.readString(txtFile);
        assertThat(content).contains("Ana Torres").contains("Café Americano");
    }

    // ── helpers ──────────────────────────────────────────────

    private static String buildKafkaMessage(String cliente, String producto,
            int cantidad, double precio, double total, String formato) {

        String payload = String.format(Locale.US,
                "{\\\"nombreCliente\\\":\\\"%s\\\","
                        + "\\\"listaProductos\\\":[{\\\"nombre\\\":\\\"%s\\\","
                        + "\\\"cantidad\\\":%d,\\\"precio\\\":%.2f}],"
                        + "\\\"total\\\":%.2f,"
                        + "\\\"formato\\\":\\\"%s\\\"}",
                cliente, producto, cantidad, precio, total, formato);

        return String.format(
                "{\"eventId\":\"evt-001\","
                        + "\"eventType\":\"FACTURA_CREATED\","
                        + "\"payload\":\"%s\","
                        + "\"timestamp\":\"2026-03-08T10:00:00\"}",
                payload);
    }

    private Path findLatestFile(String extension) throws IOException {
        assertThat(FACTURAS_DIR).exists();

        try (Stream<Path> files = Files.list(FACTURAS_DIR)) {
            List<Path> matches = files
                    .filter(p -> p.toString().endsWith(extension))
                    .filter(p -> lastModifiedSafe(p) >= testStartTimestamp)
                    .sorted(Comparator.comparingLong(this::lastModifiedSafe).reversed())
                    .toList();

            assertThat(matches)
                    .as("Expected at least one %s file in %s", extension, FACTURAS_DIR)
                    .isNotEmpty();

            return matches.get(0);
        }
    }

    private long lastModifiedSafe(Path path) {
        try {
            return Files.getLastModifiedTime(path).toMillis();
        } catch (IOException exception) {
            return 0L;
        }
    }
}

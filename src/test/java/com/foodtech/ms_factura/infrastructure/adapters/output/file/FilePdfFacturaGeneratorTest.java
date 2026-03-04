package com.foodtech.ms_factura.infrastructure.adapters.output.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;

@SuppressWarnings({ "PMD.AtLeastOneConstructor", "PMD.JUnitTestContainsTooManyAsserts", "PMD.LawOfDemeter",
        "PMD.AvoidDuplicateLiterals", "PMD.SignatureDeclareThrowsException", "PMD.AvoidThrowingRawExceptionTypes" })
@Tag("component")
class FilePdfFacturaGeneratorTest {

    private static final String FACTURA_PATH = "/tmp/facturas/";

    private final FilePdfFacturaGenerator generator = new FilePdfFacturaGenerator();

    @Test
    void testGenerar() throws IOException {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "PDF");

        generator.generar(factura);

        Path facturasDir = Path.of(FACTURA_PATH);
        assertThat(Files.exists(facturasDir)).isTrue();

        List<Path> pdfFiles = Files.list(facturasDir)
                .filter(p -> p.toString().endsWith(".pdf"))
                .toList();

        assertThat(pdfFiles).isNotEmpty();

        Path pdfFile = pdfFiles.get(0);
        assertThat(Files.size(pdfFile)).isGreaterThan(0);
    }

    @Test
    void shouldCreateDirectoryWhenItDoesNotExist() throws IOException {
        // Arrange
        deleteFacturasPath();

        Producto producto = new Producto("plato fuerte", 1, 10.0);
        Factura factura = new Factura("Kelvin", List.of(producto), 10.0, "PDF");

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
        Path facturasPath = Path.of("/tmp/facturas/");
        Files.writeString(facturasPath, "not-a-directory");

        Producto producto = new Producto("plato fuerte", 1, 10.0);
        Factura factura = new Factura("Kelvin", List.of(producto), 10.0, "PDF");

        // Act & Assert
        assertThatThrownBy(() -> generator.generar(factura))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error al generar la factura PDF")
                .hasCauseInstanceOf(IOException.class);

        deleteFacturasPath();
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

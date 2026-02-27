package com.foodtech.ms_factura.infrastructure.adapters.output.file;

import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileTxtFacturaGeneratorTest {

    private final FileTxtFacturaGenerator generator = new FileTxtFacturaGenerator();

    @Test
    void testGenerar() throws IOException {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "TXT");

        generator.generar(factura);

        Path facturasDir = Path.of("/tmp/facturas/");
        assertThat(Files.exists(facturasDir)).isTrue();

        List<Path> txtFiles = Files.list(facturasDir)
                .filter(p -> p.toString().endsWith(".txt"))
                .toList();

        assertThat(txtFiles).isNotEmpty();

        String content = Files.readString(txtFiles.get(0));
        assertThat(content).contains("Factura para: Kelvin");
        assertThat(content).contains("plato fuerte");
        assertThat(content).contains("plato entrada");
        assertThat(content).containsPattern("Total:\\s*15[\\.,]00");
    }

    @Test
    void shouldCreateDirectoryWhenItDoesNotExist() throws IOException {
        // Arrange
        deleteFacturasPath();

        Producto producto = new Producto("plato fuerte", 1, 10.0);
        Factura factura = new Factura("Kelvin", List.of(producto), 10.0, "TXT");

        // Act
        Path generatedFile = generator.generar(factura);

        // Assert
        assertThat(Files.exists(Path.of("/tmp/facturas/"))).isTrue();
        assertThat(generatedFile).exists();
    }

    @Test
    void shouldThrowRuntimeExceptionWhenFacturaPathIsAFile() throws IOException {
        // Arrange
        deleteFacturasPath();
        Path facturasPath = Path.of("/tmp/facturas/");
        Files.writeString(facturasPath, "not-a-directory");

        Producto producto = new Producto("plato fuerte", 1, 10.0);
        Factura factura = new Factura("Kelvin", List.of(producto), 10.0, "TXT");

        // Act & Assert
        assertThatThrownBy(() -> generator.generar(factura))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error al generar la factura")
                .hasCauseInstanceOf(IOException.class);

        deleteFacturasPath();
    }

    private void deleteFacturasPath() throws IOException {
        Path facturasPath = Path.of("/tmp/facturas/");
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

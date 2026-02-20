package com.foodtech.ms_factura.infrastructure;

import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FilePdfFacturaGeneratorTest {

    private final FilePdfFacturaGenerator generator = new FilePdfFacturaGenerator();

    @Test
    void testGenerar() throws IOException {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "PDF");

        // Ejecutar
        generator.generar(factura);

        // Verificar que se creó un archivo en /tmp/facturas/
        Path facturasDir = Path.of("/tmp/facturas/");
        assertThat(Files.exists(facturasDir)).isTrue();

        // Buscar archivos PDF
        List<Path> pdfFiles = Files.list(facturasDir)
                .filter(p -> p.toString().endsWith(".pdf"))
                .toList();

        assertThat(pdfFiles).isNotEmpty();

        // Verificar que el archivo tiene contenido (tamaño > 0)
        Path pdfFile = pdfFiles.get(0);
        assertThat(Files.size(pdfFile)).isGreaterThan(0);
    }
}
package com.foodtech.ms_factura.application;

import com.foodtech.ms_factura.application.ports.output.PdfFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.TxtFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.XlsxFacturaGeneratorPort;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class GenerarFacturaServiceTest {

    @Mock
    private TxtFacturaGeneratorPort txtFacturaGeneratorPort;

    @Mock
    private PdfFacturaGeneratorPort pdfFacturaGeneratorPort;

    @Mock
    private XlsxFacturaGeneratorPort xlsxFacturaGeneratorPort;

    @InjectMocks
    private GenerarFacturaService generarFacturaService;

    @Test
    void testGenerarFacturaTxt() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "TXT");

        generarFacturaService.generarFactura(factura);

        verify(txtFacturaGeneratorPort).generar(factura);
        verifyNoInteractions(pdfFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaPdf() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "PDF");

        generarFacturaService.generarFactura(factura);

        verify(pdfFacturaGeneratorPort).generar(factura);
        verifyNoInteractions(txtFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaXlsx() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "XLSX");

        generarFacturaService.generarFactura(factura);

        verify(xlsxFacturaGeneratorPort).generar(factura);
        verifyNoInteractions(txtFacturaGeneratorPort, pdfFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaDefaultTxt() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, null);

        generarFacturaService.generarFactura(factura);

        verify(txtFacturaGeneratorPort).generar(factura);
        verifyNoInteractions(pdfFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaUnknownFormatFallbackTxt() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "CSV");

        generarFacturaService.generarFactura(factura);

        verify(txtFacturaGeneratorPort).generar(factura);
        verifyNoInteractions(pdfFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }
}
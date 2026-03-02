package com.foodtech.ms_factura.application;

import com.foodtech.ms_factura.application.ports.output.PdfFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.TxtFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.XlsxFacturaGeneratorPort;
import com.foodtech.ms_factura.application.notification.FacturaGeneradaEvent;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import org.springframework.context.ApplicationEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarFacturaServiceTest {

    @Mock
    private TxtFacturaGeneratorPort txtFacturaGeneratorPort;

    @Mock
    private PdfFacturaGeneratorPort pdfFacturaGeneratorPort;

    @Mock
    private XlsxFacturaGeneratorPort xlsxFacturaGeneratorPort;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private GenerarFacturaService generarFacturaService;

    @Test
    void testGenerarFacturaTxt() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "TXT");
        when(txtFacturaGeneratorPort.generar(factura)).thenReturn(Path.of("/tmp/facturas/factura_test.txt"));

        generarFacturaService.generarFactura(factura);

        verify(txtFacturaGeneratorPort).generar(factura);
        verify(applicationEventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(pdfFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaPdf() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "PDF");
        when(pdfFacturaGeneratorPort.generar(factura)).thenReturn(Path.of("/tmp/facturas/factura_test.pdf"));

        generarFacturaService.generarFactura(factura);

        verify(pdfFacturaGeneratorPort).generar(factura);
        verify(applicationEventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(txtFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaXlsx() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "XLSX");
        when(xlsxFacturaGeneratorPort.generar(factura)).thenReturn(Path.of("/tmp/facturas/factura_test.xlsx"));

        generarFacturaService.generarFactura(factura);

        verify(xlsxFacturaGeneratorPort).generar(factura);
        verify(applicationEventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(txtFacturaGeneratorPort, pdfFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaDefaultTxt() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, null);
        when(txtFacturaGeneratorPort.generar(factura)).thenReturn(Path.of("/tmp/facturas/factura_test.txt"));

        generarFacturaService.generarFactura(factura);

        verify(txtFacturaGeneratorPort).generar(factura);
        verify(applicationEventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(pdfFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaUnknownFormatFallbackTxt() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "CSV");
        when(txtFacturaGeneratorPort.generar(factura)).thenReturn(Path.of("/tmp/facturas/factura_test.txt"));

        generarFacturaService.generarFactura(factura);

        verify(txtFacturaGeneratorPort).generar(factura);
        verify(applicationEventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(pdfFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }

    @Test
    void testGenerarFacturaIncorrectTypeFallbackTxt() {
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "INVALID_TYPE");
        when(txtFacturaGeneratorPort.generar(factura)).thenReturn(Path.of("/tmp/facturas/factura_test.txt"));

        generarFacturaService.generarFactura(factura);

        verify(txtFacturaGeneratorPort).generar(factura);
        verify(applicationEventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(pdfFacturaGeneratorPort, xlsxFacturaGeneratorPort);
    }
}
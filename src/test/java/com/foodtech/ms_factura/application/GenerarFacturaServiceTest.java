package com.foodtech.ms_factura.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.foodtech.ms_factura.application.notification.FacturaGeneradaEvent;
import com.foodtech.ms_factura.application.ports.output.PdfFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.TxtFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.XlsxFacturaGeneratorPort;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "PMD.AtLeastOneConstructor", "PMD.LawOfDemeter" })
@Tag("unit")
class GenerarFacturaServiceTest {

    private static final String MAIN_DISH = "plato fuerte";
    private static final String STARTER_DISH = "plato entrada";
    private static final String CUSTOMER_NAME = "Kelvin";
    private static final String FORMAT_TXT = "TXT";
    private static final String FORMAT_PDF = "PDF";
    private static final String FORMAT_XLSX = "XLSX";
    private static final String FORMAT_CSV = "CSV";

    private static final Path TXT_PATH = Path.of("/tmp/facturas/factura_test.txt");
    private static final Path PDF_PATH = Path.of("/tmp/facturas/factura_test.pdf");
    private static final Path XLSX_PATH = Path.of("/tmp/facturas/factura_test.xlsx");

    @Mock
    private TxtFacturaGeneratorPort txtPort;

    @Mock
    private PdfFacturaGeneratorPort pdfPort;

    @Mock
    private XlsxFacturaGeneratorPort xlsxPort;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private GenerarFacturaService service;

    // Validar que se genera factura en txt por defecto y se publica evento
    @Test
    void testGenerarFacturaTxt() {
        Producto producto1 = new Producto(MAIN_DISH, 1, 10.0);
        Producto producto2 = new Producto(STARTER_DISH, 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura(CUSTOMER_NAME, productos, 15.0, FORMAT_TXT);
        when(txtPort.generar(factura)).thenReturn(TXT_PATH);

        service.generarFactura(factura);

        verify(txtPort).generar(factura);
        verify(eventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(pdfPort, xlsxPort);
    }

    // Validar que si se pasa formato PDF, se llama al puerto correspondiente
    @Test
    void testGenerarFacturaPdf() {
        Producto producto1 = new Producto(MAIN_DISH, 1, 10.0);
        Producto producto2 = new Producto(STARTER_DISH, 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura(CUSTOMER_NAME, productos, 15.0, FORMAT_PDF);
        when(pdfPort.generar(factura)).thenReturn(PDF_PATH);

        service.generarFactura(factura);

        verify(pdfPort).generar(factura);
        verify(eventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(txtPort, xlsxPort);
    }

    // Validar que si se pasa formato XLSX, se llama al generador correspondiente
    @Test
    void testGenerarFacturaXlsx() {
        Producto producto1 = new Producto(MAIN_DISH, 1, 10.0);
        Producto producto2 = new Producto(STARTER_DISH, 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura(CUSTOMER_NAME, productos, 15.0, FORMAT_XLSX);
        when(xlsxPort.generar(factura)).thenReturn(XLSX_PATH);

        service.generarFactura(factura);

        verify(xlsxPort).generar(factura);
        verify(eventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(txtPort, pdfPort);
    }

    // Validar que si no se especifica formato, se genera en txt por defecto
    @Test
    void testGenerarFacturaDefaultTxt() {
        Producto producto1 = new Producto(MAIN_DISH, 1, 10.0);
        Producto producto2 = new Producto(STARTER_DISH, 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura(CUSTOMER_NAME, productos, 15.0, null);
        when(txtPort.generar(factura)).thenReturn(TXT_PATH);

        service.generarFactura(factura);

        verify(txtPort).generar(factura);
        verify(eventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(pdfPort, xlsxPort);
    }

    // Validar que si se pasa un formato desconocido, se cae al txt por defecto
    @Test
    void testGenerarFacturaUnknownFormatFallbackTxt() {
        Producto producto1 = new Producto(MAIN_DISH, 1, 10.0);
        Producto producto2 = new Producto(STARTER_DISH, 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura(CUSTOMER_NAME, productos, 15.0, FORMAT_CSV);
        when(txtPort.generar(factura)).thenReturn(TXT_PATH);

        service.generarFactura(factura);

        verify(txtPort).generar(factura);
        verify(eventPublisher).publishEvent(any(FacturaGeneradaEvent.class));
        verifyNoInteractions(pdfPort, xlsxPort);
    }
}
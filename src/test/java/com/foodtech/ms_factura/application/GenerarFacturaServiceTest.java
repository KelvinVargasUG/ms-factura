package com.foodtech.ms_factura.application;

import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import com.foodtech.ms_factura.infrastructure.FilePdfFacturaGenerator;
import com.foodtech.ms_factura.infrastructure.FileTxtFacturaGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GenerarFacturaServiceTest {

    @Mock
    private FileTxtFacturaGenerator fileTxtFacturaGenerator;

    @Mock
    private FilePdfFacturaGenerator filePdfFacturaGenerator;

    @InjectMocks
    private GenerarFacturaService generarFacturaService;

    @Test
    void testGenerarFacturaTxt() {
        // Datos de prueba
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "TXT");

        // Ejecutar
        generarFacturaService.generarFactura(factura);

        // Verificar que se llamó al TXT generator
        verify(fileTxtFacturaGenerator).generar(factura);
    }

    @Test
    void testGenerarFacturaPdf() {
        // Datos de prueba
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "PDF");

        // Ejecutar
        generarFacturaService.generarFactura(factura);

        // Verificar que se llamó al PDF generator
        verify(filePdfFacturaGenerator).generar(factura);
    }

    @Test
    void testGenerarFacturaDefaultTxt() {
        // Datos de prueba sin formato
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, null);

        // Ejecutar
        generarFacturaService.generarFactura(factura);

        // Verificar que se llamó al TXT generator por defecto
        verify(fileTxtFacturaGenerator).generar(factura);
    }
}
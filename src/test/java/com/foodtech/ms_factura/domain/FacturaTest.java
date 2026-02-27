package com.foodtech.ms_factura.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FacturaTest {

    @Test
    void shouldInitializeWithDefaultConstructor() {
        // Arrange & Act
        Factura factura = new Factura();

        // Assert
        assertNull(factura.getNombreCliente());
        assertNull(factura.getEmailCliente());
        assertNull(factura.getListaProductos());
        assertEquals(0.0, factura.getTotal());
        assertNull(factura.getFormato());
    }

    @Test
    void shouldInitializeWithAllArgsConstructor() {
        // Arrange
        List<Producto> productos = List.of(
                new Producto("Arroz", 10, 2.50),
                new Producto("Pasta", 5, 3.00)
        );

        // Act
        Factura factura = new Factura("Juan Pérez", productos, 50.50, "PDF");

        // Assert
        assertEquals("Juan Pérez", factura.getNombreCliente());
        assertEquals(productos, factura.getListaProductos());
        assertEquals(50.50, factura.getTotal());
        assertEquals("PDF", factura.getFormato());
    }

    @Test
    void shouldSetAndGetNombreCliente() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setNombreCliente("Carlos López");

        // Assert
        assertEquals("Carlos López", factura.getNombreCliente());
    }

    @Test
    void shouldSetAndGetEmailCliente() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setEmailCliente("carlos@example.com");

        // Assert
        assertEquals("carlos@example.com", factura.getEmailCliente());
    }

    @Test
    void shouldSetAndGetListaProductos() {
        // Arrange
        Factura factura = new Factura();
        List<Producto> productos = List.of(
                new Producto("Tomate", 20, 1.50),
                new Producto("Cebolla", 15, 0.75)
        );

        // Act
        factura.setListaProductos(productos);

        // Assert
        assertEquals(productos, factura.getListaProductos());
        assertEquals(2, factura.getListaProductos().size());
    }

    @Test
    void shouldSetAndGetTotal() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setTotal(150.75);

        // Assert
        assertEquals(150.75, factura.getTotal());
    }

    @Test
    void shouldSetAndGetFormato() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setFormato("XLSX");

        // Assert
        assertEquals("XLSX", factura.getFormato());
    }

    @Test
    void shouldHandleNullValues() {
        // Arrange
        Factura factura = new Factura("Test", List.of(), 100.0, "TXT");

        // Act
        factura.setNombreCliente(null);
        factura.setEmailCliente(null);
        factura.setListaProductos(null);
        factura.setFormato(null);

        // Assert
        assertNull(factura.getNombreCliente());
        assertNull(factura.getEmailCliente());
        assertNull(factura.getListaProductos());
        assertNull(factura.getFormato());
    }

    @Test
    void shouldHandleEmptyListaProductos() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setListaProductos(List.of());

        // Assert
        assertEquals(0, factura.getListaProductos().size());
    }

    @Test
    void shouldHandleZeroTotal() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setTotal(0.0);

        // Assert
        assertEquals(0.0, factura.getTotal());
    }

    @Test
    void shouldHandleNegativeTotal() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setTotal(-50.00);

        // Assert
        assertEquals(-50.00, factura.getTotal());
    }

    @Test
    void shouldHandleLargeTotal() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setTotal(999999.99);

        // Assert
        assertEquals(999999.99, factura.getTotal());
    }

    @Test
    void shouldHandleMultipleProductos() {
        // Arrange
        Factura factura = new Factura();
        List<Producto> productos = List.of(
                new Producto("Ajo", 3, 0.50),
                new Producto("Pimienta", 2, 2.00),
                new Producto("Sal", 5, 1.00)
        );

        // Act
        factura.setListaProductos(productos);

        // Assert
        assertEquals(3, factura.getListaProductos().size());
    }

    @Test
    void shouldUpdateMultipleFields() {
        // Arrange
        Factura factura = new Factura();

        // Act
        factura.setNombreCliente("María Rodríguez");
        factura.setEmailCliente("maria@example.com");
        factura.setTotal(250.00);
        factura.setFormato("PDF");

        // Assert
        assertEquals("María Rodríguez", factura.getNombreCliente());
        assertEquals("maria@example.com", factura.getEmailCliente());
        assertEquals(250.00, factura.getTotal());
        assertEquals("PDF", factura.getFormato());
    }

    @Test
    void shouldOverwriteListaProductos() {
        // Arrange
        List<Producto> productos1 = List.of(new Producto("Produto1", 1, 1.0));
        List<Producto> productos2 = List.of(new Producto("Produto2", 2, 2.0));
        Factura factura = new Factura();

        // Act
        factura.setListaProductos(productos1);
        factura.setListaProductos(productos2);

        // Assert
        assertEquals(1, factura.getListaProductos().size());
        assertEquals("Produto2", factura.getListaProductos().get(0).getNombre());
    }

    @Test
    void shouldConstructorNotSetEmailCliente() {
        // Arrange
        List<Producto> productos = List.of();

        // Act
        Factura factura = new Factura("Cliente", productos, 100.0, "PDF");

        // Assert
        assertNull(factura.getEmailCliente());
    }
}

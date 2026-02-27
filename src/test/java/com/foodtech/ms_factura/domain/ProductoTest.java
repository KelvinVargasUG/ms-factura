package com.foodtech.ms_factura.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ProductoTest {

    @Test
    void shouldInitializeWithDefaultConstructor() {
        // Arrange & Act
        Producto producto = new Producto();

        // Assert
        assertNull(producto.getNombre());
        assertEquals(0, producto.getCantidad());
        assertEquals(0.0, producto.getPrecio());
    }

    @Test
    void shouldInitializeWithAllArgsConstructor() {
        // Arrange & Act
        Producto producto = new Producto("Pasta", 10, 5.50);

        // Assert
        assertEquals("Pasta", producto.getNombre());
        assertEquals(10, producto.getCantidad());
        assertEquals(5.50, producto.getPrecio());
    }

    @Test
    void shouldSetAndGetNombre() {
        // Arrange
        Producto producto = new Producto();

        // Act
        producto.setNombre("Arroz");

        // Assert
        assertEquals("Arroz", producto.getNombre());
    }

    @Test
    void shouldSetAndGetCantidad() {
        // Arrange
        Producto producto = new Producto();

        // Act
        producto.setCantidad(25);

        // Assert
        assertEquals(25, producto.getCantidad());
    }

    @Test
    void shouldSetAndGetPrecio() {
        // Arrange
        Producto producto = new Producto();

        // Act
        producto.setPrecio(9.99);

        // Assert
        assertEquals(9.99, producto.getPrecio());
    }

    @Test
    void shouldHandleNullNombre() {
        // Arrange
        Producto producto = new Producto("Papa", 5, 2.00);

        // Act
        producto.setNombre(null);

        // Assert
        assertNull(producto.getNombre());
    }

    @Test
    void shouldHandleZeroCantidad() {
        // Arrange
        Producto producto = new Producto();

        // Act
        producto.setCantidad(0);

        // Assert
        assertEquals(0, producto.getCantidad());
    }

    @Test
    void shouldHandleNegativeCantidad() {
        // Arrange
        Producto producto = new Producto();

        // Act
        producto.setCantidad(-5);

        // Assert
        assertEquals(-5, producto.getCantidad());
    }

    @Test
    void shouldHandleZeroPrecio() {
        // Arrange
        Producto producto = new Producto();

        // Act
        producto.setPrecio(0.0);

        // Assert
        assertEquals(0.0, producto.getPrecio());
    }

    @Test
    void shouldHandleNegativePrecio() {
        // Arrange
        Producto producto = new Producto();

        // Act
        producto.setPrecio(-10.50);

        // Assert
        assertEquals(-10.50, producto.getPrecio());
    }

    @Test
    void shouldHandleLargePrecio() {
        // Arrange
        Producto producto = new Producto();

        // Act
        producto.setPrecio(99999.99);

        // Assert
        assertEquals(99999.99, producto.getPrecio());
    }

    @Test
    void shouldUpdateMultipleFields() {
        // Arrange
        Producto producto = new Producto("Pollo", 20, 15.00);

        // Act
        producto.setNombre("Carne");
        producto.setCantidad(50);
        producto.setPrecio(25.75);

        // Assert
        assertEquals("Carne", producto.getNombre());
        assertEquals(50, producto.getCantidad());
        assertEquals(25.75, producto.getPrecio());
    }
}

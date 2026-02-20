package com.foodtech.ms_factura;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.ms_factura.application.GenerarFacturaUseCase;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.Producto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GenerarFacturaUseCase generarFacturaUseCase;

    @InjectMocks
    private KafkaConsumer kafkaConsumer;

    @Test
    void testConsumeSuccess() throws Exception {
        // Datos de prueba
        String jsonMessage = "{\"nombreCliente\":\"Kelvin\",\"listaProductos\":[{\"nombre\":\"plato fuerte\",\"cantidad\":1,\"precio\":10.0},{\"nombre\":\"plato entrada\",\"cantidad\":1,\"precio\":5.0}],\"total\":15.0,\"formato\":\"PDF\"}";
        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);
        Factura factura = new Factura("Kelvin", productos, 15.0, "PDF");

        when(objectMapper.readValue(jsonMessage, Factura.class)).thenReturn(factura);

        // Ejecutar
        String response = kafkaConsumer.consume(jsonMessage);

        // Verificar
        verify(generarFacturaUseCase).generarFactura(factura);
        assertThat(response).isEqualTo("Factura procesada exitosamente para: Kelvin");
    }

    @Test
    void testConsumeJsonProcessingException() throws Exception {
        String invalidJson = "invalid json";

        when(objectMapper.readValue(invalidJson, Factura.class)).thenThrow(new com.fasterxml.jackson.core.JsonProcessingException("Invalid JSON") {});

        String response = kafkaConsumer.consume(invalidJson);

        assertThat(response).contains("Error al deserializar el mensaje JSON");
    }

    @Test
    void testConsumeGeneralException() throws Exception {
        String jsonMessage = "{\"nombreCliente\":\"Kelvin\",\"listaProductos\":[],\"total\":0.0,\"formato\":\"TXT\"}";
        Factura factura = new Factura("Kelvin", Arrays.asList(), 0.0, "TXT");

        when(objectMapper.readValue(jsonMessage, Factura.class)).thenReturn(factura);
        doThrow(new RuntimeException("Error")).when(generarFacturaUseCase).generarFactura(any(Factura.class));

        String response = kafkaConsumer.consume(jsonMessage);

        assertThat(response).contains("Error al generar la factura");
    }
}
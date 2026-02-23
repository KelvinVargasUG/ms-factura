package com.foodtech.ms_factura.infrastructure.adapters.input.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.ms_factura.application.ports.input.GenerarFacturaUseCase;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.FoodEvent;
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
class KafkaConsumerAdapterTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GenerarFacturaUseCase generarFacturaUseCase;

    @InjectMocks
    private KafkaConsumerAdapter kafkaConsumer;

    @Test
    void testConsumeSuccess() throws Exception {
        String jsonMessage = "{\"eventId\":\"e-1\",\"eventType\":\"CREATE_FACTURA\",\"payload\":\"{\\\"nombreCliente\\\":\\\"Kelvin\\\",\\\"listaProductos\\\":[{\\\"nombre\\\":\\\"plato fuerte\\\",\\\"cantidad\\\":1,\\\"precio\\\":10.0}],\\\"total\\\":10.0,\\\"formato\\\":\\\"PDF\\\"}\"}";
        String payloadJson = "{\"nombreCliente\":\"Kelvin\",\"listaProductos\":[{\"nombre\":\"plato fuerte\",\"cantidad\":1,\"precio\":10.0},{\"nombre\":\"plato entrada\",\"cantidad\":1,\"precio\":5.0}],\"total\":15.0,\"formato\":\"PDF\"}";

        Producto producto1 = new Producto("plato fuerte", 1, 10.0);
        Producto producto2 = new Producto("plato entrada", 1, 5.0);
        List<Producto> productos = Arrays.asList(producto1, producto2);

        FoodEvent foodEvent = new FoodEvent();
        foodEvent.setEventId("e-1");
        foodEvent.setEventType("CREATE_FACTURA");
        foodEvent.setPayload(payloadJson);

        Factura factura = new Factura("Kelvin", productos, 15.0, "PDF");

        when(objectMapper.readValue(jsonMessage, FoodEvent.class)).thenReturn(foodEvent);
        when(objectMapper.readValue(payloadJson, Factura.class)).thenReturn(factura);

        String response = kafkaConsumer.consume(jsonMessage);

        verify(generarFacturaUseCase).generarFactura(factura);
        assertThat(response).isEqualTo("Factura procesada exitosamente para: Kelvin");
    }

    @Test
    void testConsumeJsonProcessingException() throws Exception {
        String invalidJson = "invalid json";

        when(objectMapper.readValue(invalidJson, FoodEvent.class)).thenThrow(new JsonProcessingException("Invalid JSON") {});

        String response = kafkaConsumer.consume(invalidJson);

        assertThat(response).contains("Error al procesar la factura");
    }

    @Test
    void testConsumeGeneralException() throws Exception {
        String jsonMessage = "{\"eventId\":\"e-2\",\"eventType\":\"CREATE_FACTURA\",\"payload\":\"{\\\"nombreCliente\\\":\\\"Kelvin\\\",\\\"listaProductos\\\":[],\\\"total\\\":0.0,\\\"formato\\\":\\\"TXT\\\"}\"}";
        String payloadJson = "{\"nombreCliente\":\"Kelvin\",\"listaProductos\":[],\"total\":0.0,\"formato\":\"TXT\"}";

        FoodEvent foodEvent = new FoodEvent();
        foodEvent.setPayload(payloadJson);

        Factura factura = new Factura("Kelvin", Arrays.asList(), 0.0, "TXT");

        when(objectMapper.readValue(jsonMessage, FoodEvent.class)).thenReturn(foodEvent);
        when(objectMapper.readValue(payloadJson, Factura.class)).thenReturn(factura);
        doThrow(new RuntimeException("Error")).when(generarFacturaUseCase).generarFactura(any(Factura.class));

        String response = kafkaConsumer.consume(jsonMessage);

        assertThat(response).contains("Error al generar la factura");
    }
}

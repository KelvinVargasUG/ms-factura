package com.foodtech.ms_factura.infrastructure.adapters.input.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.ms_factura.application.ports.input.GenerarFacturaUseCase;
import com.foodtech.ms_factura.domain.Factura;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerAdapterTest {

    @Mock
    private GenerarFacturaUseCase generarFacturaUseCase;

    private KafkaConsumerAdapter kafkaConsumer;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        kafkaConsumer = new KafkaConsumerAdapter(objectMapper, generarFacturaUseCase);
    }

    @Test
    void testConsumeSuccess() throws Exception {
        String jsonMessage = "{\"eventId\":\"e-1\",\"eventType\":\"CREATE_FACTURA\",\"payload\":\"{\\\"nombreCliente\\\":\\\"Kelvin\\\",\\\"listaProductos\\\":[{\\\"nombre\\\":\\\"plato fuerte\\\",\\\"cantidad\\\":1,\\\"precio\\\":10.0},{\\\"nombre\\\":\\\"plato entrada\\\",\\\"cantidad\\\":1,\\\"precio\\\":5.0}],\\\"total\\\":15.0,\\\"formato\\\":\\\"PDF\\\"}\",\"timestamp\":\"2026-02-23T00:00:00\"}";

        String response = kafkaConsumer.consume(jsonMessage);

        verify(generarFacturaUseCase).generarFactura(any(Factura.class));
        assertThat(response).isEqualTo("Factura procesada exitosamente para: Kelvin");
    }

    @Test
    void testConsumeJsonProcessingException() throws Exception {
        String invalidJson = "invalid json";

        String response = kafkaConsumer.consume(invalidJson);

        assertThat(response).contains("Error al procesar la factura");
    }

    @Test
    void testConsumeGeneralException() throws Exception {
        String jsonMessage = "{\"eventId\":\"e-2\",\"eventType\":\"CREATE_FACTURA\",\"payload\":\"{\\\"nombreCliente\\\":\\\"Kelvin\\\",\\\"listaProductos\\\":[],\\\"total\\\":0.0,\\\"formato\\\":\\\"TXT\\\"}\",\"timestamp\":\"2026-02-23T00:00:00\"}";
        doThrow(new RuntimeException("Error")).when(generarFacturaUseCase).generarFactura(any(Factura.class));

        String response = kafkaConsumer.consume(jsonMessage);

        assertThat(response).contains("Error al generar la factura");
    }
}

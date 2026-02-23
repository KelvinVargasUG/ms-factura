package com.foodtech.ms_factura.infrastructure.adapters.input.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.ms_factura.application.ports.input.GenerarFacturaUseCase;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.FoodEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class RabbitMqConsumerTest {

    @Mock
    private GenerarFacturaUseCase generarFacturaUseCase;

    private RabbitMqConsumer rabbitMqConsumer;

    @BeforeEach
    void setUp() {
        rabbitMqConsumer = new RabbitMqConsumer(new ObjectMapper(), generarFacturaUseCase);
    }

    @Test
    void shouldMapAndDelegateWhenEventPayloadIsValid() throws Exception {
        FoodEvent event = new FoodEvent();
        event.setEventId("event-1");
        event.setPayload("{\"nombreCliente\":\"Kelvin\",\"listaProductos\":[],\"total\":10.0,\"formato\":\"TXT\"}");

        rabbitMqConsumer.consume(event);

        verify(generarFacturaUseCase).generarFactura(any(Factura.class));
    }

    @Test
    void shouldNotDelegateWhenPayloadMappingFails() throws Exception {
        FoodEvent event = new FoodEvent();
        event.setPayload("invalid-json");

        rabbitMqConsumer.consume(event);

        verifyNoInteractions(generarFacturaUseCase);
    }

    @Test
    void shouldNotBubbleExceptionWhenUseCaseFails() {
        FoodEvent event = new FoodEvent();
        event.setEventId("event-2");
        event.setPayload("{\"nombreCliente\":\"Kelvin\",\"listaProductos\":[],\"total\":10.0,\"formato\":\"TXT\"}");
        doThrow(new RuntimeException("boom")).when(generarFacturaUseCase).generarFactura(any(Factura.class));

        rabbitMqConsumer.consume(event);

        verify(generarFacturaUseCase).generarFactura(any(Factura.class));
    }
}

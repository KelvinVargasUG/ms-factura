package com.foodtech.ms_factura.infrastructure.adapters.input.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.ms_factura.application.ports.input.GenerarFacturaUseCase;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.FoodEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RabbitMqConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private GenerarFacturaUseCase generarFacturaUseCase;

    @InjectMocks
    private RabbitMqConsumer rabbitMqConsumer;

    @Test
    void shouldMapAndDelegateWhenEventPayloadIsValid() throws Exception {
        FoodEvent event = new FoodEvent();
        event.setEventId("event-1");
        event.setPayload("{\"nombreCliente\":\"Kelvin\",\"listaProductos\":[],\"total\":10.0,\"formato\":\"TXT\"}");

        Factura factura = new Factura();
        factura.setNombreCliente("Kelvin");
        factura.setFormato("TXT");

        when(objectMapper.readValue(event.getPayload(), Factura.class)).thenReturn(factura);

        rabbitMqConsumer.consume(event);

        verify(generarFacturaUseCase).generarFactura(factura);
    }

    @Test
    void shouldNotDelegateWhenPayloadMappingFails() throws Exception {
        FoodEvent event = new FoodEvent();
        event.setPayload("invalid-json");

        when(objectMapper.readValue(event.getPayload(), Factura.class)).thenThrow(new RuntimeException("parse error"));

        rabbitMqConsumer.consume(event);

        verifyNoInteractions(generarFacturaUseCase);
    }
}

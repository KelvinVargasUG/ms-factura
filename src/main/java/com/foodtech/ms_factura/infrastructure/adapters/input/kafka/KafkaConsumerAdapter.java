package com.foodtech.ms_factura.infrastructure.adapters.input.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.ms_factura.application.ports.input.GenerarFacturaUseCase;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.FoodEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerAdapter {

    private final ObjectMapper objectMapper;
    private final GenerarFacturaUseCase generarFacturaUseCase;

    @KafkaListener(topics = "create-factura", groupId = "ms-factura-group")
    public String consume(@Payload String message) {
        log.info("Mensaje recibido del tópico 'create-factura': {}", message);
        try {
            FoodEvent event = objectMapper.readValue(message, FoodEvent.class);
            log.info("Evento deserializado: eventType={}, eventId={}", event.getEventType(), event.getEventId());

            Factura factura = objectMapper.readValue(event.getPayload(), Factura.class);
            log.info("Factura deserializada: cliente={}, total={}", factura.getNombreCliente(), factura.getTotal());

            generarFacturaUseCase.generarFactura(factura);

            String response = "Factura procesada exitosamente para: " + factura.getNombreCliente();
            log.info("Enviando respuesta: {}", response);
            return response;
        } catch (JsonProcessingException e) {
            log.error("Error al deserializar el mensaje JSON", e);
            return "Error al procesar la factura: " + e.getMessage();
        } catch (Exception e) {
            log.error("Error al generar la factura", e);
            return "Error al generar la factura: " + e.getMessage();
        }
    }
}
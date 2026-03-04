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
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.AvoidCatchingGenericException"})
public class KafkaConsumerAdapter {

    private final ObjectMapper objectMapper;
    private final GenerarFacturaUseCase facturaUseCase;

    @KafkaListener(topics = "${foodtech.kafka.topic}", groupId = "${spring.kafka.consumer.group-id}")
    public String consume(@Payload String message) {
        if (log.isInfoEnabled()) {
            log.info("Mensaje recibido del tópico Kafka: {}", message);
        }
        String response;
        try {
            FoodEvent event = objectMapper.readValue(message, FoodEvent.class);
            String eventType = event.getEventType();
            String eventId = event.getEventId();
            String payload = event.getPayload();
            if (log.isInfoEnabled()) {
                log.info("Evento deserializado: eventType={}, eventId={}", eventType, eventId);
            }

            Factura factura = objectMapper.readValue(payload, Factura.class);
            String customerName = factura.getNombreCliente();
            double total = factura.getTotal();
            if (log.isInfoEnabled()) {
                log.info("Factura deserializada: cliente={}, total={}", customerName, total);
            }

            facturaUseCase.generarFactura(factura);

            response = "Factura procesada exitosamente para: " + customerName;
        } catch (JsonProcessingException jsonException) {
            log.error("Error al deserializar el mensaje JSON", jsonException);
            response = "Error al procesar la factura: " + jsonException.getMessage();
        } catch (RuntimeException runtimeException) {
            log.error("Error al generar la factura", runtimeException);
            response = "Error al generar la factura: " + runtimeException.getMessage();
        }
        if (log.isInfoEnabled()) {
            log.info("Enviando respuesta: {}", response);
        }
        return response;
    }
}
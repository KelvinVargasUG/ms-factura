package com.foodtech.ms_factura.infrastructure.adapters.input.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.ms_factura.application.ports.input.GenerarFacturaUseCase;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.FoodEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings("PMD.AvoidCatchingGenericException")
public class RabbitMqConsumer {

    private final ObjectMapper objectMapper;
    private final GenerarFacturaUseCase facturaUseCase;

    @RabbitListener(queues = "${foodtech.rabbitmq.queue}")
    public void consume(FoodEvent event) {
        if (log.isInfoEnabled()) {
            log.info("Mensaje recibido de RabbitMQ: {}", event);
        }
        try {
            if (log.isInfoEnabled()) {
                log.info("Evento deserializado: {}", event.getEventId());
            }

            Factura factura = objectMapper.readValue(event.getPayload(), Factura.class);
            facturaUseCase.generarFactura(factura);
            
            if (log.isInfoEnabled()) {
                log.info("Factura generada exitosamente para el evento: {}", event.getEventId());
            }
        } catch (JsonProcessingException exception) {
            log.error("Error al deserializar el mensaje de RabbitMQ", exception);
        } catch (RuntimeException exception) {
            log.error("Error al procesar el mensaje de RabbitMQ", exception);
        }
    }
}

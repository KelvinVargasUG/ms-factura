package com.foodtech.ms_factura.infrastructure.adapters.input.rabbitmq;

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
public class RabbitMqConsumer {

    private final ObjectMapper objectMapper;
    private final GenerarFacturaUseCase generarFacturaUseCase;

    @RabbitListener(queues = "${foodtech.rabbitmq.queue}")
    public void consume(FoodEvent event) {
        log.info("Mensaje recibido de RabbitMQ: {}", event);
        try {
            log.info("Evento deserializado: {}", event.getEventId());

            Factura factura = objectMapper.readValue(event.getPayload(), Factura.class);

            generarFacturaUseCase.generarFactura(factura);
            log.info("Factura generada exitosamente para el evento: {}", event.getEventId());

        } catch (Exception e) {
            log.error("Error al procesar el mensaje de RabbitMQ", e);
        }
    }
}

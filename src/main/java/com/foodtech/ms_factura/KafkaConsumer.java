package com.foodtech.ms_factura;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodtech.ms_factura.application.GenerarFacturaUseCase;
import com.foodtech.ms_factura.domain.Factura;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.kafka.annotation.SendTo;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final ObjectMapper objectMapper;
    private final GenerarFacturaUseCase generarFacturaUseCase;

    @KafkaListener(topics = "create-factura", groupId = "ms-factura-group")
    // @SendTo("factura-response")
    public String consume(@Payload String message) {
        log.info("Mensaje recibido del tópico 'create-factura': {}", message);
        try {
            // Deserializar el JSON a Factura
            Factura factura = objectMapper.readValue(message, Factura.class);

            // Generar la factura
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
package com.foodtech.ms_factura.application.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
@Slf4j
    @SuppressWarnings({"PMD.LawOfDemeter", "PMD.CyclomaticComplexity"})
public class FacturaGeneradaEventListener {

    private final NotificationDispatchService dispatchService;

    @Value("${notification.email.subject-template:Factura generada {invoiceRef}}")
    private String subjectTemplate;

    @Value("${notification.email.body-template:Factura {invoiceRef} generada en fecha {date} para cliente {customer}.}")
    private String bodyTemplate;

    @Async("notificationTaskExecutor")
    @EventListener
    public void onFacturaGenerada(FacturaGeneradaEvent event) {
        String invoiceRef = event.invoiceRef();
        var factura = event.factura();
        String recipient = factura == null ? null : factura.getEmailCliente();
        if (recipient == null || recipient.isBlank()) {
            if (log.isWarnEnabled()) {
                log.warn("No se envía notificación: emailCliente no configurado en payload invoiceRef={}", invoiceRef);
            }
            return;
        }

        var attachments = event.attachments();
        NotificationMessage message = new NotificationMessage(
                recipient,
                render(subjectTemplate, event),
                render(bodyTemplate, event),
            invoiceRef,
            attachments
        );

        dispatchService.dispatch(message);
    }

    private String render(String template, FacturaGeneradaEvent event) {
        String invoiceRef = event.invoiceRef();
        var factura = event.factura();
        var generatedAt = event.generatedAt();
        String generatedDate = generatedAt == null
            ? ""
            : generatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String customer = factura == null || factura.getNombreCliente() == null
            ? ""
            : factura.getNombreCliente();

        String result = template;
        result = result.replace("{invoiceRef}", invoiceRef == null ? "" : invoiceRef);
        result = result.replace("{date}", generatedDate);
        result = result.replace("{customer}", customer);
        return result;
    }
}

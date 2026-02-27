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
public class FacturaGeneradaEventListener {

    private final NotificationDispatchService notificationDispatchService;

    @Value("${notification.email.subject-template:Factura generada {invoiceRef}}")
    private String subjectTemplate;

    @Value("${notification.email.body-template:Factura {invoiceRef} generada en fecha {date} para cliente {customer}.}")
    private String bodyTemplate;

    @Async("notificationTaskExecutor")
    @EventListener
    public void onFacturaGenerada(FacturaGeneradaEvent event) {
        String recipient = event.factura() == null ? null : event.factura().getEmailCliente();
        if (recipient == null || recipient.isBlank()) {
            log.warn("No se envía notificación: emailCliente no configurado en payload invoiceRef={}", event.invoiceRef());
            return;
        }

        NotificationMessage message = new NotificationMessage(
                recipient,
                render(subjectTemplate, event),
                render(bodyTemplate, event),
                event.invoiceRef(),
                event.attachments()
        );

        notificationDispatchService.dispatch(message);
    }

    private String render(String template, FacturaGeneradaEvent event) {
        String generatedDate = event.generatedAt() == null
                ? ""
                : event.generatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String customer = event.factura() == null || event.factura().getNombreCliente() == null
                ? ""
                : event.factura().getNombreCliente();

        return template
                .replace("{invoiceRef}", event.invoiceRef() == null ? "" : event.invoiceRef())
                .replace("{date}", generatedDate)
                .replace("{customer}", customer);
    }
}

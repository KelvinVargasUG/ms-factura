package com.foodtech.ms_factura.application.notification;

import java.nio.file.Path;
import java.util.List;

public record NotificationMessage(
        String destinatario,
        String asunto,
        String cuerpo,
        String invoiceRef,
        List<Path> attachments
) {
}

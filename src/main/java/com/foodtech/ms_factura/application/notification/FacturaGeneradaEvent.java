package com.foodtech.ms_factura.application.notification;

import com.foodtech.ms_factura.domain.Factura;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public record FacturaGeneradaEvent(
        String invoiceRef,
        Factura factura,
        List<Path> attachments,
        LocalDateTime generatedAt
) {
}

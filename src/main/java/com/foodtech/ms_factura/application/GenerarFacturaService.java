package com.foodtech.ms_factura.application;

import com.foodtech.ms_factura.application.ports.input.GenerarFacturaUseCase;
import com.foodtech.ms_factura.application.ports.output.FacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.PdfFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.TxtFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.XlsxFacturaGeneratorPort;
import com.foodtech.ms_factura.application.notification.FacturaGeneradaEvent;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.FacturaFormatoPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("PMD.LawOfDemeter")
public class GenerarFacturaService implements GenerarFacturaUseCase {

    private final PdfFacturaGeneratorPort pdfGeneratorPort;
    private final TxtFacturaGeneratorPort txtGeneratorPort;
    private final XlsxFacturaGeneratorPort xlsxGeneratorPort;
    private final ApplicationEventPublisher eventPublisher;
    private final FacturaFormatoPolicy formatoPolicy = new FacturaFormatoPolicy();

    @Override
    public void generarFactura(Factura factura) {
        FacturaGeneratorPort generatorPort = selectGeneratorPort(factura);
        Path generatedPath = generatorPort.generar(factura);
        publishGenerationEvent(factura, generatedPath);
    }

    private FacturaGeneratorPort selectGeneratorPort(Factura factura) {
        FacturaGeneratorPort selected = txtGeneratorPort;
        if (formatoPolicy.isPdf(factura)) {
            selected = pdfGeneratorPort;
        } else if (formatoPolicy.isXlsx(factura)) {
            selected = xlsxGeneratorPort;
        }
        return selected;
    }

    private void publishGenerationEvent(Factura factura, Path generatedPath) {
        String invoiceRef = UUID.randomUUID().toString();
        List<Path> attachments = List.of(generatedPath);
        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                invoiceRef,
                factura,
                attachments,
                LocalDateTime.now()
        );
        eventPublisher.publishEvent(event);
    }
}
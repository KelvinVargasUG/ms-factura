package com.foodtech.ms_factura.application;

import com.foodtech.ms_factura.application.ports.input.GenerarFacturaUseCase;
import com.foodtech.ms_factura.application.ports.output.FacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.PdfFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.TxtFacturaGeneratorPort;
import com.foodtech.ms_factura.application.ports.output.XlsxFacturaGeneratorPort;
import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.domain.FacturaFormatoPolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerarFacturaService implements GenerarFacturaUseCase {

    private final PdfFacturaGeneratorPort pdfFacturaGeneratorPort;
    private final TxtFacturaGeneratorPort txtFacturaGeneratorPort;
    private final XlsxFacturaGeneratorPort xlsxFacturaGeneratorPort;

    private final FacturaFormatoPolicy facturaFormatoPolicy = new FacturaFormatoPolicy();

    @Override
    public void generarFactura(Factura factura) {
        FacturaGeneratorPort generatorPort;
        if (facturaFormatoPolicy.isPdf(factura)) {
            generatorPort = pdfFacturaGeneratorPort;
        } else if (facturaFormatoPolicy.isXlsx(factura)) {
            generatorPort = xlsxFacturaGeneratorPort;
        } else {
            generatorPort = txtFacturaGeneratorPort;
        }

        generatorPort.generar(factura);
    }
}
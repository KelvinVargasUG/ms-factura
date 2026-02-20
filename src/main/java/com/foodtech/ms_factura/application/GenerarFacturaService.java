package com.foodtech.ms_factura.application;

import com.foodtech.ms_factura.domain.Factura;
import com.foodtech.ms_factura.infrastructure.FacturaGeneratorStrategy;
import com.foodtech.ms_factura.infrastructure.FilePdfFacturaGenerator;
import com.foodtech.ms_factura.infrastructure.FileTxtFacturaGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerarFacturaService implements GenerarFacturaUseCase {

    private final FileTxtFacturaGenerator fileTxtFacturaGenerator;
    private final FilePdfFacturaGenerator filePdfFacturaGenerator;

    @Override
    public void generarFactura(Factura factura) {
        FacturaGeneratorStrategy strategy;
        if ("PDF".equalsIgnoreCase(factura.getFormato())) {
            strategy = filePdfFacturaGenerator;
        } else {
            // Default to TXT
            strategy = fileTxtFacturaGenerator;
        }

        strategy.generar(factura);
    }
}
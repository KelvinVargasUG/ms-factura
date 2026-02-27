package com.foodtech.ms_factura.application.ports.output;

import com.foodtech.ms_factura.domain.Factura;

import java.nio.file.Path;

public interface FacturaGeneratorPort {
    Path generar(Factura factura);
}
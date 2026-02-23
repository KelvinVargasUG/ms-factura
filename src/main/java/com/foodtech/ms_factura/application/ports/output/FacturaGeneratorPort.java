package com.foodtech.ms_factura.application.ports.output;

import com.foodtech.ms_factura.domain.Factura;

public interface FacturaGeneratorPort {
    void generar(Factura factura);
}
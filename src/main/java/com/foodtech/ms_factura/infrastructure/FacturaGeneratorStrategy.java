package com.foodtech.ms_factura.infrastructure;

import com.foodtech.ms_factura.domain.Factura;

public interface FacturaGeneratorStrategy {
    void generar(Factura factura);
}
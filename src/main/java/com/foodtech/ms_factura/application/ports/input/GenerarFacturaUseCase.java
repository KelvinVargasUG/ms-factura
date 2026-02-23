package com.foodtech.ms_factura.application.ports.input;

import com.foodtech.ms_factura.domain.Factura;

public interface GenerarFacturaUseCase {
    void generarFactura(Factura factura);
}
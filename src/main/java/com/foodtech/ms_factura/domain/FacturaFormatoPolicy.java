package com.foodtech.ms_factura.domain;

@SuppressWarnings("PMD.UnnecessaryConstructor")
public class FacturaFormatoPolicy {

    public FacturaFormatoPolicy() {
    }

    public boolean isPdf(Factura factura) {
        return factura != null && "PDF".equalsIgnoreCase(factura.getFormato());
    }

    public boolean isXlsx(Factura factura) {
        return factura != null && "XLSX".equalsIgnoreCase(factura.getFormato());
    }
}
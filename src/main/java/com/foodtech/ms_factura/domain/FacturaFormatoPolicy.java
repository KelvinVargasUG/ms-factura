package com.foodtech.ms_factura.domain;

public class FacturaFormatoPolicy {

    public boolean isPdf(Factura factura) {
        return factura != null && "PDF".equalsIgnoreCase(factura.getFormato());
    }

    public boolean isXlsx(Factura factura) {
        return factura != null && "XLSX".equalsIgnoreCase(factura.getFormato());
    }
}
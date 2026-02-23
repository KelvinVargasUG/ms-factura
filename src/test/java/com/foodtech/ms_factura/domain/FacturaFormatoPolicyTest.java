package com.foodtech.ms_factura.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FacturaFormatoPolicyTest {

    private final FacturaFormatoPolicy policy = new FacturaFormatoPolicy();

    @Test
    void shouldDetectPdfCaseInsensitive() {
        Factura factura = new Factura("Cliente", List.of(), 0.0, "pdf");

        assertThat(policy.isPdf(factura)).isTrue();
    }

    @Test
    void shouldDetectXlsxCaseInsensitive() {
        Factura factura = new Factura("Cliente", List.of(), 0.0, "xlsx");

        assertThat(policy.isXlsx(factura)).isTrue();
    }

    @Test
    void shouldReturnFalseForNullOrUnknownFormat() {
        Factura facturaNull = new Factura("Cliente", List.of(), 0.0, null);
        Factura facturaTxt = new Factura("Cliente", List.of(), 0.0, "TXT");

        assertThat(policy.isPdf(facturaNull)).isFalse();
        assertThat(policy.isPdf(facturaTxt)).isFalse();
    }
}

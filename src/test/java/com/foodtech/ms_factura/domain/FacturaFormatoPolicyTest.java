package com.foodtech.ms_factura.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "PMD.AtLeastOneConstructor", "PMD.JUnitTestContainsTooManyAsserts", "PMD.LawOfDemeter" })
@Tag("unit")
class FacturaFormatoPolicyTest {

    private static final String TEST_CLIENTE = "Cliente";

    private final FacturaFormatoPolicy policy = new FacturaFormatoPolicy();

    @Test
    void shouldDetectPdfCaseInsensitive() {
        Factura factura = new Factura(TEST_CLIENTE, List.of(), 0.0, "pdf");

        assertThat(policy.isPdf(factura)).isTrue();
    }

    @Test
    void shouldDetectXlsxCaseInsensitive() {
        Factura factura = new Factura(TEST_CLIENTE, List.of(), 0.0, "xlsx");

        assertThat(policy.isXlsx(factura)).isTrue();
    }

    @Test
    void shouldReturnFalseForNullOrUnknownFormat() {
        Factura facturaNull = new Factura(TEST_CLIENTE, List.of(), 0.0, null);
        Factura facturaTxt = new Factura(TEST_CLIENTE, List.of(), 0.0, "TXT");

        assertThat(policy.isPdf(facturaNull)).isFalse();
        assertThat(policy.isPdf(facturaTxt)).isFalse();
    }
}

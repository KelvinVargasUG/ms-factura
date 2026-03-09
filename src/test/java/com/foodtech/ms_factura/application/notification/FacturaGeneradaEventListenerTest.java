package com.foodtech.ms_factura.application.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.foodtech.ms_factura.domain.Factura;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings({ "PMD.AtLeastOneConstructor", "PMD.JUnitTestContainsTooManyAsserts", "PMD.LawOfDemeter" })
@Tag("unit")
class FacturaGeneradaEventListenerTest {

    private static final String EMAIL = "cliente@example.com";
    private static final String SUBJECT_TEMPLATE = "Factura {invoiceRef}";
    private static final String BODY_TEMPLATE = "Cliente {customer} fecha {date}";

    @Mock
    private NotificationDispatchService dispatchService;

    // Validar que construye mensaje correctamente y lo despacha
    @Test
    void shouldBuildAndDispatchNotificationMessage() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(dispatchService);
        ReflectionTestUtils.setField(listener, "subjectTemplate", SUBJECT_TEMPLATE);
        ReflectionTestUtils.setField(listener, "bodyTemplate", BODY_TEMPLATE);

        Factura factura = new Factura();
        factura.setNombreCliente("Kelvin");
        factura.setEmailCliente(EMAIL);

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-123",
                factura,
                List.of(Path.of("/tmp/facturas/factura_ref-123.pdf")),
                LocalDateTime.of(2026, 2, 25, 10, 0));

        listener.onFacturaGenerada(event);

        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(dispatchService).dispatch(messageCaptor.capture());

        NotificationMessage message = messageCaptor.getValue();
        assertThat(message.destinatario()).isEqualTo(EMAIL);
        assertThat(message.asunto()).isEqualTo("Factura ref-123");
        assertThat(message.cuerpo()).contains("Kelvin");
        assertThat(message.attachments()).hasSize(1);
    }

    // Validar que si el email del cliente es null, no se despacha notificación
    @Test
    void shouldSkipDispatchWhenRecipientIsMissing() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(dispatchService);
        Factura factura = new Factura();
        factura.setEmailCliente("");

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-456",
                factura,
                List.of(),
                LocalDateTime.now());

        listener.onFacturaGenerada(event);

        verify(dispatchService, never()).dispatch(org.mockito.ArgumentMatchers.any());
    }

    // Validar que si la factura es null, no se despacha notificación
    @Test
    void shouldSkipDispatchWhenFacturaIsNull() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(dispatchService);

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-789",
                null,
                List.of(),
                LocalDateTime.now());

        listener.onFacturaGenerada(event);

        verify(dispatchService, never()).dispatch(org.mockito.ArgumentMatchers.any());
    }

    // Validar que si el destinatario es solo espacios, no se despacha notificación
    @Test
    void shouldSkipDispatchWhenRecipientIsBlank() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(dispatchService);
        Factura factura = new Factura();
        factura.setEmailCliente("   ");

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-999",
                factura,
                List.of(),
                LocalDateTime.now());

        listener.onFacturaGenerada(event);

        verify(dispatchService, never()).dispatch(org.mockito.ArgumentMatchers.any());
    }

    // Validar que si el template tiene placeholders pero no se pasan datos, se
    // renderizan con valores vacíos
    @Test
    void shouldRenderEmptyValuesWhenDataIsMissing() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(dispatchService);
        ReflectionTestUtils.setField(listener, "subjectTemplate", SUBJECT_TEMPLATE);
        ReflectionTestUtils.setField(listener, "bodyTemplate", BODY_TEMPLATE);

        Factura factura = new Factura();
        factura.setEmailCliente(EMAIL);

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                null,
                factura,
                List.of(),
                null);

        listener.onFacturaGenerada(event);

        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(dispatchService).dispatch(messageCaptor.capture());

        NotificationMessage message = messageCaptor.getValue();
        assertThat(message.asunto()).isEqualTo("Factura ");
        assertThat(message.cuerpo()).isEqualTo("Cliente  fecha ");
    }

    // Validar que si el nombre del cliente es null, se renderiza como vacío en el
    // cuerpo del mensaje
    @Test
    void shouldRenderEmptyCustomerWhenNombreClienteIsNull() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(dispatchService);
        ReflectionTestUtils.setField(listener, "subjectTemplate", SUBJECT_TEMPLATE);
        ReflectionTestUtils.setField(listener, "bodyTemplate", BODY_TEMPLATE);

        Factura factura = new Factura();
        factura.setNombreCliente(null);
        factura.setEmailCliente(EMAIL);

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-321",
                factura,
                List.of(),
                LocalDateTime.of(2026, 2, 26, 8, 30));

        listener.onFacturaGenerada(event);

        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(dispatchService).dispatch(messageCaptor.capture());

        NotificationMessage message = messageCaptor.getValue();
        assertThat(message.cuerpo()).contains("Cliente  fecha 2026-02-26T08:30:00");
    }
}

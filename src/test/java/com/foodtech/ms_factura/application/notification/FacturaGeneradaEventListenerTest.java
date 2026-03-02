package com.foodtech.ms_factura.application.notification;

import com.foodtech.ms_factura.domain.Factura;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FacturaGeneradaEventListenerTest {

    @Mock
    private NotificationDispatchService notificationDispatchService;

    @Test
    void shouldBuildAndDispatchNotificationMessage() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(notificationDispatchService);
        ReflectionTestUtils.setField(listener, "subjectTemplate", "Factura {invoiceRef}");
        ReflectionTestUtils.setField(listener, "bodyTemplate", "Cliente {customer} fecha {date}");

        Factura factura = new Factura();
        factura.setNombreCliente("Kelvin");
        factura.setEmailCliente("cliente@example.com");

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-123",
                factura,
                List.of(Path.of("/tmp/facturas/factura_ref-123.pdf")),
                LocalDateTime.of(2026, 2, 25, 10, 0)
        );

        listener.onFacturaGenerada(event);

        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationDispatchService).dispatch(messageCaptor.capture());

        NotificationMessage message = messageCaptor.getValue();
        assertThat(message.destinatario()).isEqualTo("cliente@example.com");
        assertThat(message.asunto()).isEqualTo("Factura ref-123");
        assertThat(message.cuerpo()).contains("Kelvin");
        assertThat(message.attachments()).hasSize(1);
    }

    @Test
    void shouldSkipDispatchWhenRecipientIsMissing() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(notificationDispatchService);
        Factura factura = new Factura();
        factura.setEmailCliente("");

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-456",
            factura,
                List.of(),
                LocalDateTime.now()
        );

        listener.onFacturaGenerada(event);

        verify(notificationDispatchService, never()).dispatch(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldSkipDispatchWhenFacturaIsNull() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(notificationDispatchService);

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-789",
                null,
                List.of(),
                LocalDateTime.now()
        );

        listener.onFacturaGenerada(event);

        verify(notificationDispatchService, never()).dispatch(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldSkipDispatchWhenRecipientIsBlank() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(notificationDispatchService);
        Factura factura = new Factura();
        factura.setEmailCliente("   ");

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-999",
                factura,
                List.of(),
                LocalDateTime.now()
        );

        listener.onFacturaGenerada(event);

        verify(notificationDispatchService, never()).dispatch(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldRenderEmptyValuesWhenDataIsMissing() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(notificationDispatchService);
        ReflectionTestUtils.setField(listener, "subjectTemplate", "Factura {invoiceRef}");
        ReflectionTestUtils.setField(listener, "bodyTemplate", "Cliente {customer} fecha {date}");

        Factura factura = new Factura();
        factura.setEmailCliente("cliente@example.com");

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                null,
                factura,
                List.of(),
                null
        );

        listener.onFacturaGenerada(event);

        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationDispatchService).dispatch(messageCaptor.capture());

        NotificationMessage message = messageCaptor.getValue();
        assertThat(message.asunto()).isEqualTo("Factura ");
        assertThat(message.cuerpo()).isEqualTo("Cliente  fecha ");
    }

    @Test
    void shouldRenderEmptyCustomerWhenNombreClienteIsNull() {
        FacturaGeneradaEventListener listener = new FacturaGeneradaEventListener(notificationDispatchService);
        ReflectionTestUtils.setField(listener, "subjectTemplate", "Factura {invoiceRef}");
        ReflectionTestUtils.setField(listener, "bodyTemplate", "Cliente {customer} fecha {date}");

        Factura factura = new Factura();
        factura.setNombreCliente(null);
        factura.setEmailCliente("cliente@example.com");

        FacturaGeneradaEvent event = new FacturaGeneradaEvent(
                "ref-321",
                factura,
                List.of(),
                LocalDateTime.of(2026, 2, 26, 8, 30)
        );

        listener.onFacturaGenerada(event);

        ArgumentCaptor<NotificationMessage> messageCaptor = ArgumentCaptor.forClass(NotificationMessage.class);
        verify(notificationDispatchService).dispatch(messageCaptor.capture());

        NotificationMessage message = messageCaptor.getValue();
        assertThat(message.cuerpo()).contains("Cliente  fecha 2026-02-26T08:30:00");
    }
}

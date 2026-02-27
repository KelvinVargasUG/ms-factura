package com.foodtech.ms_factura.application.notification;

import com.foodtech.ms_factura.application.ports.output.NotificationChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationDispatchServiceTest {

    @Mock
    private NotificationChannel emailChannel;

    @Mock
    private NotificationChannel smsChannel;

    @Spy
    private List<NotificationChannel> notificationChannels = new ArrayList<>();

    @InjectMocks
    private NotificationDispatchService service;

    @BeforeEach
    void setUp() {
        notificationChannels.clear();
        notificationChannels.addAll(List.of(emailChannel, smsChannel));
    }

    @Test
    void shouldDispatchToConfiguredChannel() {
        // Arrange
        ReflectionTestUtils.setField(service, "notificationEnabled", true);
        ReflectionTestUtils.setField(service, "configuredChannel", "email");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Asunto",
                "Cuerpo",
                "ref-1",
                List.of(Path.of("/tmp/facturas/factura_ref-1.txt"))
        );

        when(emailChannel.getChannelName()).thenReturn("email");

        // Act
        service.dispatch(message);

        // Assert
        verify(emailChannel).send(message);
        verify(smsChannel, never()).send(any());
    }

    @Test
    void shouldSkipDispatchWhenNotificationsAreDisabled() {
        // Arrange
        ReflectionTestUtils.setField(service, "notificationEnabled", false);
        ReflectionTestUtils.setField(service, "configuredChannel", "email");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Asunto",
                "Cuerpo",
                "ref-2",
                List.of()
        );

        // Act
        service.dispatch(message);

        // Assert
        verifyNoInteractions(emailChannel, smsChannel);
    }

    @Test
    void shouldSkipDispatchWhenChannelIsUnsupported() {
        // Arrange
        ReflectionTestUtils.setField(service, "notificationEnabled", true);
        ReflectionTestUtils.setField(service, "configuredChannel", "push");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Asunto",
                "Cuerpo",
                "ref-3",
                List.of()
        );

        when(emailChannel.getChannelName()).thenReturn("email");
        when(smsChannel.getChannelName()).thenReturn("sms");

        // Act
        service.dispatch(message);

        // Assert
        verify(emailChannel, never()).send(any());
        verify(smsChannel, never()).send(any());
    }

    @Test
    void shouldMatchChannelNameIgnoringCase() {
        // Arrange
        ReflectionTestUtils.setField(service, "notificationEnabled", true);
        ReflectionTestUtils.setField(service, "configuredChannel", "EMAIL");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Asunto",
                "Cuerpo",
                "ref-4",
                List.of()
        );

        when(emailChannel.getChannelName()).thenReturn("email");

        // Act
        service.dispatch(message);

        // Assert
        verify(emailChannel).send(message);
        verify(smsChannel, never()).send(any());
    }

    @Test
    void shouldNotPropagateChannelFailure() {
        // Arrange
        ReflectionTestUtils.setField(service, "notificationEnabled", true);
        ReflectionTestUtils.setField(service, "configuredChannel", "email");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Asunto",
                "Cuerpo",
                "ref-5",
                List.of()
        );

        when(emailChannel.getChannelName()).thenReturn("email");
        doThrow(new RuntimeException("smtp error")).when(emailChannel).send(message);

        // Act
        assertDoesNotThrow(() -> service.dispatch(message));

        // Assert
        verify(emailChannel).send(message);
    }

    @Test
    void shouldThrowWhenConfiguredChannelIsNull() {
        // Arrange
        ReflectionTestUtils.setField(service, "notificationEnabled", true);
        ReflectionTestUtils.setField(service, "configuredChannel", null);

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Asunto",
                "Cuerpo",
                "ref-6",
                List.of()
        );

        when(emailChannel.getChannelName()).thenReturn("email");

        // Act + Assert
        assertThrows(NullPointerException.class, () -> service.dispatch(message));
    }

    @Test
    void shouldThrowWhenMessageIsNull() {
        // Arrange
        ReflectionTestUtils.setField(service, "notificationEnabled", true);
        ReflectionTestUtils.setField(service, "configuredChannel", "email");

        // Act + Assert
        assertThrows(NullPointerException.class, () -> service.dispatch(null));
    }
}

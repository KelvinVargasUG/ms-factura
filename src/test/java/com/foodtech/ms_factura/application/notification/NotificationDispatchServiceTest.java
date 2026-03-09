package com.foodtech.ms_factura.application.notification;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.foodtech.ms_factura.application.ports.output.NotificationChannel;

@SuppressWarnings({ "PMD.TooManyStaticImports", "PMD.AtLeastOneConstructor", "PMD.LawOfDemeter", "PMD.LongVariable" })
@ExtendWith(MockitoExtension.class)
@Tag("unit")
class NotificationDispatchServiceTest {

    private static final String FIELD_NOTIFICATION_ENABLED = "notificationEnabled";
    private static final String FIELD_CONFIGURED_CHANNEL = "configuredChannel";
    private static final String EMAIL_CHANNEL = "email";
    private static final String TEST_EMAIL = "cliente@example.com";
    private static final String TEST_SUBJECT = "Asunto";
    private static final String TEST_BODY = "Cuerpo";

    @Mock
    private NotificationChannel emailChannel;

    @Mock
    private NotificationChannel smsChannel;

    @Spy
    @SuppressWarnings("PMD.ImmutableField")
    private final List<NotificationChannel> notificationChannels = new ArrayList<>();

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
        ReflectionTestUtils.setField(service, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(service, FIELD_CONFIGURED_CHANNEL, EMAIL_CHANNEL);

        NotificationMessage message = new NotificationMessage(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_BODY,
                "ref-1",
                List.of(Path.of("/tmp/facturas/factura_ref-1.txt")));

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
        ReflectionTestUtils.setField(service, FIELD_NOTIFICATION_ENABLED, false);
        ReflectionTestUtils.setField(service, FIELD_CONFIGURED_CHANNEL, EMAIL_CHANNEL);

        NotificationMessage message = new NotificationMessage(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_BODY,
                "ref-2",
                List.of());

        // Act
        service.dispatch(message);

        // Assert
        verifyNoInteractions(emailChannel, smsChannel);
    }

    @Test
    void shouldSkipDispatchWhenChannelIsUnsupported() {
        // Arrange
        ReflectionTestUtils.setField(service, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(service, FIELD_CONFIGURED_CHANNEL, "push");

        NotificationMessage message = new NotificationMessage(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_BODY,
                "ref-3",
                List.of());

        when(emailChannel.getChannelName()).thenReturn(EMAIL_CHANNEL);
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
        ReflectionTestUtils.setField(service, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(service, FIELD_CONFIGURED_CHANNEL, "EMAIL");

        NotificationMessage message = new NotificationMessage(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_BODY,
                "ref-4",
                List.of());

        when(emailChannel.getChannelName()).thenReturn(EMAIL_CHANNEL);

        // Act
        service.dispatch(message);

        // Assert
        verify(emailChannel).send(message);
        verify(smsChannel, never()).send(any());
    }

    @Test
    void shouldNotPropagateChannelFailure() {
        // Arrange
        ReflectionTestUtils.setField(service, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(service, FIELD_CONFIGURED_CHANNEL, EMAIL_CHANNEL);

        NotificationMessage message = new NotificationMessage(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_BODY,
                "ref-5",
                List.of());

        when(emailChannel.getChannelName()).thenReturn(EMAIL_CHANNEL);
        doThrow(new RuntimeException("smtp error")).when(emailChannel).send(message);

        // Act
        assertDoesNotThrow(() -> service.dispatch(message));

        // Assert
        verify(emailChannel).send(message);
    }

    @Test
    void shouldThrowWhenConfiguredChannelIsNull() {
        // Arrange
        ReflectionTestUtils.setField(service, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(service, FIELD_CONFIGURED_CHANNEL, null);

        NotificationMessage message = new NotificationMessage(
                TEST_EMAIL,
                TEST_SUBJECT,
                TEST_BODY,
                "ref-6",
                List.of());

        // Act + Assert
        assertThrows(NullPointerException.class, () -> service.dispatch(message));
    }

    @Test
    void shouldThrowWhenMessageIsNull() {
        // Arrange
        ReflectionTestUtils.setField(service, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(service, FIELD_CONFIGURED_CHANNEL, EMAIL_CHANNEL);

        // Act + Assert
        assertThrows(NullPointerException.class, () -> service.dispatch(null));
    }
}

package com.foodtech.ms_factura.infrastructure.adapters.output.notification;

import com.foodtech.ms_factura.application.notification.NotificationMessage;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailNotificationChannelTest {

    @Mock
    private JavaMailSender mailSender;

    @TempDir
    Path tempDir;

    // Enviar email con adjunto válido
    @Test
    void shouldSendEmailWithAttachment() throws Exception {
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", "no-reply@example.com");

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        Path attachment = tempDir.resolve("factura.txt");
        Files.writeString(attachment, "contenido factura");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-1",
                "Body",
                "ref-1",
                List.of(attachment));

        channel.send(message);

        verify(mailSender).send(any(MimeMessage.class));
        assertThat(channel.getChannelName()).isEqualTo("email");
    }

    // Validar que lanza excepción si 'from' es null (obligatorio)
    @Test
    void shouldThrowWhenFromIsNull() throws Exception {
        // Arrange
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", null);

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-2",
                "Body",
                "ref-2",
                List.of());

        // Act & Assert
        assertThatThrownBy(() -> channel.send(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Propiedad 'notification.email.from' es obligatoria para enviar notificaciones por email");
    }

    // Validar que lanza excepción si 'from' es vacío (solo espacios)
    @Test
    void shouldThrowWhenFromIsBlank() throws Exception {
        // Arrange
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", "   ");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-3",
                "Body",
                "ref-3",
                List.of());

        // Act & Assert
        assertThatThrownBy(() -> channel.send(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Propiedad 'notification.email.from' es obligatoria para enviar notificaciones por email");
    }

    // Enviar email con lista de adjuntos que contiene un elemento null
    @Test
    void shouldSkipNullAttachments() throws Exception {
        // Arrange
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", "no-reply@example.com");

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-4",
                "Body",
                "ref-4",
                List.of());

        // Act
        channel.send(message);

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
    }

    // Enviar email con lista de adjuntos que contiene un elemento null y otro
    // válido
    @Test
    void shouldSkipNullAttachmentEntryInsideList() throws Exception {
        // Arrange
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", "no-reply@example.com");

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        Path attachment = tempDir.resolve("factura-null-entry.txt");
        Files.writeString(attachment, "contenido");

        List<Path> attachments = new ArrayList<>();
        attachments.add(null);
        attachments.add(attachment);

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-null-entry",
                "Body",
                "ref-null-entry",
                attachments);

        // Act
        channel.send(message);

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
    }

    // Validar que lanza excepción si un adjunto no existe en el sistema de archivos
    @Test
    void shouldThrowWhenAttachmentDoesNotExist() throws Exception {
        // Arrange
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", "no-reply@example.com");

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        Path nonExistentFile = tempDir.resolve("no-existe.txt");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-5",
                "Body",
                "ref-5",
                List.of(nonExistentFile));

        // Act & Assert
        assertThatThrownBy(() -> channel.send(message))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Archivo adjunto no existe: " + nonExistentFile);
    }

    // Simular una excepción de MessagingException al construir el email (por
    // ejemplo, debido a un formato de dirección "from" inválido)
    // y verificar que se lanza una RuntimeException con el mensaje esperado.
    @Test
    void shouldThrowRuntimeExceptionOnMessagingException() throws Exception {
        // Arrange
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", "no-reply@example.com");

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("SMTP error"));

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-6",
                "Body",
                "ref-6",
                List.of());

        // Act & Assert
        assertThatThrownBy(() -> channel.send(message))
                .isInstanceOf(RuntimeException.class);
    }

    // Simular una excepción de MessagingException debido a un formato de dirección
    // "from" inválido
    // y verificar que se lanza una RuntimeException con el mensaje esperado y la
    // causa correcta.
    @Test
    void shouldWrapMessagingExceptionWhenFromAddressIsInvalid() {
        // Arrange
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", "invalid\nfrom@example.com");

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-invalid-from",
                "Body",
                "ref-invalid-from",
                List.of());

        // Act & Assert
        assertThatThrownBy(() -> channel.send(message))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Error al construir email de notificación")
                .hasCauseInstanceOf(jakarta.mail.MessagingException.class);
    }

    // Enviar email con múltiples adjuntos válidos
    @Test
    void shouldSendEmailWithMultipleAttachments() throws Exception {
        // Arrange
        EmailNotificationChannel channel = new EmailNotificationChannel(mailSender);
        ReflectionTestUtils.setField(channel, "from", "no-reply@example.com");

        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        Path attachment1 = tempDir.resolve("factura1.txt");
        Files.writeString(attachment1, "contenido1");

        Path attachment2 = tempDir.resolve("factura2.txt");
        Files.writeString(attachment2, "contenido2");

        NotificationMessage message = new NotificationMessage(
                "cliente@example.com",
                "Factura ref-7",
                "Body",
                "ref-7",
                List.of(attachment1, attachment2));

        // Act
        channel.send(message);

        // Assert
        verify(mailSender).send(any(MimeMessage.class));
    }
}

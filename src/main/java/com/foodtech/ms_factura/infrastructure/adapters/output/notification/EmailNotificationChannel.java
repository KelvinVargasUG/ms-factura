package com.foodtech.ms_factura.infrastructure.adapters.output.notification;

import com.foodtech.ms_factura.application.notification.NotificationMessage;
import com.foodtech.ms_factura.application.ports.output.NotificationChannel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationChannel implements NotificationChannel {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from:}")
    private String from;

    @Override
    public String getChannelName() {
        return "email";
    }

    @Override
    public void send(NotificationMessage message) {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("Propiedad 'notification.email.from' es obligatoria para enviar notificaciones por email");
        }
        
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(from);
            helper.setTo(message.destinatario());
            helper.setSubject(message.asunto());
            helper.setText(message.cuerpo(), false);

            for (Path attachment : message.attachments()) {
                if (attachment != null) {
                    if (!Files.exists(attachment)) {
                        throw new IllegalArgumentException(
                            "Archivo adjunto no existe: " + attachment);
                    }
                    helper.addAttachment(attachment.getFileName().toString(), attachment.toFile());
                }
            }

            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            log.error("Fallo construyendo email invoiceRef={} causa={}", message.invoiceRef(), exception.getMessage(), exception);
            throw new RuntimeException("Error al construir email de notificación", exception);
        }
    }
}

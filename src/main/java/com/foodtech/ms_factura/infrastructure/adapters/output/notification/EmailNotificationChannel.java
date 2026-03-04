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
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.LawOfDemeter"})
public class EmailNotificationChannel implements NotificationChannel {

    private static final String EMAIL_CHARSET = StandardCharsets.UTF_8.name();

    private final JavaMailSender mailSender;

    @Value("${notification.email.from:}")
    private String from;

    @Override
    public String getChannelName() {
        return "email";
    }

    @Override
    public void send(NotificationMessage message) {
        validateEmailConfiguration();
        try {
            sendEmailMessage(message);
        } catch (MessagingException exception) {
            handleEmailSendError(message, exception);
        }
    }

    private void validateEmailConfiguration() {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("Propiedad 'notification.email.from' es obligatoria para enviar notificaciones por email");
        }
    }

    private void sendEmailMessage(NotificationMessage message) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, EMAIL_CHARSET);
        configureEmailMessage(helper, message);
        addAttachments(helper, message);
        mailSender.send(mimeMessage);
    }

    private void configureEmailMessage(MimeMessageHelper helper, NotificationMessage message) throws MessagingException {
        String recipient = message.destinatario();
        String subject = message.asunto();
        String body = message.cuerpo();
        helper.setFrom(from);
        helper.setTo(recipient);
        helper.setSubject(subject);
        helper.setText(body, false);
    }

    private void addAttachments(MimeMessageHelper helper, NotificationMessage message) throws MessagingException {
        var attachments = message.attachments();
        for (Path attachment : attachments) {
            if (attachment == null) {
                continue;
            }
            if (!Files.exists(attachment)) {
                throw new IllegalArgumentException("Archivo adjunto no existe: " + attachment);
            }
            Path fileName = attachment.getFileName();
            String name = fileName == null ? "attachment" : fileName.toString();
            helper.addAttachment(name, attachment.toFile());
        }
    }

    private void handleEmailSendError(NotificationMessage message, MessagingException exception) {
        if (log.isErrorEnabled()) {
            log.error("Fallo construyendo email invoiceRef={} causa={}", message.invoiceRef(), exception.getMessage(), exception);
        }
        throw new IllegalStateException("Error al construir email de notificación", exception);
    }
}

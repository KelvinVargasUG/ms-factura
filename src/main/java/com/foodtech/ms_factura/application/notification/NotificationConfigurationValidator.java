package com.foodtech.ms_factura.application.notification;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@SuppressWarnings({"PMD.UnnecessaryConstructor", "PMD.LongVariable"})
public class NotificationConfigurationValidator {

    private static final String EMAIL_CHANNEL = "email";

    public NotificationConfigurationValidator() {
    }

    @Value("${notification.enabled:true}")
    private boolean notificationEnabled;

    @Value("${notification.channel:email}")
    private String configuredChannel;

    @PostConstruct
    public void validate() {
        if (!notificationEnabled) {
            log.info("Notificaciones deshabilitadas por configuración");
            return;
        }

        if (!EMAIL_CHANNEL.equalsIgnoreCase(configuredChannel)) {
            log.warn("Canal de notificación no soportado en esta versión: {}", configuredChannel);
        }
    }
}

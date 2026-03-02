package com.foodtech.ms_factura.application.notification;

import com.foodtech.ms_factura.application.ports.output.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationDispatchService {

    private final List<NotificationChannel> notificationChannels;

    @Value("${notification.enabled:true}")
    private boolean notificationEnabled;

    @Value("${notification.channel:email}")
    private String configuredChannel;

    public void dispatch(NotificationMessage message) {
        if (!notificationEnabled) {
            log.info("Notificaciones deshabilitadas para invoiceRef={}", message.invoiceRef());
            return;
        }

        NotificationChannel channel = notificationChannels.stream()
                .filter(candidate -> candidate.getChannelName().equalsIgnoreCase(configuredChannel))
                .findFirst()
                .orElse(null);

        if (channel == null) {
            log.warn("Canal de notificación no soportado: {}", configuredChannel);
            return;
        }

        try {
            channel.send(message);
            log.info("Notificación enviada OK canal={} invoiceRef={} destinatario={}",
                    channel.getChannelName(),
                    message.invoiceRef(),
                    message.destinatario());
        } catch (Exception exception) {
            log.error("Fallo enviando notificación canal={} invoiceRef={} causa={}",
                    channel.getChannelName(),
                    message.invoiceRef(),
                    exception.getMessage(),
                    exception);
        }
    }
}

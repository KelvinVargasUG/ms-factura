package com.foodtech.ms_factura.application.notification;

import com.foodtech.ms_factura.application.ports.output.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"PMD.LawOfDemeter", "PMD.CyclomaticComplexity", "PMD.LongVariable", "PMD.AvoidCatchingGenericException"})
public class NotificationDispatchService {

    private final List<NotificationChannel> notificationChannels;

    @Value("${notification.enabled:true}")
    private boolean notificationEnabled;

    @Value("${notification.channel:email}")
    private String configuredChannel;

    public void dispatch(NotificationMessage message) {
        boolean isEnabled = notificationEnabled;
        if (!isEnabled && log.isInfoEnabled()) {
            log.info("Notificaciones deshabilitadas para invoiceRef={}", message.invoiceRef());
        }

        NotificationChannel channel = isEnabled ? findChannel() : null;
        if (isEnabled && channel == null && log.isWarnEnabled()) {
            log.warn("Canal de notificación no soportado: {}", configuredChannel);
        }

        if (isEnabled && channel != null) {
            sendNotification(channel, message);
        }
    }

    private NotificationChannel findChannel() {
        String channelName = configuredChannel.toLowerCase(Locale.ROOT);
        return notificationChannels.stream()
            .filter(candidate -> candidate.getChannelName().equalsIgnoreCase(channelName))
                .findFirst()
                .orElse(null);
    }

    private void sendNotification(NotificationChannel channel, NotificationMessage message) {
        String channelName = channel.getChannelName();
        try {
            channel.send(message);
            if (log.isInfoEnabled()) {
                log.info("Notificación enviada OK canal={} invoiceRef={} destinatario={}",
                        channelName,
                        message.invoiceRef(),
                        message.destinatario());
            }
        } catch (RuntimeException exception) {
            if (log.isErrorEnabled()) {
                log.error("Fallo enviando notificación canal={} invoiceRef={} causa={}",
                        channelName,
                        message.invoiceRef(),
                        exception.getMessage(),
                        exception);
            }
        }
    }
}

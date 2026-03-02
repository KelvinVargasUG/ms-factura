package com.foodtech.ms_factura.application.ports.output;

import com.foodtech.ms_factura.application.notification.NotificationMessage;

public interface NotificationChannel {
    String getChannelName();

    void send(NotificationMessage message);
}

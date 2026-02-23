package com.foodtech.ms_factura.domain;

import java.time.LocalDateTime;

public class FoodEvent {
    private String eventId;
    private String eventType;
    private String payload;
    private LocalDateTime timestamp;

    public FoodEvent() {
    }

    public FoodEvent(String eventId, String eventType, String payload, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.payload = payload;
        this.timestamp = timestamp;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
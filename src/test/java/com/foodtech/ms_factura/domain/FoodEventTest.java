package com.foodtech.ms_factura.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class FoodEventTest {

    @Mock
    private Object unusedDependency;

    @InjectMocks
    private FoodEvent foodEvent;

    @Test
    void shouldInitializeWithAllArgsConstructor() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 2, 27, 9, 30);

        // Act
        FoodEvent event = new FoodEvent("evt-1", "FACTURA_CREADA", "{json}", timestamp);

        // Assert
        assertEquals("evt-1", event.getEventId());
        assertEquals("FACTURA_CREADA", event.getEventType());
        assertEquals("{json}", event.getPayload());
        assertEquals(timestamp, event.getTimestamp());
    }

    @Test
    void shouldSetAndGetValues() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.of(2026, 2, 27, 10, 15);

        // Act
        foodEvent.setEventId("evt-2");
        foodEvent.setEventType("FACTURA_ACTUALIZADA");
        foodEvent.setPayload("payload");
        foodEvent.setTimestamp(timestamp);

        // Assert
        assertEquals("evt-2", foodEvent.getEventId());
        assertEquals("FACTURA_ACTUALIZADA", foodEvent.getEventType());
        assertEquals("payload", foodEvent.getPayload());
        assertEquals(timestamp, foodEvent.getTimestamp());
    }

    @Test
    void shouldAllowNullValues() {
        // Arrange
        FoodEvent event = new FoodEvent();

        // Act
        event.setEventId(null);
        event.setEventType(null);
        event.setPayload(null);
        event.setTimestamp(null);

        // Assert
        assertNull(event.getEventId());
        assertNull(event.getEventType());
        assertNull(event.getPayload());
        assertNull(event.getTimestamp());
    }
}

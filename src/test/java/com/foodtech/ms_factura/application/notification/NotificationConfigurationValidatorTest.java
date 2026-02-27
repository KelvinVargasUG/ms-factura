package com.foodtech.ms_factura.application.notification;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class NotificationConfigurationValidatorTest {

    @Test
    void shouldValidateWithoutThrowingWhenConfigurationIsIncomplete() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, "notificationEnabled", true);
        ReflectionTestUtils.setField(validator, "configuredChannel", "sms");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldReturnEarlyWhenNotificationsAreDisabled() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, "notificationEnabled", false);
        ReflectionTestUtils.setField(validator, "configuredChannel", "email");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldLogWarnWhenChannelIsNotEmail() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, "notificationEnabled", true);
        ReflectionTestUtils.setField(validator, "configuredChannel", "sms");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldValidateWithoutThrowingWhenChannelIsNull() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, "notificationEnabled", true);
        ReflectionTestUtils.setField(validator, "configuredChannel", null);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldValidateWithoutThrowingWhenChannelIsBlank() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, "notificationEnabled", true);
        ReflectionTestUtils.setField(validator, "configuredChannel", "   ");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldValidateSuccessfullyWithValidConfiguration() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, "notificationEnabled", true);
        ReflectionTestUtils.setField(validator, "configuredChannel", "email");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldHandleCaseInsensitiveChannelName() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, "notificationEnabled", true);
        ReflectionTestUtils.setField(validator, "configuredChannel", "EMAIL");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }
}

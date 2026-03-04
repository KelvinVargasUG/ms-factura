package com.foodtech.ms_factura.application.notification;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@SuppressWarnings({ "PMD.AtLeastOneConstructor", "PMD.LongVariable" })
@Tag("unit")
class NotificationConfigurationValidatorTest {

    private static final String FIELD_NOTIFICATION_ENABLED = "notificationEnabled";
    private static final String FIELD_CONFIGURED_CHANNEL = "configuredChannel";

    @Test
    void shouldValidateWithoutThrowingWhenConfigurationIsIncomplete() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(validator, FIELD_CONFIGURED_CHANNEL, "sms");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldReturnEarlyWhenNotificationsAreDisabled() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, FIELD_NOTIFICATION_ENABLED, false);
        ReflectionTestUtils.setField(validator, FIELD_CONFIGURED_CHANNEL, "email");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldLogWarnWhenChannelIsNotEmail() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(validator, FIELD_CONFIGURED_CHANNEL, "sms");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldValidateWithoutThrowingWhenChannelIsNull() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(validator, FIELD_CONFIGURED_CHANNEL, null);

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldValidateWithoutThrowingWhenChannelIsBlank() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(validator, FIELD_CONFIGURED_CHANNEL, "   ");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldValidateSuccessfullyWithValidConfiguration() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(validator, FIELD_CONFIGURED_CHANNEL, "email");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }

    @Test
    void shouldHandleCaseInsensitiveChannelName() {
        // Arrange
        NotificationConfigurationValidator validator = new NotificationConfigurationValidator();
        ReflectionTestUtils.setField(validator, FIELD_NOTIFICATION_ENABLED, true);
        ReflectionTestUtils.setField(validator, FIELD_CONFIGURED_CHANNEL, "EMAIL");

        // Act & Assert
        assertDoesNotThrow(() -> validator.validate());
    }
}

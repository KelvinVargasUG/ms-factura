## ADDED Requirements

### Requirement: Notification channels are abstracted through strategy contracts
Notification delivery MUST be modeled through channel contracts so the application layer can dispatch notifications without depending on specific transport providers.

#### Scenario: Application uses channel contract instead of concrete provider
- **WHEN** the notification use case dispatches a notification
- **THEN** it SHALL invoke a channel strategy/port contract rather than a concrete provider implementation

#### Scenario: Infrastructure provides channel-specific adapters
- **WHEN** a notification channel is enabled
- **THEN** its provider-specific logic SHALL be implemented in infrastructure adapters only

### Requirement: Channel selection is configuration-driven and extensible
The system MUST select one or multiple notification channels from configuration without changing core business orchestration.

#### Scenario: Single-channel mode selects email
- **WHEN** `notification.channel=email` is configured
- **THEN** the system SHALL dispatch notifications through the email channel strategy

#### Scenario: Multi-channel mode selects all configured channels
- **WHEN** `notification.channels` contains multiple values
- **THEN** the system SHALL attempt dispatch through each configured channel strategy

#### Scenario: Unknown or disabled channels are handled safely
- **WHEN** configuration contains unsupported or disabled channel names
- **THEN** the system SHALL skip unsupported channels and log a configuration warning without breaking invoice generation flow

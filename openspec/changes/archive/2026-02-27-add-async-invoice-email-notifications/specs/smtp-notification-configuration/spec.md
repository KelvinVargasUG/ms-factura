## ADDED Requirements

### Requirement: SMTP email settings are externally configurable
The email notification adapter MUST read SMTP configuration from externalized properties and MUST NOT hardcode credentials in source code.

#### Scenario: SMTP host and sender settings are resolved from configuration
- **WHEN** the email adapter initializes
- **THEN** SMTP host, port, username, password, and sender address SHALL be loaded from application configuration

#### Scenario: Credentials are provided via environment or secret source
- **WHEN** runtime credentials are needed for SMTP authentication
- **THEN** credentials SHALL be sourced from environment variables or secret-management integration, not hardcoded constants

### Requirement: Email template content is configurable
The notification system MUST allow configurable subject/body templates for invoice delivery messages.

#### Scenario: Subject template is applied
- **WHEN** an email notification is created
- **THEN** the email subject SHALL be rendered from configurable template text

#### Scenario: Body template includes required invoice fields
- **WHEN** the body template is rendered
- **THEN** it SHALL include invoice number/reference and invoice date values in the final message

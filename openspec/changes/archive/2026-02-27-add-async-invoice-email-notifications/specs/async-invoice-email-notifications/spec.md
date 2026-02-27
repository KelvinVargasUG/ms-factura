## ADDED Requirements

### Requirement: Invoice generation triggers asynchronous notification flow
The system MUST trigger a notification workflow when invoice generation completes, and this workflow MUST execute asynchronously from the main invoice-generation request path.

#### Scenario: Notification flow is triggered after invoice output generation
- **WHEN** an invoice and its output files are generated successfully
- **THEN** the system SHALL publish an application-level event indicating invoice generation completion

#### Scenario: Main invoice generation flow remains non-blocking
- **WHEN** notification dispatch is slow or temporarily unavailable
- **THEN** invoice generation completion SHALL NOT wait for notification delivery and SHALL preserve existing response behavior

### Requirement: Email notification supports invoice attachments and delivery outcome logging
The system MUST send an email notification through the configured email channel and MUST support attaching one or more generated invoice files (PDF, XLSX, TXT).

#### Scenario: Email includes configured template and invoice metadata
- **WHEN** the email notification is prepared for a generated invoice
- **THEN** the subject and body SHALL be built from configured templates and include invoice identifier and generation date

#### Scenario: Email can attach one or more generated invoice files
- **WHEN** generated files are available for the invoice
- **THEN** the notification SHALL attach all configured/available files among PDF, XLSX, and TXT

#### Scenario: Delivery result is logged with outcome and error cause
- **WHEN** email sending succeeds or fails
- **THEN** the system SHALL log channel, invoice reference, recipient, result status, and failure cause when applicable

## MODIFIED Requirements

### Requirement: Domain and Application Have Isolated Unit Test Coverage
The project MUST provide unit tests that validate Domain and Application behavior without framework bootstrapping.

#### Scenario: Domain rules are tested without Spring context
- **WHEN** Domain business rules are executed in test
- **THEN** tests SHALL run as plain unit tests with no Spring test runner or container wiring

#### Scenario: Application use cases are tested with mocked ports
- **WHEN** an Application use case is tested
- **THEN** external collaborators SHALL be represented by mocked/stubbed ports and assertions SHALL validate orchestration behavior

#### Scenario: Asynchronous notification dispatch is validated at use-case level
- **WHEN** invoice generation completion triggers notification dispatch in application tests
- **THEN** tests SHALL verify event/dispatch invocation through ports without waiting on SMTP infrastructure

### Requirement: Adapter Integration Tests Validate Wiring and Translation
Infrastructure adapters MUST be covered by integration tests for framework wiring and contract translation.

#### Scenario: Inbound messaging adapter integration behavior is validated
- **WHEN** representative Kafka or RabbitMQ payloads are processed
- **THEN** integration tests SHALL verify deserialization/mapping, delegation to use case ports, and error-path handling

#### Scenario: Output adapter integration behavior is validated
- **WHEN** TXT or PDF generation adapters are invoked through their contracts
- **THEN** integration tests SHALL verify successful output generation and failure handling semantics

#### Scenario: Email notification adapter integration behavior is validated
- **WHEN** email notification dispatch is executed with generated attachments
- **THEN** integration tests SHALL verify template rendering, attachment inclusion, SMTP invocation, and failure-path logging semantics

### Requirement: Backward-Compatible Behavior Is Guarded by Tests
The test suite MUST include regression coverage for behavior that must remain unchanged during refactor.

#### Scenario: DTO/status/validation compatibility is protected
- **WHEN** existing endpoint-level inputs are tested
- **THEN** assertions SHALL confirm unchanged DTO structure, status codes, and validation outcomes

#### Scenario: Format-selection fallback compatibility is protected
- **WHEN** invoice generation receives `PDF`, `TXT`, and null/unknown formats
- **THEN** assertions SHALL confirm unchanged selection and TXT fallback behavior

#### Scenario: Notification failures do not break invoice generation response
- **WHEN** notification delivery fails after invoice generation
- **THEN** regression assertions SHALL confirm the primary invoice generation response/observable behavior remains unchanged

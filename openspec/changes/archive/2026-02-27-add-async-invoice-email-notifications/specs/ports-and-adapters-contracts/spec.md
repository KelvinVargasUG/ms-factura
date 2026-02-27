## MODIFIED Requirements

### Requirement: Use Cases Exposed Through Input Ports
Application use cases MUST be invoked through explicit input port interfaces that represent business actions.

#### Scenario: Input adapters call use case ports
- **WHEN** an inbound message is received from Kafka or RabbitMQ
- **THEN** the adapter SHALL map transport payloads and invoke the corresponding input port

#### Scenario: Use case orchestration is transport-agnostic
- **WHEN** the same business action is invoked from different transports
- **THEN** Application logic SHALL execute without transport-specific conditionals

#### Scenario: Invoice completion emits application notification event
- **WHEN** invoice generation finishes successfully in the application flow
- **THEN** the application layer SHALL emit an internal event/port signal for post-generation notification dispatch

### Requirement: Side Effects Accessed Through Output Ports
Application orchestration MUST trigger side effects through output port interfaces implemented in Infrastructure.

#### Scenario: Invoice output is selected via port-backed strategy
- **WHEN** `formato` is `PDF`
- **THEN** the Application layer SHALL call the PDF-capable output adapter through an output port

#### Scenario: Fallback to TXT remains explicit
- **WHEN** `formato` is null, empty, or non-PDF
- **THEN** the Application layer SHALL call the TXT output adapter through the same output port contract

#### Scenario: Notification delivery uses channel port contract
- **WHEN** a post-generation notification is requested
- **THEN** the Application layer SHALL dispatch through notification channel port contracts without direct dependency on SMTP/provider classes

### Requirement: Adapter Responsibility Is Limited to Translation and I/O
Infrastructure adapters MUST handle framework integration, serialization/deserialization, and I/O concerns, but MUST NOT contain core business rules.

#### Scenario: Inbound adapter translates event and delegates
- **WHEN** a valid message wrapper arrives
- **THEN** the adapter SHALL deserialize/map input data and delegate business processing to an input port

#### Scenario: Output adapter performs file generation only
- **WHEN** Application requests invoice rendering
- **THEN** the adapter SHALL generate the file and return/propagate technical result without embedding domain decision logic

#### Scenario: Email adapter handles SMTP transport concerns only
- **WHEN** the notification adapter sends invoice emails
- **THEN** it SHALL resolve SMTP transport, template rendering, and attachment I/O without introducing domain decision logic

## MODIFIED Requirements

### Requirement: Enforced Layer Dependency Direction
The codebase MUST enforce dependency direction such that Infrastructure depends on Application and Domain, Application depends on Domain, and Domain depends on no framework or adapter layer.

#### Scenario: Domain imports are framework-free
- **WHEN** Domain classes are reviewed for imports
- **THEN** no Domain class SHALL import Spring, messaging clients, persistence libraries, HTTP libraries, or infrastructure adapters

#### Scenario: Application does not depend on infrastructure implementations
- **WHEN** Application use case classes are reviewed
- **THEN** Application classes SHALL depend only on Domain types and declared ports, not concrete adapter classes

#### Scenario: Infrastructure hosts framework integration points
- **WHEN** framework-facing components (controllers, consumers, repositories, file generators) are reviewed
- **THEN** those components SHALL be located under Infrastructure adapter packages and SHALL not be required by Domain classes

### Requirement: Stable External Behavior During Refactor
The refactor MUST preserve externally observable behavior for existing API and messaging flows.

#### Scenario: Existing request/response contract remains unchanged
- **WHEN** existing API consumers invoke current endpoints
- **THEN** the service SHALL return the same DTO shapes, status codes, and validations as before the refactor

#### Scenario: Existing invoice generation result remains equivalent
- **WHEN** a valid invoice event requests format `PDF` or another/null format
- **THEN** the service SHALL produce equivalent results, including TXT fallback for non-PDF values

#### Scenario: Existing input channels continue processing equivalently
- **WHEN** equivalent payloads are received through Kafka and RabbitMQ adapters
- **THEN** both flows SHALL delegate to the same application use case behavior without changing observable outcomes

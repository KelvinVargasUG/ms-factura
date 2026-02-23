## 1. Baseline and Compatibility Guardrails

- [x] 1.1 Inventory current external contracts (endpoints, DTOs, status codes, validations) and messaging entry points to preserve behavior during refactor
- [x] 1.2 Confirm baseline regression tests cover format selection semantics (`PDF` and TXT fallback for null/unknown values)
- [x] 1.3 Add missing compatibility tests for unchanged API and adapter-observable behavior before structural changes

## 2. Define Layer Boundaries and Ports

- [x] 2.1 Establish/normalize package boundaries for Domain, Application, and Infrastructure adapters
- [x] 2.2 Define input port interfaces for invoice-generation use cases in the Application boundary
- [x] 2.3 Define output port interfaces for TXT/PDF/XLSX generation and other external side effects
- [x] 2.4 Refactor application orchestration services to depend only on Domain types and declared ports

## 3. Isolate Domain from Framework Concerns

- [x] 3.1 Remove Spring/transport/persistence/file-system dependencies and annotations from Domain classes
- [x] 3.2 Extract transport and framework decision logic from Domain into Application or Infrastructure adapters
- [x] 3.3 Verify dependency direction (`infrastructure -> application -> domain`) through import/package review

## 4. Refactor Infrastructure Adapters

- [x] 4.1 Refactor Kafka inbound flow into adapter responsibilities: payload mapping, error handling, and delegation to input ports
- [x] 4.2 Refactor RabbitMQ inbound flow with equivalent adapter responsibilities and use-case delegation
- [x] 4.3 Align TXT/PDF/XLSX generators as output-port implementations with unchanged business outcomes
- [x] 4.4 Ensure output selection behavior remains equivalent, including explicit TXT fallback for non-PDF formats

## 5. Add Layered Test Coverage

- [x] 5.1 Add/adjust pure Domain unit tests (no Spring context)
- [x] 5.2 Add/adjust Application unit tests with mocked ports to validate orchestration and delegation
- [x] 5.3 Add/adjust integration tests for Kafka/Rabbit adapters (mapping, delegation, malformed payload handling)
- [x] 5.4 Add/adjust integration tests for TXT/PDF/XLSX adapters (success paths and failure semantics)
- [x] 5.5 Add/adjust regression tests proving unchanged API contract and cross-adapter behavior parity

## 6. Validate and Finalize Refactor

- [x] 6.1 Run full test suite and resolve refactor-induced failures without altering external contracts
- [x] 6.2 Remove obsolete wiring/classes replaced by ports and adapters
- [x] 6.3 Re-check OpenSpec artifact status and confirm change is ready for `/opsx:apply`

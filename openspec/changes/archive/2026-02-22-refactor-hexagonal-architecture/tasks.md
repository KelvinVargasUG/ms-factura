## 1. Baseline and Safety Checks

- [x] 1.1 Capture current behavior baseline for API contracts, status codes, validations, and format fallback (PDF vs default TXT)
- [x] 1.2 Identify and document all current entry points (Kafka consumer, RabbitMQ consumer, any HTTP endpoints/controllers)
- [x] 1.3 Add/refresh regression tests that assert unchanged external behavior before refactor

## 2. Define Hexagonal Boundaries and Ports

- [x] 2.1 Create/standardize package structure for `domain`, `application`, and `infrastructure/adapters`
- [x] 2.2 Define input port interfaces for invoice-generation use cases in the application boundary
- [x] 2.3 Define output port interfaces for invoice rendering/output operations
- [x] 2.4 Refactor application use-case orchestration to depend only on ports and domain types

## 3. Isolate Domain from Frameworks

- [x] 3.1 Remove framework-specific dependencies/imports from domain classes and domain policies
- [x] 3.2 Move any transport/framework decision logic out of domain into application or adapters
- [x] 3.3 Verify dependency direction: infrastructure -> application -> domain

## 4. Implement Infrastructure Adapters

- [x] 4.1 Refactor Kafka flow into an input adapter that maps payloads and calls application input port
- [x] 4.2 Refactor RabbitMQ flow into an input adapter that maps payloads and calls application input port
- [x] 4.3 Implement/adjust TXT output adapter behind output port contract
- [x] 4.4 Implement/adjust PDF output adapter behind output port contract
- [x] 4.5 Keep format selection behavior unchanged (PDF exact handling and TXT fallback)

## 5. Testing by Layer

- [x] 5.1 Add/adjust domain unit tests (pure unit tests, no Spring context)
- [x] 5.2 Add/adjust application unit tests with mocked ports to verify orchestration behavior
- [x] 5.3 Add integration tests for Kafka/Rabbit input adapters (mapping, delegation, error-path behavior)
- [x] 5.4 Add integration tests for output adapters (TXT/PDF generation success and failure handling)
- [x] 5.5 Ensure compatibility tests cover unchanged DTOs/status codes/validations and fallback behavior

## 6. Validation and Cleanup

- [x] 6.1 Run full test suite and fix refactor-induced failures without changing external contracts
- [x] 6.2 Remove obsolete wiring/classes replaced by ports and adapters
- [x] 6.3 Verify OpenSpec artifacts and mark implementation tasks complete as work is delivered

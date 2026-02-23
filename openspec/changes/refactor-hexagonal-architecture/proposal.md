## Why

The current codebase has partial layering but still mixes framework concerns with core business orchestration, which increases coupling and makes future changes riskier than necessary. A full Ports & Adapters refactor is needed now to isolate business logic, preserve external behavior, and enable safer evolution with stronger testability.

## What Changes

- Reorganize the codebase into explicit layers: Domain, Application (use cases), and Infrastructure (adapters).
- Remove framework dependencies from the Domain layer (no Spring/transport/persistence concerns in Domain).
- Introduce and standardize ports (interfaces) in Domain/Application for external interactions.
- Implement adapter classes in Infrastructure for inbound messaging and outbound factura generation.
- Keep externally observable behavior unchanged:
  - Same endpoints and API contract behavior.
  - Same request/response DTOs.
  - Same status codes and validations.
  - Same business outcome for invoice generation (including TXT fallback behavior).
- Improve automated test strategy:
  - Add/adjust unit tests for Domain and Application use cases.
  - Add integration tests for Infrastructure adapters and wiring.

## Capabilities

### New Capabilities
- `hexagonal-layer-separation`: Enforce strict Domain/Application/Infrastructure boundaries and dependency direction.
- `ports-and-adapters-contracts`: Define application/domain ports and provide infrastructure adapter implementations for message consumption and invoice output.
- `layered-testability`: Provide layer-appropriate tests (unit tests for Domain/Application, integration tests for adapters).

### Modified Capabilities
- None (no existing capability specs currently defined in `openspec/specs/`).

## Impact

- Affected code areas:
  - `src/main/java/com/foodtech/ms_factura/domain/**`
  - `src/main/java/com/foodtech/ms_factura/application/**`
  - `src/main/java/com/foodtech/ms_factura/infrastructure/**`
  - `src/main/java/com/foodtech/ms_factura/KafkaConsumer.java`
  - `src/main/java/com/foodtech/ms_factura/infrastructure/adapters/input/rabbitmq/**`
  - `src/test/java/com/foodtech/ms_factura/**`
- Public API impact:
  - No intentional API contract changes.
- Dependencies/systems:
  - Kafka and RabbitMQ integrations remain, but behind explicit adapter boundaries.
  - No required new runtime dependency is expected; changes are primarily structural and test-focused.

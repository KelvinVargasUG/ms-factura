## Why

The current service still has framework and transport concerns mixed with business orchestration in some flows, which increases coupling and makes changes harder to test safely. We need a focused refactor to fully enforce Ports & Adapters boundaries while keeping external behavior identical.

## What Changes

- Reorganize code into explicit layers:
  - Domain (business model and business rules)
  - Application (use cases and ports)
  - Infrastructure (input/output adapters and framework wiring)
- Ensure Domain remains framework-agnostic:
  - No Spring, HTTP, messaging, persistence, or file-system dependencies in Domain classes.
- Define and use ports (interfaces) at Application/Domain boundaries.
- Implement Infrastructure adapters for all external interactions (Kafka, RabbitMQ, file/PDF/XLSX output, and any transport/mapping glue).
- Preserve public API behavior exactly as-is:
  - same endpoints
  - same request/response DTOs
  - same status codes
  - same validations
- Improve testability by layer:
  - add/adjust unit tests for Domain rules and policies
  - add/adjust unit tests for Application use cases with mocked ports
  - add/adjust integration tests for Infrastructure adapters and wiring

## Capabilities

### Modified Capabilities

- `hexagonal-layer-separation`
  - Tighten and verify dependency direction (`infrastructure -> application -> domain`).
- `ports-and-adapters-contracts`
  - Standardize port contracts used by use cases and implemented by adapters.
- `layered-testability`
  - Expand test coverage to reflect strict layering and adapter boundaries.

### Related Existing Capabilities

- `xlsx-invoice-output`
- `spreadsheet-output-adapter`
- `xlsx-regression-coverage`

These remain behavior-compatible and continue to work through the new/refined port contracts.

## Constraints and Non-Goals

- No functional changes to external API contracts or validation semantics.
- No redesign of endpoint surface or response schemas.
- No changes to business output rules beyond architecture boundary extraction.

## Impact

- Affected code areas:
  - `src/main/java/com/foodtech/ms_factura/domain/**`
  - `src/main/java/com/foodtech/ms_factura/application/**`
  - `src/main/java/com/foodtech/ms_factura/infrastructure/**`
  - `src/test/java/com/foodtech/ms_factura/**`
- Runtime behavior:
  - Expected to remain unchanged from a client/integration perspective.
- Risk:
  - Medium refactor risk (structural movement and wiring), mitigated by regression + layer-focused tests.

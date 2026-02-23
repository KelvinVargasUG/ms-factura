## Context

`ms-factura` currently implements invoice generation with a partial layering approach, but framework concerns are still mixed with orchestration in some flows. This change formalizes a Ports & Adapters structure so business behavior remains stable while dependencies become explicit and testable.

Current constraints:
- External behavior must remain identical (endpoints, DTOs, status codes, validations).
- Existing Kafka/RabbitMQ integrations must continue to invoke the same business outcome.
- Existing invoice output behavior (including TXT fallback) must not change.

Stakeholders:
- API consumers and upstream event producers (require compatibility)
- Development team (requires maintainable boundaries and easier tests)

## Goals / Non-Goals

**Goals:**
- Enforce dependency direction: `infrastructure -> application -> domain`.
- Keep Domain free of framework dependencies.
- Introduce clear input/output port interfaces used by use cases.
- Move all framework and transport details to Infrastructure adapters.
- Improve layer-specific test coverage (domain/application unit, adapter integration).

**Non-Goals:**
- No endpoint contract redesign.
- No DTO shape changes.
- No changes to validation semantics.
- No business rule changes for format selection/output.

## Decisions

### Decision 1: Define explicit ports in Application boundary
- Use input ports for use-case entry points (e.g., invoice generation command handling).
- Use output ports for external effects (file generation, message-specific side effects).

**Rationale:**
- Makes use cases framework-agnostic and mockable.
- Keeps orchestration logic testable without Spring context.

**Alternatives considered:**
- Keep direct adapter dependencies in services: rejected due to tight coupling and brittle tests.
- Define ports in infrastructure package: rejected because dependency direction would invert.

### Decision 2: Keep Domain as pure Java model/rules only
- Domain classes and policies contain business logic only.
- No Spring annotations, transport parsing, or messaging dependencies in Domain.

**Rationale:**
- Prevents framework leakage into business core.
- Enables stable and fast domain unit tests.

**Alternatives considered:**
- Allow selective framework annotations in Domain for convenience: rejected due to long-term coupling.

### Decision 3: Move message mapping/transport handling to inbound adapters
- Kafka and RabbitMQ adapters handle deserialization, payload translation, and error-path logging.
- Adapters call application input ports with normalized command objects/domain inputs.

**Rationale:**
- Isolates protocol/transport variability.
- Preserves existing integration behavior while keeping Application focused on use-case orchestration.

**Alternatives considered:**
- Parse transport payloads in Application service: rejected because it mixes transport concerns with use cases.

### Decision 4: Keep output strategy behavior through output-port implementations
- TXT/PDF/XLSX generation remains in Infrastructure as adapter implementations.
- Selection/fallback semantics remain behavior-compatible.

**Rationale:**
- Maintains current observable outputs.
- Keeps file-system/PDF/spreadsheet dependencies outside Domain/Application.

**Alternatives considered:**
- Merge output generation into Application service: rejected due to infra dependency leakage.

### Decision 5: Layered testing strategy as acceptance guard
- Domain: pure unit tests for rules/policies.
- Application: unit tests with mocked output ports.
- Infrastructure: integration tests for adapters and framework wiring.

**Rationale:**
- Ensures refactor confidence without changing external behavior.
- Detects boundary violations early.

**Alternatives considered:**
- Rely mostly on end-to-end tests: rejected because failures become harder to localize.

## Risks / Trade-offs

- [Risk] Regression during package moves or wiring refactor → Mitigation: preserve contracts and run focused regression tests before/after each adapter migration.
- [Risk] Hidden framework dependency remains in Domain → Mitigation: enforce package boundaries and review imports during refactor.
- [Risk] Adapter mapping drift (Kafka/Rabbit payload handling) → Mitigation: add adapter integration tests for success + malformed payload paths.
- [Risk] Increased number of interfaces/classes → Mitigation: keep ports minimal and aligned to use-case intent, avoid over-abstraction.

## Migration Plan

1. Establish/confirm package boundaries and port interfaces in Application.
2. Refactor use-case service(s) to depend only on ports + domain types.
3. Move/adjust inbound adapters (Kafka, RabbitMQ) to call input ports.
4. Move/adjust outbound adapters (TXT/PDF/XLSX) to implement output ports.
5. Add/adjust tests by layer; keep contract tests for unchanged behavior.
6. Verify full test suite and perform final dependency-direction check.

Rollback strategy:
- Keep refactor in small, reversible commits per adapter/use-case path.
- If a migration step fails, revert the specific adapter wiring change while preserving previous stable behavior.

## Open Questions

- Should port naming remain Spanish domain language for consistency across layers, or use English technical naming for generic adapters?
- Do we enforce architectural boundaries with static analysis tooling now, or keep it as review-time policy in this iteration?
- Is there any legacy endpoint/controller behavior not currently covered by regression tests that needs explicit compatibility tests before applying code refactors?

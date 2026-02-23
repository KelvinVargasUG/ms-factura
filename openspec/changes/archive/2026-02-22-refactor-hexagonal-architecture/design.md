## Context

`ms-factura` currently has partial layering but still couples orchestration and framework-specific concerns in core flows (message consumers, format selection, and file output wiring). The goal is to complete a Ports & Adapters structure while preserving externally observable behavior (API contracts, validations, status semantics, and invoice-generation outcomes).

Constraints:
- No functional regressions in endpoint behavior and DTO contracts.
- Domain must not depend on Spring, messaging libraries, persistence, or transport details.
- Existing Kafka and RabbitMQ channels remain supported.
- Existing TXT fallback behavior must remain unchanged.

## Goals / Non-Goals

**Goals:**
- Establish explicit dependency direction: `infrastructure -> application -> domain`.
- Keep domain entities and domain policies framework-agnostic.
- Define clear input/output ports at use-case boundaries.
- Move framework adapters to infrastructure packages and wire through ports.
- Increase testability with layer-focused tests:
  - Domain/Application unit tests (fast, isolated)
  - Adapter integration tests (framework wiring and serialization/deserialization boundaries)

**Non-Goals:**
- No redesign of business rules for invoice generation.
- No API contract changes (request/response models, status codes, validations).
- No mandatory adoption of new runtime platforms or brokers.
- No broad product feature additions beyond architecture and testability scope.

## Decisions

1. **Introduce explicit ports around use cases and side effects**
   - Decision: Define inbound and outbound interfaces in application/domain boundary (e.g., invoice generation output, event intake orchestration).
   - Rationale: Core logic depends on abstractions; infrastructure can vary independently.
   - Alternatives considered:
     - Keep concrete classes + strategy only: rejected due to persistent coupling.
     - Introduce full CQRS/event-sourcing: rejected as over-scope for current migration.

2. **Keep domain pure and free from framework annotations**
   - Decision: Domain classes remain plain Java objects and domain services/policies contain only business behavior.
   - Rationale: Maximizes portability, test speed, and architectural clarity.
   - Alternatives considered:
     - Continue using framework/lombok-heavy domain models: partially acceptable but rejected for strict hexagonal boundary.

3. **Treat Kafka/Rabbit consumers as input adapters only**
   - Decision: Consumers deserialize/map transport payloads and delegate to use cases via input ports.
   - Rationale: Prevents transport concerns from leaking into application/domain logic.
   - Alternatives considered:
     - Keep deserialization + business orchestration in consumers: rejected for poor separation.

4. **Treat TXT/PDF generators as output adapters behind an output port**
   - Decision: Keep file format implementations in infrastructure and inject through application ports.
   - Rationale: Allows swapping output mechanism (filesystem/cloud/object-store) without rewriting use cases.
   - Alternatives considered:
     - Keep direct concrete dependencies in service: rejected due to low extensibility/testability.

5. **Adopt layered testing strategy as architectural guardrail**
   - Decision: Expand unit tests for domain/application behavior and add integration tests for adapters.
   - Rationale: Verifies both business intent and real framework wiring.
   - Alternatives considered:
     - Rely only on end-to-end tests: rejected due to slower feedback and weak isolation of regressions.

## Risks / Trade-offs

- **[Risk] Regression while moving classes/packages** → **Mitigation:** Preserve contracts first, refactor in small slices, and run tests continuously.
- **[Risk] Hidden behavior changes in payload mapping** → **Mitigation:** Add adapter integration tests for representative Kafka/Rabbit payloads (valid/invalid cases).
- **[Risk] Over-abstraction introducing unnecessary complexity** → **Mitigation:** Start with minimal ports required by current use cases; avoid speculative interfaces.
- **[Risk] Incomplete boundary enforcement** → **Mitigation:** Keep dependency direction explicit in package layout and test strategy; review imports for framework leakage into domain.

## Migration Plan

1. Create/standardize package boundaries and define target dependency direction.
2. Introduce input/output ports for current invoice generation flow.
3. Refactor application service(s) to depend only on ports.
4. Move Kafka/Rabbit message handling into infrastructure input adapters that call input ports.
5. Move TXT/PDF generation into infrastructure output adapters implementing output ports.
6. Keep API and behavior compatibility checks active (contract + fallback behavior).
7. Add/adjust unit tests for domain/application and integration tests for adapters.
8. Validate end-to-end behavior and finalize cleanup of obsolete wiring.

Rollback strategy:
- Maintain small, incremental commits and preserve old behavior paths until equivalent adapter path is verified.
- If regression appears, revert the last migration slice and reintroduce compatibility tests before retry.

## Open Questions

- Should format selection policy remain in application service or move to a dedicated domain policy object?
- Do we define one generic output port for factura generation or separate ports per output format?
- Which adapter integration test boundaries should be mandatory in CI (Rabbit, Kafka, filesystem) versus optional/local?
- Should package names be normalized to an explicit hexagonal convention (`adapters/input`, `adapters/output`, `application/ports`) in this change or phased incrementally?

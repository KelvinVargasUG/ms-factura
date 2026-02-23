## Purpose

Define architectural boundaries and dependency direction for a hexagonal structure while preserving externally observable behavior.

## Requirements

### Requirement: Enforced Layer Dependency Direction
The codebase MUST enforce dependency direction such that Infrastructure depends on Application and Domain, Application depends on Domain, and Domain depends on no framework or adapter layer.

#### Scenario: Domain imports are framework-free
- **WHEN** Domain classes are reviewed for imports
- **THEN** no Domain class SHALL import Spring, messaging clients, persistence libraries, HTTP libraries, or infrastructure adapters

#### Scenario: Application does not depend on infrastructure implementations
- **WHEN** Application use case classes are reviewed
- **THEN** Application classes SHALL depend only on Domain types and declared ports, not concrete adapter classes

### Requirement: Stable External Behavior During Refactor
The refactor MUST preserve externally observable behavior for existing API and messaging flows.

#### Scenario: Existing request/response contract remains unchanged
- **WHEN** existing API consumers invoke current endpoints
- **THEN** the service SHALL return the same DTO shapes, status codes, and validations as before the refactor

#### Scenario: Existing invoice generation result remains equivalent
- **WHEN** a valid invoice event requests format `PDF` or another/null format
- **THEN** the service SHALL produce equivalent results, including TXT fallback for non-PDF values

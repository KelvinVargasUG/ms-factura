## Context

`ms-factura` currently supports invoice output in `PDF` and `TXT` through Ports & Adapters boundaries, with selection logic in the application layer and concrete file generators in infrastructure adapters. New consumers require spreadsheet output (`XLSX`) for analysis and accounting workflows, but external behavior (API contracts, validation behavior, and current fallback semantics) must remain unchanged.

Constraints:
- Keep existing public API behavior unchanged.
- Keep existing PDF/TXT output behavior unchanged.
- Preserve current fallback to TXT for unsupported/unknown formats.
- Integrate XLSX within existing architecture (application ports + infrastructure adapters).

## Goals / Non-Goals

**Goals:**
- Add XLSX invoice output as a first-class format option.
- Implement XLSX generation as an infrastructure output adapter behind output port contracts.
- Keep selection orchestration in the application layer and avoid moving business rules into adapters.
- Extend test coverage for XLSX selection and generation while preserving regression checks for PDF/TXT behavior.

**Non-Goals:**
- No endpoint, DTO, status code, or validation contract changes.
- No redesign of invoice business fields/content model.
- No replacement of existing PDF/TXT implementations.
- No change to fallback semantics beyond supporting explicit `XLSX` selection.

## Decisions

1. **Add a dedicated XLSX output port and adapter**
   - Decision: Introduce an output port type for XLSX and implement it in an infrastructure file adapter.
   - Rationale: Maintains clean dependency direction and keeps infrastructure-specific concerns isolated.
   - Alternatives considered:
     - Reuse a generic untyped adapter only: rejected because explicit format contracts improve clarity and wiring safety.

2. **Use Apache POI for XLSX generation**
   - Decision: Use Apache POI (`poi-ooxml`) to produce `.xlsx` files.
   - Rationale: Mature Java ecosystem support for spreadsheet creation, straightforward tabular writing, and maintainable implementation.
   - Alternatives considered:
     - CSV-only output: rejected because requirement explicitly asks for XLSX.
     - Custom minimal XML packaging: rejected due to complexity/risk.

3. **Preserve application-layer selection policy with minimal extension**
   - Decision: Extend format selection policy to recognize `XLSX` while preserving existing `PDF` path and TXT fallback for all other values.
   - Rationale: Keeps behavior stable and minimizes risk of regressions.
   - Alternatives considered:
     - Push selection into adapters: rejected because it mixes orchestration and I/O concerns.

4. **Keep spreadsheet structure aligned with current invoice semantics**
   - Decision: Generate a tabular sheet containing customer header and line items (`producto`, `cantidad`, `precio`, `subtotal`) plus total.
   - Rationale: Meets consumer analysis/reporting needs while preserving domain model semantics.
   - Alternatives considered:
     - Add extra computed/business fields: rejected as scope expansion.

5. **Add layered test coverage for XLSX path and regression compatibility**
   - Decision: Add/adjust application tests for format routing and adapter tests for XLSX file generation; preserve existing PDF/TXT tests.
   - Rationale: Ensures new behavior and non-regression under the same architecture rules.
   - Alternatives considered:
     - Only E2E tests: rejected due to slower and less isolated feedback.

## Risks / Trade-offs

- **[Risk] Dependency footprint increase from Apache POI** → **Mitigation:** Add only required POI modules and keep usage isolated to XLSX adapter.
- **[Risk] Behavior regression in format routing** → **Mitigation:** Add explicit tests for `XLSX`, `PDF`, and fallback formats before/after changes.
- **[Risk] Spreadsheet content mismatch with invoice semantics** → **Mitigation:** Validate generated sheet headers/rows and totals in adapter tests.
- **[Risk] File output side effects in tests (`/tmp/facturas`)** → **Mitigation:** Keep deterministic assertions and cleanly scope test checks to created extension/type.

## Migration Plan

1. Add XLSX dependency (`poi-ooxml`) in build configuration.
2. Add XLSX output port contract in application output ports.
3. Implement XLSX infrastructure adapter under output adapters package.
4. Extend application selection logic to route `XLSX` to the new port.
5. Keep PDF and TXT routing unchanged, including fallback semantics.
6. Add/adjust tests:
   - Application routing tests for `XLSX` + regression for `PDF` and fallback.
   - Output adapter tests for XLSX file generation and content structure checks.
7. Run full test suite and verify no API/contract regressions.

Rollback strategy:
- Revert the XLSX-specific port/adapter wiring and dependency if regressions appear.
- Keep previous PDF/TXT behavior path intact as known stable baseline.

## Open Questions

- Should XLSX generation be represented as a dedicated port type (`XlsxFacturaGeneratorPort`) or a generic strategy keyed by format in application layer?
**Decision:** XLSX generation will be implemented as a dedicated output port (e.g., XlsxFacturaGeneratorPort) in the application boundary. This allows the spreadsheet capability to evolve independently from other formats and avoids overloading existing output contracts.

- Do consumers require one worksheet only, or future multi-sheet.
**Decision:** structure (e.g., summary + detail) should be anticipated?
The initial implementation will generate a single worksheet per invoice. The sheet will contain header information (customer, invoice metadata) and a tabular detail section (items, quantities, prices, subtotals, total). Support for multiple worksheets is explicitly out of scope for this version.

- Should spreadsheet localization/number format conventions be fixed **Decision:** now or deferred to a later capability?
All monetary and numeric values in the spreadsheet will be written as numeric cell values (not text). Cells will use a fixed two-decimal format to ensure consistency across environments and spreadsheet applications. Localization-specific formatting is deferred to future enhancements.

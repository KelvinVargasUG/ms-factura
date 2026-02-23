## Why

Some invoice consumers require spreadsheet output to support analysis, reporting, and accounting-tool integration, and PDF/TXT alone is no longer sufficient for those workflows. Adding XLSX output now solves this operational gap while preserving all current contracts and existing output behavior.

## What Changes

- Add support for generating invoices in spreadsheet format (`XLSX`) in addition to current `PDF` and `TXT` outputs.
- Preserve existing behavior and contracts:
  - Keep current API/request-response behavior unchanged.
  - Keep current status codes and validations unchanged.
  - Keep current PDF generation behavior unchanged.
  - Keep current TXT fallback behavior unchanged for non-supported/unknown formats.
- Integrate XLSX generation through the existing Ports & Adapters architecture by adding a new output adapter behind output port contracts.
- Extend test coverage to include XLSX output selection and generation while maintaining regression coverage for PDF/TXT and fallback rules.

## Capabilities

### New Capabilities
- `xlsx-invoice-output`: Generate invoice files in XLSX format with tabular invoice data (customer, item lines, quantities, prices, subtotals, total).
- `spreadsheet-output-adapter`: Add an infrastructure output adapter for XLSX generation wired through application output ports.
- `xlsx-regression-coverage`: Add tests that verify XLSX selection/generation and preserve compatibility for existing PDF/TXT behavior.

### Modified Capabilities
- None (no existing capability specs currently defined in `openspec/specs/`).

## Impact

- Affected code areas:
  - `src/main/java/com/foodtech/ms_factura/application/**`
  - `src/main/java/com/foodtech/ms_factura/application/ports/output/**`
  - `src/main/java/com/foodtech/ms_factura/infrastructure/adapters/output/**`
  - `src/test/java/com/foodtech/ms_factura/application/**`
  - `src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/**`
- Public API impact:
  - No intended changes to endpoints, DTOs, status codes, or validations.
- Dependencies/systems:
  - May require adding an XLSX writing library for Java.
  - Existing PDF/TXT generation paths remain active.

## 1. Setup and Baseline Safety

- [x] 1.1 Document current invoice output baseline (PDF path, TXT fallback behavior, and unchanged API contract expectations)
- [ ] 1.2 Add XLSX library dependency (Apache POI `poi-ooxml`) to the Gradle build and verify project compiles
- [x] 1.3 Confirm no controller/DTO/status/validation contract changes are introduced by this feature scope

## 2. Ports and Application Routing

- [x] 2.1 Add an XLSX output port contract in the application output ports package (e.g., `XlsxFacturaGeneratorPort`)
- [x] 2.2 Extend application format-selection policy to recognize `XLSX` case-insensitively
- [x] 2.3 Refactor `GenerarFacturaService` wiring/routing to invoke XLSX output port for `XLSX` while preserving existing PDF and TXT fallback behavior
- [x] 2.4 Add/adjust application unit tests to verify routing for `XLSX`, `PDF`, and TXT fallback paths

## 3. Infrastructure XLSX Adapter

- [x] 3.1 Implement XLSX file output adapter under `infrastructure/adapters/output/file` behind the XLSX output port
- [x] 3.2 Generate spreadsheet structure with required invoice data (customer info, item rows, quantities, prices, subtotals, total)
- [x] 3.3 Write numeric and monetary values as numeric cells with fixed two-decimal formatting
- [x] 3.4 Keep filesystem concerns isolated in the adapter and preserve existing output directory conventions

## 4. Adapter and Regression Testing

- [x] 4.1 Add XLSX adapter tests verifying `.xlsx` file creation and expected worksheet/table content
- [x] 4.2 Add tests verifying numeric cells/format conventions for price, subtotal, and total values
- [ ] 4.3 Re-run/adjust existing PDF and TXT adapter tests to ensure no regressions
- [x] 4.4 Ensure compatibility coverage confirms unchanged fallback semantics for null/unknown formats

## 5. Validation and Cleanup

- [ ] 5.1 Run full test suite (`gradle test`) and resolve feature-related failures
- [x] 5.2 Remove temporary/obsolete wiring introduced during migration and keep dependency direction `infrastructure -> application -> domain`
- [x] 5.3 Update change artifacts/checklist status and verify implementation remains aligned with proposal/spec/design

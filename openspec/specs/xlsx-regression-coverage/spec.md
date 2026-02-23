## Purpose

Define regression and automated coverage requirements to ensure XLSX support is validated and existing non-XLSX behavior remains stable.

## Requirements

### Requirement: XLSX Selection and Generation Are Covered by Automated Tests
The test suite MUST include coverage for XLSX format routing and successful spreadsheet generation.

#### Scenario: Application routing test covers XLSX selection
- **WHEN** application use-case orchestration is tested with `formato` set to `XLSX`
- **THEN** assertions SHALL verify that the XLSX output port is invoked

#### Scenario: Adapter test covers XLSX file generation
- **WHEN** XLSX output adapter is executed in tests with a valid invoice
- **THEN** assertions SHALL verify that a `.xlsx` file is produced and contains expected worksheet structure/data

### Requirement: Compatibility Regressions Are Guarded
The test suite MUST continue verifying existing behavior compatibility for non-XLSX paths.

#### Scenario: Existing PDF path remains tested
- **WHEN** tests run for invoice output with `PDF` format
- **THEN** assertions SHALL confirm unchanged PDF routing and generation behavior

#### Scenario: TXT fallback remains tested
- **WHEN** tests run with null/unknown format values
- **THEN** assertions SHALL confirm unchanged fallback to TXT behavior

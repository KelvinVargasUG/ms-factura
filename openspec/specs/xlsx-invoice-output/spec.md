## Purpose

Define expected behavior for generating invoices in XLSX format, including required spreadsheet structure and numeric representation.

## Requirements

### Requirement: System Generates Invoice in XLSX Format
The system MUST generate invoice files in `XLSX` format when the requested output format is `XLSX`.

#### Scenario: XLSX format routes to spreadsheet output
- **WHEN** an invoice is processed with `formato` equal to `XLSX` (case-insensitive)
- **THEN** the application SHALL invoke XLSX generation through the spreadsheet output contract

#### Scenario: Generated spreadsheet contains invoice table data
- **WHEN** an XLSX invoice is generated successfully
- **THEN** the spreadsheet SHALL include customer data, item rows, quantities, prices, subtotals, and total amount in tabular form

### Requirement: Spreadsheet Uses Stable Numeric Cell Representation
The system MUST write monetary and numeric values in the spreadsheet as numeric cells with two-decimal display formatting.

#### Scenario: Spreadsheet numeric values are not written as text
- **WHEN** unit prices, subtotals, and totals are exported to XLSX
- **THEN** those values SHALL be written as numeric cell types with fixed two-decimal formatting

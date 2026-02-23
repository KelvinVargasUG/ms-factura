## ADDED Requirements

### Requirement: XLSX Output Is Implemented as Infrastructure Adapter
Spreadsheet generation MUST be implemented as an infrastructure output adapter behind application output ports.

#### Scenario: Application depends on spreadsheet output port, not concrete library
- **WHEN** spreadsheet output capability is wired into invoice generation flow
- **THEN** application services SHALL depend on an XLSX output port interface and SHALL NOT depend on spreadsheet library classes directly

#### Scenario: XLSX adapter encapsulates file and library concerns
- **WHEN** an XLSX invoice is generated
- **THEN** spreadsheet library usage and filesystem I/O SHALL remain inside infrastructure adapter code

### Requirement: Existing Output Paths Remain Backward Compatible
Adding XLSX adapter MUST NOT alter existing PDF and TXT behavior.

#### Scenario: PDF request behavior remains unchanged
- **WHEN** `formato` is `PDF`
- **THEN** the system SHALL continue using the PDF output path with equivalent behavior

#### Scenario: Unknown/null format fallback remains TXT
- **WHEN** `formato` is null, empty, or unsupported
- **THEN** the system SHALL continue falling back to TXT output

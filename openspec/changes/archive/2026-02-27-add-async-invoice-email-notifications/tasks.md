## 1. Baseline and TDD Setup

- [x] 1.1 Confirm current invoice-generation behavior and identify non-regression assertions (response behavior must remain unchanged)
- [x] 1.2 Add failing unit tests for notification orchestration contracts in Application (event publication, channel delegation, non-blocking semantics)
- [x] 1.3 Add failing tests for notification message template rendering (subject/body with invoice metadata)

## 2. Notification Domain/Application Contracts

- [x] 2.1 Create notification models/value objects for message payload and attachment references (prefer Path/stream semantics)
- [x] 2.2 Define notification ports/strategies in Application (`NotificationChannel` and dispatch service contract)
- [x] 2.3 Add channel-selection policy in Application for single-channel mode (`notification.channel=email`) and safe handling of unsupported values
- [x] 2.4 Refactor Application orchestration to emit `FacturaGeneradaEvent` after successful file generation without changing public behavior

## 3. Async Event Flow

- [x] 3.1 Implement application event publisher wiring for `FacturaGeneradaEvent`
- [x] 3.2 Implement asynchronous listener that transforms event data into notification command/message and delegates to notification service
- [x] 3.3 Configure async executor abstraction to allow deterministic execution in tests
- [x] 3.4 Add logging for dispatch result (`OK`/`Fallo`) with invoice reference and cause

## 4. Infrastructure Email Adapter

- [x] 4.1 Add Spring Mail dependency and base SMTP wiring if missing
- [x] 4.2 Implement `EmailNotificationChannel` adapter using `JavaMailSender` with support for one or multiple attachments (PDF/XLSX/TXT)
- [x] 4.3 Ensure SMTP adapter encapsulates provider concerns only (transport, template application, attachment I/O)
- [x] 4.4 Handle SMTP failures by logging error details and preventing propagation to break invoice-generation completion behavior

## 5. Configuration and Security

- [x] 5.1 Add externalized properties for SMTP (`host`, `port`, `username`, `password`, `from`) and notification templates (`subject`, `body`)
- [x] 5.2 Ensure credentials are sourced from environment variables/secret integration (no hardcoded secrets)
- [x] 5.3 Add feature/config toggles for notification enablement and channel selection
- [x] 5.4 Validate startup behavior for missing/invalid notification configuration with safe defaults/warnings

## 6. Test Coverage and Validation

- [x] 6.1 Make Application unit tests pass for event publication, channel strategy delegation, and notification failure isolation
- [x] 6.2 Add integration tests for `EmailNotificationChannel` (template rendering, attachment inclusion, SMTP invocation with mocked/fake mail sender)
- [x] 6.3 Add regression tests to prove invoice-generation observable behavior is unchanged when notification succeeds or fails
- [x] 6.4 Run full test suite and fix only issues related to this change

## 7. Finalization

- [x] 7.1 Update/clean wiring to preserve hexagonal boundaries (`infrastructure -> application -> domain`)
- [x] 7.2 Review logs/observability fields for delivery outcome consistency
- [x] 7.3 Re-run OpenSpec apply/status checks and confirm readiness for `/opsx:apply`

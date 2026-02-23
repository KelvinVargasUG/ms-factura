## External Contract Inventory (Baseline)

Date: 2026-02-23
Change: `refactor-hexagonal-ports-adapters-2026-02-23`

### Public API Surface

- HTTP controllers/endpoints in current codebase: none found.
- Request/response DTO contracts exposed via HTTP: none.
- Endpoint status-code and validation contracts: not applicable (no HTTP API layer in this service).

### Messaging Entry Points

- Kafka inbound adapter:
  - Class: `KafkaConsumerAdapter`
  - Topic: `create-factura`
  - Input contract: message JSON containing `FoodEvent` with `payload` as serialized `Factura`
  - Observable behavior:
    - Success message: `Factura procesada exitosamente para: <cliente>`
    - JSON parse error path: response includes `Error al procesar la factura`
    - Generation error path: response includes `Error al generar la factura`

- RabbitMQ inbound adapter:
  - Class: `RabbitMqConsumer`
  - Queue: `${foodtech.rabbitmq.queue}`
  - Input contract: `FoodEvent` with `payload` as serialized `Factura`
  - Observable behavior:
    - Delegates to use case on valid payload
    - Logs and swallows exceptions on mapping/processing failures

### Output Behavior Contracts

- Format selection policy:
  - `PDF` (case-insensitive) => PDF generator
  - `XLSX` (case-insensitive) => XLSX generator
  - null/empty/unknown => TXT generator fallback
- Output adapters:
  - TXT: writes `.txt` invoice file under `/tmp/facturas/`
  - PDF: writes `.pdf` invoice file under `/tmp/facturas/`
  - XLSX: writes `.xlsx` invoice file under `/tmp/facturas/`

### Compatibility Scope for Refactor

- Keep messaging entry-point behavior equivalent (mapping, delegation, error-path semantics).
- Keep format selection and fallback behavior unchanged.
- Keep file output semantics and adapter-level outcomes unchanged.

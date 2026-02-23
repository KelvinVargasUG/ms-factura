# ms-factura — Project Context

## Overview
`ms-factura` is a Spring Boot microservice that consumes event messages (Kafka and RabbitMQ), deserializes invoice payloads, and generates invoice files in TXT or PDF format.

Core behavior today:
- Input event wrapper: `FoodEvent` (contains `payload` JSON as `String`)
- Payload target: `Factura`
- Format rule: if `formato == PDF` (case-insensitive), generate PDF; otherwise fallback to TXT
- Output files are written under `/tmp/facturas/` with unique UUID names

## Tech Stack
- Language: Java 17
- Framework: Spring Boot 3.5.11
- Build tool: Gradle
- Messaging:
  - Spring for Apache Kafka
  - Spring AMQP (RabbitMQ)
- Web: Spring Web
- Validation: Spring Validation
- PDF generation: iText 7 (`com.itextpdf:itext7-core:7.2.5`)
- Boilerplate reduction: Lombok
- Testing:
  - JUnit 5
  - Mockito
  - Spring Boot Test

## Runtime and Configuration
Main config file: `src/main/resources/application.properties`

Important properties:
- `spring.application.name=ms-factura`
- `server.port=8082`
- Kafka:
  - `spring.kafka.bootstrap-servers=localhost:9092`
  - `spring.kafka.consumer.group-id=ms-factura-group`
- RabbitMQ:
  - `spring.rabbitmq.host=localhost`
  - `spring.rabbitmq.port=5672`
  - `foodtech.rabbitmq.exchange=foodtech.exchange`
  - `foodtech.rabbitmq.queue=foodtech.queue`
  - `foodtech.rabbitmq.routingkey=foodtech.routingkey`

## Current Architecture (As-Is)
Base package: `com.foodtech.ms_factura`

Main package layout:
- `domain/`: business models (`Factura`, `FoodEvent`, `Producto`)
- `application/`: use-case API and orchestration (`GenerarFacturaUseCase`, `GenerarFacturaService`)
- `infrastructure/`: technical implementations (`FileTxtFacturaGenerator`, `FilePdfFacturaGenerator`, strategy interface)
- `infrastructure/adapters/input/rabbitmq/`: RabbitMQ inbound adapter (`RabbitMqConsumer`)
- `KafkaConsumer` currently sits in the root package (candidate to move under input adapters in future refactor)

## Coding Conventions
- Business naming is in Spanish (`Factura`, `GenerarFacturaService`, `Producto`)
- Constructor injection via Lombok `@RequiredArgsConstructor`
- Logging with SLF4J + `@Slf4j`; log messages are Spanish
- Domain model classes currently use Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- Keep behavior-compatible changes by default unless explicitly requested otherwise
- Keep edits focused and minimal; avoid unrelated refactors

## Error Handling Conventions
- Consumers catch processing errors and log them
- File generator adapters wrap `IOException` into `RuntimeException`
- Maintain existing fallback behavior (TXT default) unless requirement explicitly changes

## Testing Conventions
Test root: `src/test/java`

Current style:
- Unit tests with Mockito for application services and collaborators
- Verify interaction-based behavior for strategy selection (TXT/PDF/default)
- Spring context tests for bootstrapping where needed

Target testing direction for architecture changes:
- Domain tests: pure unit tests, no framework wiring
- Application tests: unit tests with mocked ports
- Adapter tests: integration tests for Kafka/Rabbit/file adapters

## Frequent Commands
- Run app: `./gradlew bootRun`
- Run tests: `./gradlew test`
- Build: `./gradlew build`

## OpenSpec Guidance for This Repo
When generating proposals/specs/design/tasks, optimize for:
1. Clear boundaries between `domain`, `application`, and `infrastructure`
2. Domain free of framework dependencies
3. Use ports/interfaces at boundaries; adapters implement them
4. Preserve external behavior unless change explicitly says otherwise
5. Add tests at the right layer (domain/application unit, adapter integration)

### Typical capability examples
- New output format:
  - Add new output adapter implementing output port
  - Keep use-case orchestration stable
- New input channel:
  - Add input adapter under `infrastructure/adapters/input`
  - Reuse existing application use case
- Event contract change:
  - Limit mapping/deserialization changes to adapters
  - Keep domain and use-case signatures stable where possible

## Notes
There are refactor docs in the repository describing a more advanced hexagonal target state; OpenSpec artifacts should be grounded on the actual current code and evolve incrementally.

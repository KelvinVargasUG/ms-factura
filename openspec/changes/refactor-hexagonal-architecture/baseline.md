## Baseline de comportamiento (pre-refactor)

### Contratos y comportamiento externo que se preservan
- Regla de formato:
  - `PDF` (case-insensitive) genera factura PDF.
  - Cualquier otro valor (`TXT`, `null`, desconocido) usa fallback a TXT.
- Flujo Kafka:
  - Entrada: mensaje JSON con wrapper `FoodEvent` y `payload` serializado.
  - Respuesta exitosa: `Factura procesada exitosamente para: <cliente>`.
  - Respuesta por error de deserialización/procesamiento: cadena con prefijo `Error al procesar la factura:` o `Error al generar la factura:`.
- Flujo RabbitMQ:
  - Entrada: `FoodEvent` por cola configurada.
  - Efecto: delega generación de factura vía caso de uso.

### Endpoints y entry points actuales
- HTTP:
  - No se identificaron controladores REST en `src/main/java`.
- Mensajería:
  - Kafka listener: tópico `create-factura` en adaptador de entrada Kafka.
  - Rabbit listener: cola `${foodtech.rabbitmq.queue}` en adaptador de entrada RabbitMQ.

### Cobertura de regresión agregada/ajustada
- Dominio:
  - `FacturaFormatoPolicyTest` valida selección PDF/fallback.
- Aplicación:
  - `GenerarFacturaServiceTest` valida orquestación por puertos y fallback TXT.
- Adaptadores de entrada:
  - `KafkaConsumerAdapterTest` valida mapeo `FoodEvent -> Factura`, delegación y rutas de error.
  - `RabbitMqConsumerTest` valida delegación y ruta de error al mapear payload.
- Adaptadores de salida:
  - `FileTxtFacturaGeneratorTest` y `FilePdfFacturaGeneratorTest` validan generación de archivos.

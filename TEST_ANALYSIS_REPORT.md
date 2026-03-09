# 🔬 Reporte de Análisis de Pruebas — Caja Negra vs Caja Blanca

**Proyecto:** ms-factura  
**Fecha:** 2026-03-08  
**Total de clases de test:** 16  
**Total de tests:** 87  

---

## 📖 Definiciones

| Tipo | Definición |
|------|-----------|
| **⬛ Caja Negra** | Se prueba exclusivamente a través de la interfaz pública: se proporcionan entradas y se verifican salidas observables. No se manipula ni inspecciona el estado interno ni las dependencias. |
| **⬜ Caja Blanca** | Se requiere conocimiento de la estructura interna del componente: se usan mocks para dependencias internas, `ReflectionTestUtils` para campos privados, `verify()` para comprobar interacciones internas, o se inspecciona estructura de paquetes/bytecode. |

---

## 📊 Resumen General

| Tipo | Clases | Tests | Porcentaje |
|------|-------:|------:|-----------:|
| ⬛ Caja Negra | 8 | 42 | 48% |
| ⬜ Caja Blanca | 8 | 45 | 52% |
| **Total** | **16** | **87** | **100%** |

---

## ⬛ Pruebas de Caja Negra (8 clases — 42 tests)

### 1. `ProductoTest` — 12 tests | `@Tag("unit")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/domain/ProductoTest.java`

**Justificación:** Instancia la clase `Producto` usando constructores públicos y verifica el comportamiento de getters/setters. No usa mocks, no accede a campos privados. Solo valida **entrada → salida observable**.

| Test | Qué verifica |
|------|-------------|
| `shouldInitializeWithDefaultConstructor` | Valores por defecto del constructor vacío |
| `shouldInitializeWithAllArgsConstructor` | Constructor con parámetros |
| `shouldSetAndGetNombre` | Setter/getter de nombre |
| `shouldSetAndGetCantidad` | Setter/getter de cantidad |
| `shouldSetAndGetPrecio` | Setter/getter de precio |
| `shouldHandleNullNombre` | Nombre null |
| `shouldHandleZeroCantidad` | Cantidad cero |
| `shouldHandleNegativeCantidad` | Cantidad negativa |
| `shouldHandleZeroPrecio` | Precio cero |
| `shouldHandleNegativePrecio` | Precio negativo |
| `shouldHandleLargePrecio` | Precio alto |
| `shouldUpdateMultipleFields` | Actualización de múltiples campos |

---

### 2. `FacturaTest` — 16 tests | `@Tag("unit")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/domain/FacturaTest.java`

**Justificación:** Idéntico patrón que `ProductoTest`. Prueba constructores públicos y getters/setters de la entidad `Factura`. Solo API pública del dominio, sin dependencias internas.

| Test | Qué verifica |
|------|-------------|
| `shouldInitializeWithDefaultConstructor` | Valores por defecto |
| `shouldInitializeWithAllArgsConstructor` | Constructor con parámetros |
| `shouldSetAndGetNombreCliente` | Nombre del cliente |
| `shouldSetAndGetEmailCliente` | Email del cliente |
| `shouldSetAndGetListaProductos` | Lista de productos |
| `shouldSetAndGetTotal` | Total de la factura |
| `shouldSetAndGetFormato` | Formato (PDF/TXT/XLSX) |
| `shouldHandleNullValues` | Valores null en todos los campos |
| `shouldHandleEmptyListaProductos` | Lista vacía |
| `shouldHandleZeroTotal` | Total cero |
| `shouldHandleNegativeTotal` | Total negativo |
| `shouldHandleLargeTotal` | Total grande |
| `shouldHandleMultipleProductos` | Lista con 3 productos |
| `shouldUpdateMultipleFields` | Actualización de múltiples campos |
| `shouldOverwriteListaProductos` | Sobrescritura de lista |
| `shouldConstructorNotSetEmailCliente` | Constructor no setea emailCliente |

---

### 3. `FoodEventTest` — 3 tests | `@Tag("unit")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/domain/FoodEventTest.java`

**Justificación:** Aunque usa `@InjectMocks`, el mock (`unusedDependency`) no participa en ningún test. Los tests son puramente de getters/setters sobre la entidad `FoodEvent`.

| Test | Qué verifica |
|------|-------------|
| `shouldInitializeWithAllArgsConstructor` | Constructor con parámetros |
| `shouldSetAndGetValues` | Setters y getters |
| `shouldAllowNullValues` | Valores null |

---

### 4. `FacturaFormatoPolicyTest` — 3 tests | `@Tag("unit")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/domain/FacturaFormatoPolicyTest.java`

**Justificación:** Instancia directa de `FacturaFormatoPolicy`, invoca `isPdf()` / `isXlsx()` con diferentes inputs y verifica solo el **retorno booleano** sin conocer la implementación interna.

| Test | Qué verifica |
|------|-------------|
| `shouldDetectPdfCaseInsensitive` | Detecta formato "pdf" (case insensitive) |
| `shouldDetectXlsxCaseInsensitive` | Detecta formato "xlsx" (case insensitive) |
| `shouldReturnFalseForNullOrUnknownFormat` | Retorna false para null o formatos desconocidos |

---

### 5. `FileXlsxFacturaGeneratorTest` — 4 tests | `@Tag("component")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/file/FileXlsxFacturaGeneratorTest.java`

**Justificación:** Llama `generator.generar(factura)` y luego **lee el archivo XLSX real** generado en disco para verificar su contenido (celdas, formato numérico). No mockea nada: verifica el **artefacto de salida**, no cómo se construyó internamente.

| Test | Qué verifica |
|------|-------------|
| `testGenerar` | Contenido correcto del XLSX (celdas, valores) |
| `shouldWriteMoneyAsNumericCellsWithTwoDecimalFormat` | Formato numérico "0.00" en celdas de dinero |
| `shouldCreateDirectoryWhenItDoesNotExist` | Crea directorio si no existe |
| `shouldThrowRuntimeExceptionWhenFacturaPathIsAFile` | Error cuando la ruta es un archivo |

---

### 6. `FileTxtFacturaGeneratorTest` — 3 tests | `@Tag("component")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/file/FileTxtFacturaGeneratorTest.java`

**Justificación:** Llama `generar()` y **lee el archivo TXT** para validar que contiene las cadenas esperadas. Verifica el **output real** sin inspeccionar la implementación.

| Test | Qué verifica |
|------|-------------|
| `testGenerar` | Contenido correcto del TXT |
| `shouldCreateDirectoryWhenItDoesNotExist` | Crea directorio si no existe |
| `shouldThrowRuntimeExceptionWhenFacturaPathIsAFile` | Error cuando la ruta es un archivo |

---

### 7. `FilePdfFacturaGeneratorTest` — 3 tests | `@Tag("component")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/file/FilePdfFacturaGeneratorTest.java`

**Justificación:** Llama `generar()` y verifica que el **archivo PDF existe y tiene tamaño mayor a 0**. No inspecciona cómo se genera internamente.

| Test | Qué verifica |
|------|-------------|
| `testGenerar` | Archivo PDF existe y tiene contenido |
| `shouldCreateDirectoryWhenItDoesNotExist` | Crea directorio si no existe |
| `shouldThrowRuntimeExceptionWhenFacturaPathIsAFile` | Error cuando la ruta es un archivo |

---

### 8. `MsFacturaApplicationTests` — 1 test | `@Tag("integration")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/MsFacturaApplicationTests.java`

**Justificación:** Usa `@SpringBootTest` para levantar el contexto completo de Spring. Solo verifica que **la aplicación arranca sin errores**. Trata toda la aplicación como una unidad opaca.

| Test | Qué verifica |
|------|-------------|
| `contextLoads` | El contexto de Spring arranca correctamente |

---

## ⬜ Pruebas de Caja Blanca (8 clases — 45 tests)

### 1. `GenerarFacturaServiceTest` — 5 tests | `@Tag("unit")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/application/GenerarFacturaServiceTest.java`

**Justificación:** Usa `@Mock` de puertos internos (`TxtPort`, `PdfPort`, `XlsxPort`, `EventPublisher`) + `@InjectMocks`. Verifica con `verify()` y `verifyNoInteractions()` **qué dependencia interna se invoca** según el formato recibido.

**Indicadores de caja blanca:**
- `@Mock` de dependencias internas (puertos)
- `@InjectMocks` para inyectar mocks
- `verify(txtPort).generar(factura)` — verifica interacción interna
- `verifyNoInteractions(pdfPort, xlsxPort)` — verifica que NO se llamaron otras dependencias

| Test | Qué verifica |
|------|-------------|
| `testGenerarFacturaTxt` | Formato TXT → delega a `txtPort` |
| `testGenerarFacturaPdf` | Formato PDF → delega a `pdfPort` |
| `testGenerarFacturaXlsx` | Formato XLSX → delega a `xlsxPort` |
| `testGenerarFacturaDefaultTxt` | Formato null → fallback a `txtPort` |
| `testGenerarFacturaUnknownFormatFallbackTxt` | Formato desconocido → fallback a `txtPort` |

---

### 2. `NotificationDispatchServiceTest` — 7 tests | `@Tag("unit")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/application/notification/NotificationDispatchServiceTest.java`

**Justificación:** Usa `ReflectionTestUtils.setField` para manipular campos `@Value` privados (`notificationEnabled`, `configuredChannel`). Mockea canales de notificación y verifica con `verify()` las interacciones internas.

**Indicadores de caja blanca:**
- `ReflectionTestUtils.setField(service, "notificationEnabled", true)` — acceso a campo privado
- `verify(emailChannel).send(message)` — verifica interacción interna
- `verifyNoInteractions(emailChannel, smsChannel)` — verifica decisiones internas

| Test | Qué verifica |
|------|-------------|
| `shouldDispatchToConfiguredChannel` | Despacha al canal configurado |
| `shouldSkipDispatchWhenNotificationsAreDisabled` | No despacha si está deshabilitado |
| `shouldSkipDispatchWhenChannelIsUnsupported` | No despacha si el canal no existe |
| `shouldMatchChannelNameIgnoringCase` | Match case-insensitive del canal |
| `shouldNotPropagateChannelFailure` | Absorbe excepciones del canal |
| `shouldThrowWhenConfiguredChannelIsNull` | NullPointerException si canal es null |
| `shouldThrowWhenMessageIsNull` | NullPointerException si mensaje es null |

---

### 3. `NotificationConfigurationValidatorTest` — 7 tests | `@Tag("unit")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/application/notification/NotificationConfigurationValidatorTest.java`

**Justificación:** Usa `ReflectionTestUtils.setField` para setear campos `@Value` privados. Prueba cada combinación de estado interno (`enabled` + `channel`) para ejercitar los **caminos de decisión internos** del validador.

**Indicadores de caja blanca:**
- `ReflectionTestUtils.setField(validator, "notificationEnabled", true)` — acceso directo a estado interno
- `ReflectionTestUtils.setField(validator, "configuredChannel", "sms")` — configuración de branches internos

| Test | Qué verifica |
|------|-------------|
| `shouldValidateWithoutThrowingWhenConfigurationIsIncomplete` | Config incompleta no lanza |
| `shouldReturnEarlyWhenNotificationsAreDisabled` | Return temprano si deshabilitado |
| `shouldLogWarnWhenChannelIsNotEmail` | Warn cuando canal no es email |
| `shouldValidateWithoutThrowingWhenChannelIsNull` | Canal null no lanza |
| `shouldValidateWithoutThrowingWhenChannelIsBlank` | Canal blank no lanza |
| `shouldValidateSuccessfullyWithValidConfiguration` | Config válida OK |
| `shouldHandleCaseInsensitiveChannelName` | Canal "EMAIL" en mayúsculas |

---

### 4. `FacturaGeneradaEventListenerTest` — 6 tests | `@Tag("unit")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/application/notification/FacturaGeneradaEventListenerTest.java`

**Justificación:** Usa `ReflectionTestUtils.setField` para setear templates privados y `ArgumentCaptor` para capturar el `NotificationMessage` construido internamente. Inspecciona **cómo se construye** el mensaje, no solo si se envía.

**Indicadores de caja blanca:**
- `ReflectionTestUtils.setField(listener, "subjectTemplate", ...)` — acceso a campo privado
- `ArgumentCaptor<NotificationMessage>` — captura argumento de invocación interna
- Inspecciona campos del mensaje construido internamente (`asunto`, `cuerpo`, `attachments`)

| Test | Qué verifica |
|------|-------------|
| `shouldBuildAndDispatchNotificationMessage` | Construye y despacha mensaje correctamente |
| `shouldSkipDispatchWhenRecipientIsMissing` | No despacha si email está vacío |
| `shouldSkipDispatchWhenFacturaIsNull` | No despacha si factura es null |
| `shouldSkipDispatchWhenRecipientIsBlank` | No despacha si email es solo espacios |
| `shouldRenderEmptyValuesWhenDataIsMissing` | Renderiza placeholders vacíos |
| `shouldRenderEmptyCustomerWhenNombreClienteIsNull` | Nombre null → vacío en template |

---

### 5. `EmailNotificationChannelTest` — 9 tests | `@Tag("component")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/notification/EmailNotificationChannelTest.java`

**Justificación:** Usa `ReflectionTestUtils.setField` para campo privado `from`, `@Mock JavaMailSender` y `verify(mailSender).send()`. Conoce que internamente usa `MimeMessageHelper` y valida `from` antes de enviar.

**Indicadores de caja blanca:**
- `ReflectionTestUtils.setField(channel, "from", ...)` — acceso a campo privado
- `@Mock JavaMailSender` — reemplaza dependencia interna
- `verify(mailSender).send(any(MimeMessage.class))` — verifica interacción interna
- Prueba validaciones internas de `from` (null, blank, inválido)

| Test | Qué verifica |
|------|-------------|
| `shouldSendEmailWithAttachment` | Envía email con adjunto |
| `shouldThrowWhenFromIsNull` | Lanza si `from` es null |
| `shouldThrowWhenFromIsBlank` | Lanza si `from` es blank |
| `shouldSkipNullAttachments` | Envía sin adjuntos |
| `shouldSkipNullAttachmentEntryInsideList` | Salta entries null en lista |
| `shouldThrowWhenAttachmentDoesNotExist` | Lanza si adjunto no existe |
| `shouldThrowRuntimeExceptionOnMessagingException` | Wrappea excepciones SMTP |
| `shouldWrapMessagingExceptionWhenFromAddressIsInvalid` | Wrappea MessagingException |
| `shouldSendEmailWithMultipleAttachments` | Envía con múltiples adjuntos |

---

### 6. `KafkaConsumerAdapterTest` — 3 tests | `@Tag("integration")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/infrastructure/adapters/input/kafka/KafkaConsumerAdapterTest.java`

**Justificación:** Usa `@Mock GenerarFacturaUseCase` y `verify(generarFacturaUseCase).generarFactura()` para verificar que internamente **delega al use case** después de deserializar el JSON. Conoce el flujo interno: consume → parse → delegate.

**Indicadores de caja blanca:**
- `@Mock GenerarFacturaUseCase` — reemplaza dependencia interna
- `verify(generarFacturaUseCase).generarFactura(any(Factura.class))` — verifica delegación interna

| Test | Qué verifica |
|------|-------------|
| `testConsumeSuccess` | JSON válido → delega al use case |
| `testConsumeJsonProcessingException` | JSON inválido → mensaje de error |
| `testConsumeGeneralException` | Excepción del use case → mensaje de error |

---

### 7. `RabbitMqConsumerTest` — 3 tests | `@Tag("integration")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/infrastructure/adapters/input/rabbitmq/RabbitMqConsumerTest.java`

**Justificación:** Mismo patrón que `KafkaConsumerAdapterTest`. Usa `@Mock` del use case + `verify()` / `verifyNoInteractions()` para probar la **orquestación interna** del consumer.

**Indicadores de caja blanca:**
- `@Mock GenerarFacturaUseCase` — reemplaza dependencia interna
- `verify(generarFacturaUseCase).generarFactura(any(Factura.class))` — verifica delegación
- `verifyNoInteractions(generarFacturaUseCase)` — verifica que NO se delega

| Test | Qué verifica |
|------|-------------|
| `shouldMapAndDelegateWhenEventPayloadIsValid` | Payload válido → delega al use case |
| `shouldNotDelegateWhenPayloadMappingFails` | Payload inválido → no delega |
| `shouldNotBubbleExceptionWhenUseCaseFails` | Excepción del use case → no propaga |

---

### 8. `LayeredArchitectureTest` — 2 tests | `@Tag("integration")`

**Ubicación:** `src/test/java/com/foodtech/ms_factura/architecture/LayeredArchitectureTest.java`

**Justificación:** Usa `ClassFileImporter` de ArchUnit para **escanear el bytecode** de las clases de producción e inspeccionar las **dependencias entre paquetes internos**. Es caja blanca por definición: analiza la **estructura interna** del código fuente.

**Indicadores de caja blanca:**
- `ClassFileImporter().importPackages(...)` — escanea bytecode de producción
- `layeredArchitecture().layer(...).definedBy(...)` — define capas internas
- `noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat()` — inspecciona dependencias internas

| Test | Qué verifica |
|------|-------------|
| `hexagonalLayerDirectionShouldBeRespected` | Dirección de dependencias entre capas |
| `domainShouldBeFrameworkAgnostic` | Dominio no depende de frameworks |

---

## 🔑 Guía de Indicadores

| Señal en el código | Clasificación |
|-------------------|---------------|
| `ReflectionTestUtils.setField(...)` | ⬜ Caja blanca — manipula estado privado |
| `verify(mock).method()` | ⬜ Caja blanca — verifica interacciones internas |
| `verifyNoInteractions(...)` | ⬜ Caja blanca — verifica que NO hubo interacciones |
| `@Mock` + `@InjectMocks` con verificación | ⬜ Caja blanca — reemplaza y espía dependencias |
| `ArgumentCaptor<T>` | ⬜ Caja blanca — captura argumentos de invocaciones internas |
| `ClassFileImporter` / ArchUnit | ⬜ Caja blanca — inspecciona estructura de bytecode |
| Solo `assertEquals` / `assertThat` sobre retorno | ⬛ Caja negra — verifica output de API pública |
| Leer archivo de salida y verificar contenido | ⬛ Caja negra — verifica artefacto observable |
| `@SpringBootTest` + "does it start?" | ⬛ Caja negra — trata el sistema como unidad opaca |

---

## 📈 Distribución por Categoría de Test

| Categoría | Caja Negra | Caja Blanca | Total |
|-----------|:----------:|:-----------:|------:|
| Unit (`@Tag("unit")`) | 34 tests (4 clases) | 25 tests (4 clases) | 59 |
| Component (`@Tag("component")`) | 10 tests (3 clases) | 9 tests (1 clase) | 19 |
| Integration (`@Tag("integration")`) | 1 test (1 clase) | 8 tests (3 clases) | 9 |
| **Total** | **45 tests** | **42 tests** | **87** |

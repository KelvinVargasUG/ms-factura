# Estrategia de Testing — Email y Creacion de Archivos

## Introduccion

Este documento define la estrategia de QA del microservicio ms-factura, limitada a los procesos de envio de email y creacion de archivos de factura. Distingue entre:

- Verificacion: comprobar que el codigo funciona tecnicamente.
- Validacion: confirmar que cumple las reglas de negocio esperadas.

## Cobertura actual

- Cobertura de lineas: 100%
- Cobertura de ramas: 94.8%

## Verificacion vs Validacion (definicion operativa)

| Aspecto | Verificacion | Validacion |
| --- | --- | --- |
| Pregunta | El codigo funciona tecnicamente | Cumple reglas de negocio |
| Enfoque | Integraciones y framework | Comportamiento funcional |
| Evidencia | mocks, asserts tecnicos | asserts de reglas y flujos |

## Proceso 1: Envio de email

### Verificacion (tecnica)

- Se construye y envia el correo con JavaMailSender.
- Se agregan adjuntos existentes al mensaje.
- Se envuelve una excepcion de correo en RuntimeException.

Tests que verifican estos puntos:

- [EmailNotificationChannelTest.java](src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/notification/EmailNotificationChannelTest.java)

### Validacion (negocio)

- El remitente (from) es obligatorio, si es null o en blanco se rechaza.
- Si el usuario intenta adjuntar un archivo que no existe, se rechaza.
- Si el destinatario o mensaje no es valido, no debe enviarse.

Tests que validan estos puntos:

- [EmailNotificationChannelTest.java](src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/notification/EmailNotificationChannelTest.java)
- [FacturaGeneradaEventListenerTest.java](src/test/java/com/foodtech/ms_factura/application/notification/FacturaGeneradaEventListenerTest.java)

## Proceso 2: Creacion de archivos de factura

### Verificacion (tecnica)

- PDF se genera sin errores y el archivo existe.
- TXT se genera con formato legible y el path es valido.
- XLSX se genera con celdas y hojas correctas.

Tests que verifican estos puntos:

- [FilePdfFacturaGeneratorTest.java](src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/file/FilePdfFacturaGeneratorTest.java)
- [FileTxtFacturaGeneratorTest.java](src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/file/FileTxtFacturaGeneratorTest.java)
- [FileXlsxFacturaGeneratorTest.java](src/test/java/com/foodtech/ms_factura/infrastructure/adapters/output/file/FileXlsxFacturaGeneratorTest.java)

### Validacion (negocio)

- Se usa el generador correcto segun el formato solicitado.
- El archivo generado se adjunta al mensaje de notificacion cuando aplica.

Tests que validan estos puntos:

- [GenerarFacturaServiceTest.java](src/test/java/com/foodtech/ms_factura/application/GenerarFacturaServiceTest.java)
- [FacturaGeneradaEventListenerTest.java](src/test/java/com/foodtech/ms_factura/application/notification/FacturaGeneradaEventListenerTest.java)

## Criterios de aceptacion de QA

- Todos los tests de email y archivos pasan en verde.
- Se mantiene cobertura minima requerida (>= 90%).
- Reglas de negocio criticas: from obligatorio y adjuntos inexistentes generan error.
        └─> Test ARQUITECTÓNICO (ArchUnit)
            Tipo: ✔️ VERIFICACIÓN

---

## 🛡️ Estrategia de Mocking

### Cuándo Usar Mocks

| Situación | Mock | Justificación |
|-----------|------|---------------|
| Dependencia externa (Kafka, RabbitMQ) | ✅ Sí | Tests unitarios deben ser rápidos y deterministas |
| Puerto de salida (FacturaOutputPort) | ✅ Sí | Aislamos lógica de aplicación |
| Servicio de aplicación | ✅ Sí (en tests de infra) | Verificamos solo el adaptador |
| Entidad de dominio (Factura) | ❌ No | Son datos, no comportamiento |
| JavaMailSender | ✅ Sí | Evitamos enviar emails reales |
| FileSystem | ⚠️ Depende | TempDir de JUnit5 para tests reales |

### Ejemplos de Mocking Correcto

```java
// ✅ CORRECTO: Mock de puerto en capa de aplicación
@Mock
private PdfFacturaGeneratorPort pdfFacturaGeneratorPort;

@Test
void testGenerarFacturaPdf() {
    // Mock retorna un Path simulado
    when(pdfFacturaGeneratorPort.generar(factura))
        .thenReturn(Path.of("/tmp/facturas/factura_test.pdf"));
    
    // Verificamos que se llamó al puerto correcto
    verify(pdfFacturaGeneratorPort).generar(factura);
}
```

```java
// ✅ CORRECTO: Uso de @TempDir para filesystem real (no mock)
@TempDir
Path tempDir;

@Test
void shouldCreatePdfFile() {
    FilePdfFacturaGenerator generator = new FilePdfFacturaGenerator(tempDir.toString());
    
    Path result = generator.generar(factura);
    
    assertTrue(Files.exists(result)); // Archivo realmente creado
}
```

---

## 🔧 Herramientas y Configuración

### Stack Tecnológico de Testing

```yaml
Testing Framework: JUnit 5 (Jupiter)
Mocking: Mockito 5.x
Assertions: AssertJ + JUnit Assertions
Coverage: JaCoCo 0.8.12
Architecture Tests: ArchUnit 1.2.1
Build Tool: Gradle 9.3.0
```

### Configuración JaCoCo (build.gradle)

```gradle
jacoco {
    toolVersion = '0.8.12'
}

jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = 0.90  // 90% mínimo obligatorio
            }
        }
    }
}

// Exclusiones inteligentes
jacocoTestReport {
    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.collect {
            fileTree(dir: it, exclude: [
                '**/*Application.class'  // Clase main de Spring Boot
            ])
        }))
    }
}
```

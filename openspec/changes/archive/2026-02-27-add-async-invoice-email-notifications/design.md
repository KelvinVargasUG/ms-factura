## Context

`ms-factura` genera archivos de factura (TXT/PDF/XLSX) pero no notifica automáticamente al cliente al finalizar el proceso. El cambio introduce notificación asíncrona por email como primer canal, manteniendo la arquitectura hexagonal y preparando el servicio para múltiples canales futuros.

Estado actual relevante:
- La generación de factura ya está orquestada por caso de uso en Application.
- Los formatos de salida se resuelven por puertos/adaptadores (TXT/PDF/XLSX).
- No existe hoy un puerto de notificación ni un flujo event-driven interno para “factura generada”.

Restricciones clave:
- No bloquear la respuesta/flujo principal de generación de factura.
- No hardcodear credenciales SMTP.
- Mantener extensibilidad para nuevos canales sin tocar reglas de negocio.
- Diseño guiado por TDD: la solución debe facilitar pruebas unitarias sin dependencias de red (SMTP) y minimizar acoplamientos a infraestructura.
- Se debe de evidenciar que se uso TDD

Stakeholders:
- Operación y soporte (trazabilidad de envíos y fallos)
- Producto/negocio (experiencia de cliente)
- Ingeniería (evolución a WhatsApp/SMS/Push)

## Goals / Non-Goals

**Goals:**
- Disparar notificación cuando la factura y sus archivos queden generados.
- Ejecutar envío en segundo plano (asíncrono) para no impactar latencia del flujo principal.
- Implementar canal Email con JavaMailSender y adjuntos.
- Introducir contratos de canal (`NotificationChannel`) para escalar a múltiples canales.
- Configurar SMTP y plantilla (asunto/cuerpo) vía `application.properties` + variables de entorno/secretos.
- Registrar resultado de envío (`OK` / `Fallo`) con causa.
- Implementar la funcionalidad siguiendo TDD, con cobertura de pruebas sobre el caso de uso/servicio y comportamiento de eventos, evitando SMTP real en tests.

**Non-Goals:**
- No añadir nuevos endpoints HTTP para esta iteración.
- No implementar canales distintos a Email en esta fase.
- No rediseñar el modelo funcional de factura ni su contrato de entrada.
- No agregar scheduler/reintentos avanzados distribuidos en esta versión inicial.

## Decisions

### Decision 1: Publicar evento de aplicación `FacturaGeneradaEvent`
Se emitirá un evento de aplicación después de completar la generación de archivos.

**Rationale:**
- Separa el flujo principal de facturación del flujo de notificación.
- Evita acoplar el caso de uso a una implementación concreta de email.

**Alternatives considered:**
- Invocar email directamente en el caso de uso principal: rechazado por acoplamiento y mayor latencia.
- Enviar mensaje externo (Kafka/Rabbit) en primera fase: rechazado por complejidad operativa inicial.

### Decision 2: Listener asíncrono para notificación
El consumo del evento de factura generada se hará en listener con `@Async` (o executor equivalente configurado). Para pruebas, el executor asíncrono debe poder configurarse para ejecutarse de forma determinista (ej. SyncTaskExecutor en perfil test o @SpringBootTest con configuración de executor).

**Rationale:**
- Garantiza comportamiento no bloqueante para la operación principal.
- Permite escalar workers de notificación sin tocar lógica de dominio.

**Alternatives considered:**
- Hilo manual por cada envío: rechazado por manejo pobre de recursos y observabilidad.
- Sincronía con timeout: rechazado por impacto en SLA del flujo principal.

### Decision 3: Strategy/Port para canales de notificación
Se definirá un puerto/estrategia `NotificationChannel` y un `NotificationService` que delega según configuración (`notification.channel` o `notification.channels`).

**Rationale:**
- Abierto a extensiones futuras (WhatsApp/SMS/Push) sin modificar el caso de uso.
- Mantiene principio de inversión de dependencias de la arquitectura hexagonal.

**Alternatives considered:**
- `if/else` de canales dentro del servicio principal: rechazado por romper extensibilidad.\

### Decision 4: Diseñar puertos y adaptadores para habilitar TDD
La lógica de construcción del `NotificationMessage` y el ruteo de canal deben residir en Application, y la infraestructura (SMTP/JavaMailSender) debe quedar encapsulada en adaptadores.

**Rationale:**
- Permite pruebas unitarias del flujo (evento → mensaje → canal) sin SMTP real.
- Reduce regresiones y facilita agregar canales nuevos con tests.

**Alternatives considered:**
- Construir el mensaje y adjuntos dentro del adaptador SMTP: rechazado por dificultar pruebas y acoplar la lógica al canal.

### Decision 5: Adaptador SMTP con JavaMailSender y adjuntos
El canal email se implementa en Infrastructure con `JavaMailSender` y construcción de `MimeMessage` con uno o varios adjuntos.

**Rationale:**
- Spring Mail provee integración madura para SMTP y adjuntos.
- Permite configuración declarativa en `application.properties`.

**Alternatives considered:**
- Cliente SMTP custom/manual: rechazado por mayor costo y menor mantenibilidad.

### Decision 6: Configuración y secretos externos
Credenciales (`username/password`) se leen desde variables de entorno o secret manager integrado al runtime.

**Rationale:**
- Evita secretos en código o repositorio.
- Facilita despliegues por ambiente (dev/stage/prod).

**Alternatives considered:**
- Credenciales fijas en properties: rechazado por riesgo de seguridad.

### Decision 7: Observabilidad por logs estructurados
Se registrará outcome de envío con correlación mínima (id factura/evento, canal, destinatario, resultado, error).

**Rationale:**
- Mejora soporte operativo y auditoría técnica.
- Permite construir métricas posteriores sin rediseñar flujo.

**Alternatives considered:**
- Logging mínimo sin contexto: rechazado por bajo valor diagnóstico.

## Testing Strategy (TDD)
**Unit tests (Application):**
- FacturaGeneradaEventListener transforma evento → NotificationMessage.
- NotificationService delega a NotificationChannel configurado.
- Cuando el canal falla, se registra error y no se propaga excepción al flujo principal.

**Integration tests (Infrastructure):**
- Carga de contexto Spring y wiring del EmailNotificationChannel.
- No se permite SMTP real en CI: JavaMailSender debe ser mock/fake en tests.

## Risks / Trade-offs

- [Risk] Fallos SMTP intermitentes → Mitigation: capturar excepción, loggear causa y no afectar el resultado de facturación.
- [Risk] Saturación de executor asíncrono bajo alta carga → Mitigation: configurar pool y cola con límites y rechazo controlado.
- [Risk] Adjuntos grandes incrementan uso de memoria → Mitigation: adjuntar por rutas/streams y validar tamaño por configuración.
- [Risk] Configuración incorrecta de canal(es) → Mitigation: validación de properties al arranque y fallback seguro.
- [Risk] Duplicidad de notificaciones por reprocesos → Mitigation: incluir identificador de factura/evento y preparar idempotencia en iteración futura.
- [Risk] Pruebas inestables por asincronía → Mitigation: inyectar executor configurable; en tests usar ejecución síncrona o esperar con latch/controlado.

## Migration Plan

1. Definir contratos y pruebas (`NotificationService`, Listener, construcción de mensaje) antes de implementar adaptadores.
2. Introducir modelo/puertos de notificación en Application (`NotificationMessage`, `NotificationChannel`, `NotificationService`).
3. Añadir evento de aplicación `FacturaGeneradaEvent` y publicación al finalizar generación de archivos.
4. Implementar listener asíncrono que consume el evento y delega al `NotificationService`.
5. Implementar `EmailNotificationChannel` en Infrastructure usando `JavaMailSender` y adjuntos.
6. Agregar configuración SMTP y plantilla en properties (con secretos externos).
7. Añadir/ajustar pruebas unitarias de Application y pruebas de integración de adaptador email.
8. Validar no regresión del flujo de facturación (sin bloqueo y con fallback/logging correcto).

Rollback strategy:
- Feature toggle de notificaciones (`notification.enabled=false`) para desactivar envío sin retirar código.
- Revertir solo capa de notificación si impacta operación, manteniendo generación de factura intacta.

## Open Questions

- ¿Se enviará siempre a un único destinatario o se requiere CC/BCC por configuración?
Se enviara a un unico usuario
- ¿El cuerpo/asunto requiere i18n o basta plantilla simple por ambiente en esta fase?
Basta con plantilla simple
- ¿Se necesita persistir historial de notificaciones en base de datos desde la primera versión?
Por ahora no
- ¿Debe soportarse selección multi-canal desde inicio (`notification.channels`) o solo canal único (`notification.channel`)?
unico canal
- ¿Los adjuntos se pasan como byte[] (en memoria) o como Path/stream (disco)?
Recomendación inicial: Path/stream para evitar memoria con archivos grandes.
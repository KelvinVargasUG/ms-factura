## Why

La generación de factura hoy finaliza sin notificar automáticamente al cliente, lo que deja un paso manual crítico y reduce trazabilidad operacional. Se necesita incorporar notificación asíncrona por email con adjuntos para mejorar experiencia, observabilidad y preparar la plataforma para nuevos canales sin acoplar la lógica de negocio.

## What Changes

- Publicar un evento de aplicación al completar la generación de factura y archivos.
- Agregar un flujo asíncrono de notificación que no bloquee la respuesta de generación de factura.
- Implementar canal inicial de notificación por email usando `Spring Mail`/`JavaMailSender` con soporte de adjuntos PDF/XLSX/TXT.
- Permitir configuración por propiedades para SMTP (`host`, `port`, `username`, `password`, `from`) y plantillas simples de asunto/cuerpo.
- Registrar en logs resultados de envío (`OK` o `Fallo` con causa).
- Definir contratos de notificación extensibles para habilitar canales futuros (WhatsApp/SMS/Push) sin cambios en reglas de dominio.

## Capabilities

### New Capabilities
- `async-invoice-email-notifications`: Envía notificaciones por email de forma asíncrona al generarse una factura, con adjuntos y logging de resultado.
- `notification-channel-strategy`: Define puertos/estrategias de canal para seleccionar uno o varios canales por configuración.
- `smtp-notification-configuration`: Configura SMTP y plantilla básica de asunto/cuerpo mediante properties y secretos externos.

### Modified Capabilities
- `ports-and-adapters-contracts`: Extiende contratos de puertos para disparo de evento de factura generada y ejecución de notificaciones en adaptadores.
- `layered-testability`: Amplía cobertura con pruebas unitarias del flujo asíncrono de aplicación y pruebas de integración del adaptador email.

## Impact

- Affected code:
  - `src/main/java/com/foodtech/ms_factura/application/**`
  - `src/main/java/com/foodtech/ms_factura/application/ports/**`
  - `src/main/java/com/foodtech/ms_factura/infrastructure/adapters/**`
  - `src/main/resources/application.properties`
  - `src/test/java/com/foodtech/ms_factura/**`
- APIs and behavior:
  - Sin cambios en contratos públicos de generación de factura.
  - El envío de notificaciones será eventual y no bloqueante.
- Dependencies/systems:
  - Nuevo uso de SMTP (Gmail u otro proveedor compatible) y Spring Mail.
  - Secretos de credenciales gestionados vía variables de entorno o secret manager.

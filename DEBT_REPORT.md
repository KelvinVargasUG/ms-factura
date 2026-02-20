# Reporte: Antes vs. Después 🚀

Este reporte muestra cómo aplicamos los principios **SOLID** para limpiar el código y hacerlo profesional.

### 📊 Métricas del Cambio
| Métrica | Antes (Monolítico) | Después (Hexagonal) | Impacto |
| :--- | :--- | :--- | :--- |
| **Arquitectura** | Acoplada | Hexagonal (Limpia) | 100% Flexible |
| **Clases Totales** | ~7 clases | 14 clases | Mayor Orden |
| **Líneas de Código** | ~312 LOC | 504 LOC | Más Robusto |
| **Acoplamiento** | Alto (Rígido) | Bajo (Plug & Play) | Cero Dolor |

---

### 1. Inversión de Dependencias (DIP)
**Principio:** El servicio debe depender de abstracciones (interfaces), no de clases fijas. 
**¿Por qué importa?:** Antes, si querías cambiar la librería de PDFs por una más moderna, tenías que entrar al "corazón" del sistema y arriesgarte a romper la lógica de negocio. Ahora, el servicio solo sabe que tiene un "enchufe" (`FacturaOutputPort`) donde puede conectar cualquier generador.
**Evidencia:**
```java
// ANTES (Mal - Acoplado)
// El servicio está "atado" a clases específicas de archivos.
public class GenerarFacturaService {
    private final FilePdfFacturaGenerator pdfGen; 
    private final FileTxtFacturaGenerator txtGen;
}

// DESPUÉS (Bien - DIP)
// El servicio ahora es libre. Solo conoce el "contrato" (Interfaz).
public class GenerarFacturaService {
    private final Map<String, FacturaOutputPort> generators; 
}
```

### 2. Abierto/Cerrado (OCP)
**Principio:** El código debe estar abierto a extensiones (nuevos formatos) pero cerrado a modificaciones del código central.
**¿Por qué importa?:** Evitamos el "Efecto Dominó". En el código antiguo, para añadir un formato "Excel", tenías que modificar el `GenerarFacturaService` añadiendo más `if-else`. En el nuevo diseño, simplemente creas una clase nueva y Spring la "enchufa" automáticamente. El código viejo no se toca.
**Evidencia:**
```java
// ANTES (Mal - Rígido)
// Cada vez que hay un formato nuevo, hay que escribir más código aquí.
if ("PDF".equals(formato)) { ... } 
else if ("TXT".equals(formato)) { ... }

// DESPUÉS (Bien - OCP)
// El sistema es inteligente. Busca el formato en el Mapa y lo ejecuta.
// No importa si mañana hay 50 formatos nuevos, esta línea no cambia.
generators.get(formato).generar(factura);
```

### 3. Responsabilidad Única (SRP)
**Principio:** Cada clase debe tener una única misión. 
**¿Por qué importa?:** El "Corazón" (Dominio) del sistema debe ser sagrado. Antes, si el equipo de RabbitMQ decidía cambiar el nombre de una variable en el mensaje, tu lógica de negocio fallaba. Ahora, tenemos "traductores" (DTOs) en la frontera. Si el mundo exterior cambia, solo ajustas el traductor, y el corazón sigue latiendo tranquilo.
**Evidencia:**
```java
// ANTES (Mal - Mezclado)
// El mensaje externo (infraestructura) se usaba como lógica interna.
public void consume(FoodEvent event) { 
    // ...
}

// DESPUÉS (Bien - SRP)
// Separamos el "Mensajero" (DTO) de la "Lógica" (Dominio).
public void consume(FoodEventDto eventDto) { 
    // Primero traducimos el mensaje...
    FoodEvent domain = mapper.toDomain(eventDto); 
    // ...y luego procesamos la lógica puramente.
}
```

---

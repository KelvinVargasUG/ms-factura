# Reporte: Antes vs. Después 

Este reporte muestra cómo aplicamos los principios **SOLID** para limpiar el código.

### 1. Inversión de Dependencias (DIP)
**Problema:** El servicio dependía de clases fijas. Si cambiabas el PDF, rompías el servicio.
**Solución:** Ahora depende de una Interfaz (un "contrato").

```java
// ANTES (Mal - Acoplado)
public class GenerarFacturaService {
    private final FilePdfFacturaGenerator pdfGen; // Clase fija
}

// DESPUÉS (Bien - DIP)
public class GenerarFacturaService {
    private final Map<String, FacturaOutputPort> generators; // Interfaz
}
```

### 2. Abierto/Cerrado (OCP)
**Problema:** Para añadir un formato nuevo, había que modificar el código principal.
**Solución:** Usamos un Mapa. Añadir formatos es como enchufar un USB.

```java
// ANTES (Mal - Rígido)
if ("PDF".equals(formato)) { ... } 
else if ("TXT".equals(formato)) { ... }

// DESPUÉS (Bien - OCP)
generators.get(formato).generar(factura); // ¡Sin ifs!
```

### 3. Responsabilidad Única (SRP)
**Problema:** Una misma clase hacía de "Mensajero" y de "Lógica de Negocio".
**Solución:** Separamos los mensajes (DTO) de la lógica real (Dominio).

```java
// ANTES (Mal - Mezclado)
public void consume(FoodEvent event) { // Evento es Dominio
    // ...
}

// DESPUÉS (Bien - SRP)
public void consume(FoodEventDto eventDto) { // DTO para el mensaje
    FoodEvent domain = mapper.toDomain(eventDto); // Traducción
}
```


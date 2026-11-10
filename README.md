# _TALLER MICROSERVICIOS_
--------------------------------------------------- 

| Paquete / clase actual | Destino |
|---|---|
| `investigadores.*` | `investigadores_service` |
| `publicaciones.*` | `publicaciones_service` |
| `compartido.GlobalExceptionHandler`, `RecursoNoEncontradoException` | Se duplica en ambos |
| `plataforma.StatusController`, CorsConfig | Se duplica en ambos, adaptado |
| `plataforma.ArranqueInformativo`, `SaludoInstitucionalService` | Se descarta (demo del Tutorial 2) |

* Decisión de arquitectura: duplicar `compartido` evita acoplar los servicios,
pero obliga a mantener dos copias. 


# _TALLER_COMUNICACIÓN_MICROSERVICIOS
--------------------------------------------------- 
| Necesidad | Patrón | Dónde en este taller |
| :--- | :--- | :--- |
| Confirmar que un investigador existe antes de registrar una publicación | RPI síncrono — gRPC | Paso 2 |
| No caer en cascada si `investigadores-service` está lento o caído | Circuit Breaker (Resilience4j) | Paso 3 |
| Que ningún servicio dependa de una dirección de red fija | Service Registry (Eureka) | Paso 4 |
| Un único punto de entrada para el frontend | API Gateway | Paso 5 |
| Notificar que se registró un investigador, sin acoplar al publicador con quien escucha | Domain Event + Transactional Outbox (Kafka) | Paso 6 |
| Eliminar un investigador y, en consecuencia, marcar sus publicaciones — sin una transacción distribuida | Saga (coreografía) | Paso 7 |

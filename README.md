
--------------------------------------------------- 
# _TALLER MICROSERVICIOS_

| Paquete / clase actual | Destino |
|---|---|
| `investigadores.*` | `investigadores_service` |
| `publicaciones.*` | `publicaciones_service` |
| `compartido.GlobalExceptionHandler`, `RecursoNoEncontradoException` | Se duplica en ambos |
| `plataforma.StatusController`, CorsConfig | Se duplica en ambos, adaptado |
| `plataforma.ArranqueInformativo`, `SaludoInstitucionalService` | Se descarta (demo del Tutorial 2) |

* Decisión de arquitectura: duplicar `compartido` evita acoplar los servicios,
pero obliga a mantener dos copias. 


--------------------------------------------------- 
# _TALLER COMUNICACIÓN MICROSERVICIOS_

| Necesidad | Patrón | Dónde en este taller |
| :--- | :--- | :--- |
| Confirmar que un investigador existe antes de registrar una publicación | RPI síncrono — gRPC | Paso 2 |
| No caer en cascada si `investigadores-service` está lento o caído | Circuit Breaker (Resilience4j) | Paso 3 |
| Que ningún servicio dependa de una dirección de red fija | Service Registry (Eureka) | Paso 4 |
| Un único punto de entrada para el frontend | API Gateway | Paso 5 |
| Notificar que se registró un investigador, sin acoplar al publicador con quien escucha | Domain Event + Transactional Outbox (Kafka) | Paso 6 |
| Eliminar un investigador y, en consecuencia, marcar sus publicaciones — sin una transacción distribuida | Saga (coreografía) | Paso 7 |


--------------------------------------------------- 
# _TALLER ARQUITECTURA ORIENTADA A EVENTOS EDA_

### Discusión: ¿Qué hacemos con la DLT?

1. ¿Quién debería enterarse cuando llega un mensaje a la DLT y cómo (alerta, tablero, correo)?
    - Quien debe enterarse es el dueño de `consultas-service`, o del encargado de monitorearlo, porque la DLT es de un condumudor y nadie mas puede arreglar
    la causa. Debe enterarse mediante una alerta automatica que se active cuando `investigadores.registrados.DLT` reciba un mensaje.

2. ¿Cómo se reprocesaría un mensaje después de corregir el error: reenviándolo al tópico original, o con una herramienta aparte?
    - Utilizando una herramienta externa como algun comando o endpoint administrativo que pueda leer la DLT y pueda republicar en el topico original solo los
    mensajes que ya se pueden procesar. Así si alguien revisa y filtra qué se reenvía y queda constancia de quién lo hizo, y si el mensaje vuelve a fallar regresa a la DLT sin bloquear nada.

3. ¿Cuándo es válido descartar un mensaje de la DLT, y quién lo decide? 
    - Cuando este es irrecuperable y no tiene valor alguno, como el ejemplo de "esto no es json" de la prueba, o un mensaje de prueba, o cuando su efecto ya quedó 
    aplicado por otra via. La decisión la toma el dueño servicio, junto con el responsable del negocio si el mensaje puede afectar datos reales, y debe quedar un registro de quién lo descartó y por qué.

4. ¿Qué pasa si el mensaje de la DLT es válido pero llegó a destiempo?
    - Reprocesarlo a ciegas puede pisar un estado más nuevo. Por ejemplo, un `investigadores.registrados` atascado en la DLT, reprocesado después de que el investigador fue eliminado, resucitaría una ficha que ya no debería existir. Antes de reenviarlo hay que comparar con el estado actual y descartarlo si ya quedó obsoleto.

* Reintentar un mensaje venenoso sin fin es peor que perderlo todo:
    - Reintentar sin fin un mensaje venenoso es peor que perderlo porque, mientras se repite el fallo, la partición queda detenida y los mensajes buenos que vienen detrás no se procesan, y al final el mensaje se pierde igual, sin dejar rastro, con el costo adicional de haber bloqueado el servicio. Y es peor que enviarlo a una DLT porque un error como un JSON roto nunca se arregla reintentando, mientras que la DLT lo aparta de inmediato, deja el consumidor libre y conserva el mensaje con su diagnóstico para revisarlo o reprocesarlo después.
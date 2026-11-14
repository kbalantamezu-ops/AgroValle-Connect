# Sprint Planning: Sprint 7

**Proyecto:** AgroValle Connect  
**Sprint:** 7: Carrito de compras y notificaciones  
**Capacidad:** 10 Story Points (rango permitido: 8 a 12)  
**Estado:** Proyectado. Esta planificación se confirma y refina en el Sprint Planning al inicio del sprint.

## Sprint Goal

> Facilitar el proceso de compra mediante un carrito donde los usuarios organicen los productos que desean adquirir, e implementar notificaciones sobre los cambios de estado de sus pedidos.

## Historias Seleccionadas

| ID | Historia de Usuario | Prioridad MoSCoW | Story Points |
|---|---|---|---|
| HU-11 | Agregar Productos al Carrito | Must Have | 5 |
| HU-14 | Notificaciones de Cambio de Estado del Pedido | Should Have | 5 |
| | **Total** | | **10** |

## Justificación

- **HU-11:** Permite seleccionar varios productos antes de confirmar la compra.
- **HU-14:** Mantiene informado al comprador sin que tenga que consultar constantemente. Aplica el patrón Observer.

## Dependencias y Supuestos

- HU-11 alimenta la confirmación de pedidos construida en HU-09 (Sprint 6).
- HU-14 aplica el patrón Observer: PedidoService publica un evento cuando cambia el estado y un listener crea la notificación, sin acoplar ambos componentes.
- El envío externo (correo o mensajería) queda fuera del alcance: las notificaciones se almacenan y se consultan por la API.
- Mientras no exista autenticación con JWT, el identificador del usuario o del agricultor viaja como parámetro o en el cuerpo de la petición. La protección por token se planifica como trabajo técnico aparte.

## Desglose Técnico

Cada tarea tiene un máximo de 8 horas. Las horas son una estimación inicial que el equipo ajusta en el Sprint Planning.

### HU-11: Agregar Productos al Carrito (5 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| POST | `/api/v1/carrito/productos` | 201 Created | 400 cantidad inválida, 404 producto inexistente, 409 cantidad mayor a la disponible |
| GET | `/api/v1/carrito` | 200 OK | - |
| PUT | `/api/v1/carrito/productos/{id}` | 200 OK | 404 ítem inexistente, 409 cantidad mayor a la disponible |
| DELETE | `/api/v1/carrito/productos/{id}` | 204 No Content | 404 ítem inexistente |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-11.01 | Mapear la entidad ItemCarrito a la tabla items_carrito (única por usuario y producto) | PostgreSQL, Spring Data JPA (@Entity) | Mantenibilidad | 4 |
| T-11.02 | Crear ItemCarritoRepository (findByUsuarioId) | Spring Data JPA | Adecuación funcional | 2 |
| T-11.03 | Implementar CarritoService.agregar: validar producto activo, cantidad disponible y acumular si ya existe | Spring (@Service, @Transactional) | Fiabilidad | 7 |
| T-11.04 | Implementar la modificación y eliminación de ítems | Spring (@Service) | Adecuación funcional | 5 |
| T-11.05 | Calcular subtotales y total del carrito en pesos colombianos con BigDecimal | Java 17 (BigDecimal) | Adecuación funcional | 4 |
| T-11.06 | Implementar los endpoints del carrito con sus DTO | Spring Boot (@RestController), DTO | Compatibilidad | 6 |
| T-11.07 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 7 |
| | **Total estimado** | | | **35** |

### HU-14: Notificaciones de Cambio de Estado del Pedido (5 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| GET | `/api/v1/notificaciones` | 200 OK | 404 usuario inexistente |
| PATCH | `/api/v1/notificaciones/{id}/leida` | 200 OK | 404 notificación inexistente o ajena |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-14.01 | Mapear la entidad Notificacion a la tabla notificaciones | PostgreSQL, Spring Data JPA (@Entity) | Mantenibilidad | 3 |
| T-14.02 | Crear NotificacionRepository con consulta por usuario ordenada por fecha | Spring Data JPA | Adecuación funcional | 3 |
| T-14.03 | Definir el evento PedidoEstadoCambiadoEvent y publicarlo desde PedidoService (patrón Observer) | Spring (ApplicationEventPublisher) | Mantenibilidad | 5 |
| T-14.04 | Implementar el listener que genera la notificación al recibir el evento | Spring (@EventListener / @TransactionalEventListener) | Fiabilidad | 6 |
| T-14.05 | Implementar los endpoints de consulta y de marcado como leída | Spring Boot (@RestController), DTO | Compatibilidad | 5 |
| T-14.06 | Garantizar que cada usuario consulte solo sus notificaciones | Spring (@Service) | Seguridad | 4 |
| T-14.07 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 7 |
| | **Total estimado** | | | **33** |

## Tareas Transversales del Sprint

| ID | Tarea | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|
| T-S7.01 | Ejecutar `./mvnw clean verify` y auditar Checkstyle (0 advertencias) y JaCoCo (instrucciones y ramas de al menos 60%) | Mantenibilidad | 3 |
| T-S7.02 | Actualizar el DER, el diagrama de secuencia y el README si el sprint modifica el diseño | Mantenibilidad | 3 |
| T-S7.03 | Registrar la bitácora de Daily Scrum, el Sprint Review y la Retrospectiva en `docs/` | Adecuación funcional | 3 |

## Definition of Done

Una tarea se considera terminada cuando cumple el contrato de `docs/dod.md`:

- Compilación limpia con `./mvnw clean verify`.
- 0 advertencias de Checkstyle.
- 100% de las pruebas pasando y cobertura JaCoCo de al menos 60% en instrucciones y ramas.
- Pull Request revisado y aprobado por un compañero distinto al autor.
- Pipeline de GitHub Actions en verde antes del merge.

## Riesgos

- Las dependencias con pedidos pueden obligar a ajustar pruebas cuando se implemente HU-09.
- Si una historia no cabe en el sprint, se divide y la parte restante vuelve al Product Backlog.

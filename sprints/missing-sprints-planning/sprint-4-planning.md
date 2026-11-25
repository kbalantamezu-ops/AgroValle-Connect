# Sprint Planning: Sprint 4

**Proyecto:** AgroValle Connect  
**Sprint:** 4: Transporte y estado de los pedidos  
**Capacidad:** 11 Story Points (rango permitido: 8 a 12)  
**Estado:** Proyectado. Esta planificación se confirma y refina en el Sprint Planning al inicio del sprint.

## Sprint Goal

> Facilitar la gestión del transporte de los productos agrícolas y permitir que los compradores consulten el estado de sus pedidos, desde su confirmación hasta la entrega.

## Historias Seleccionadas

| ID | Historia de Usuario | Prioridad MoSCoW | Story Points |
|---|---|---|---|
| HU-07 | Transporte | Should Have | 8 |
| HU-10 | Consultar el Estado de un Pedido | Must Have | 3 |
| | **Total** | | **11** |

## Justificación

- **HU-07:** Organiza el transporte de los productos desde el agricultor hasta el comprador y reutiliza el patrón Strategy de fletes.
- **HU-10:** Permite que el comprador conozca el avance de sus pedidos hasta la entrega.

## Dependencias y Supuestos

- HU-07 reutiliza el patrón Strategy de fletes (FleteService) construido en el Sprint de Diseño.
- El seguimiento del despacho se maneja por estados (PROGRAMADO, EN_RUTA, ENTREGADO). El seguimiento en tiempo real con GPS queda fuera del alcance.
- HU-10 consulta pedidos que, hasta el Sprint 6, se crean como datos de prueba.
- Mientras no exista autenticación con JWT, el identificador del usuario o del agricultor viaja como parámetro o en el cuerpo de la petición. La protección por token se planifica como trabajo técnico aparte.

## Desglose Técnico

Cada tarea tiene un máximo de 8 horas. Las horas son una estimación inicial que el equipo ajusta en el Sprint Planning.

### HU-07: Transporte (8 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| POST | `/api/v1/despachos` | 201 Created | 400 datos inválidos, 404 pedido o vehículo inexistente, 409 vehículo no disponible |
| GET | `/api/v1/despachos/pedido/{pedidoId}` | 200 OK | 404 despacho inexistente |
| PATCH | `/api/v1/despachos/{id}/estado` | 200 OK | 409 transición de estado inválida |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-07.01 | Mapear las entidades Vehiculo y Despacho a las tablas vehiculos y despachos | PostgreSQL, Spring Data JPA (@Entity) | Mantenibilidad | 5 |
| T-07.02 | Crear VehiculoRepository (findByDisponibleTrue) y DespachoRepository (findByPedidoId) | Java 17, Spring Data JPA | Adecuación funcional | 3 |
| T-07.03 | Implementar DespachoService: programar el despacho con origen, destino y vehículo disponible | Spring (@Service, @Transactional) | Adecuación funcional | 8 |
| T-07.04 | Integrar el cálculo de flete con FleteService (patrón Strategy) y estimar la fecha de entrega | Spring (@Service), patrón Strategy | Mantenibilidad | 4 |
| T-07.05 | Validar el estado del pedido, la disponibilidad y la capacidad del vehículo | Spring (@Service) | Fiabilidad | 6 |
| T-07.06 | Implementar la actualización del estado (PROGRAMADO, EN_RUTA, ENTREGADO) y liberar el vehículo | Spring (@Service, @Transactional) | Fiabilidad | 5 |
| T-07.07 | Implementar los endpoints de despachos con sus DTO | Spring Boot (@RestController), DTO | Compatibilidad | 6 |
| T-07.08 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 8 |
| | **Total estimado** | | | **45** |

### HU-10: Consultar el Estado de un Pedido (3 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| GET | `/api/v1/pedidos/{id}/estado` | 200 OK | 404 pedido inexistente o ajeno |
| GET | `/api/v1/pedidos?usuario_id={id}` | 200 OK | 404 usuario inexistente |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-10.01 | Definir los estados del pedido (PENDIENTE, EN_PREPARACION, ENVIADO, ENTREGADO, CANCELADO) en la entidad Pedido | Java 17, Spring Data JPA | Mantenibilidad | 3 |
| T-10.02 | Crear consultas en PedidoRepository: findByUsuarioId y findByIdAndUsuarioId | Spring Data JPA | Adecuación funcional | 2 |
| T-10.03 | Implementar PedidoService.consultarEstado y listarPorUsuario | Spring (@Service) | Adecuación funcional | 5 |
| T-10.04 | Implementar los endpoints de consulta con su DTO de estado | Spring Boot (@RestController), DTO | Compatibilidad | 5 |
| T-10.05 | Garantizar que el pedido pertenezca al usuario que consulta | Spring (@Service) | Seguridad | 4 |
| T-10.06 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 6 |
| | **Total estimado** | | | **25** |

## Tareas Transversales del Sprint

| ID | Tarea | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|
| T-S4.01 | Ejecutar `./mvnw clean verify` y auditar Checkstyle (0 advertencias) y JaCoCo (instrucciones y ramas de al menos 60%) | Mantenibilidad | 3 |
| T-S4.02 | Actualizar el DER, el diagrama de secuencia y el README si el sprint modifica el diseño | Mantenibilidad | 3 |
| T-S4.03 | Registrar la bitácora de Daily Scrum, el Sprint Review y la Retrospectiva en `docs/` | Adecuación funcional | 3 |

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

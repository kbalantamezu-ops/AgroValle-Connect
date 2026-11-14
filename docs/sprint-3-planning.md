# Sprint Planning: Sprint 3

**Proyecto:** AgroValle Connect  
**Sprint:** 3: Contacto directo y medios de pago  
**Capacidad:** 11 Story Points (rango permitido: 8 a 12)  
**Estado:** Proyectado. Esta planificación se confirma y refina en el Sprint Planning al inicio del sprint.

## Sprint Goal

> Permitir que los compradores se comuniquen directamente con los agricultores y realicen pagos por los productos adquiridos, garantizando la seguridad de la información y el correcto registro de las transacciones.

## Historias Seleccionadas

| ID | Historia de Usuario | Prioridad MoSCoW | Story Points |
|---|---|---|---|
| HU-05 | Contacto Directo | Must Have | 3 |
| HU-06 | Medios de Pago | Should Have | 8 |
| | **Total** | | **11** |

## Justificación

- **HU-05:** Permite que el comprador se comunique con el agricultor para consultar productos, precios y disponibilidad.
- **HU-06:** Completa el proceso de compra al permitir que el comprador elija cómo pagar.

## Dependencias y Supuestos

- HU-05 es independiente: usa los agricultores, usuarios y productos del Sprint 1.
- HU-06 requiere pedidos. En este sprint se crea la entidad Pedido básica; la creación completa de pedidos llega con HU-09 (Sprint 6), por lo que las pruebas de pago usan pedidos creados como datos de prueba.
- No se integra una pasarela de pagos real: se define la interfaz PasarelaPago y una implementación simulada. Un proveedor real se podrá conectar después sin modificar el servicio.
- La notificación de pago confirmado al comprador y al agricultor se entrega con HU-14 (Sprint 7).
- Mientras no exista autenticación con JWT, el identificador del usuario o del agricultor viaja como parámetro o en el cuerpo de la petición. La protección por token se planifica como trabajo técnico aparte.

## Desglose Técnico

Cada tarea tiene un máximo de 8 horas. Las horas son una estimación inicial que el equipo ajusta en el Sprint Planning.

### HU-05: Contacto Directo (3 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| GET | `/api/v1/agricultores/{id}/contactos` | 200 OK | 404 agricultor inexistente |
| POST | `/api/v1/mensajes` | 201 Created | 400 datos inválidos, 404 agricultor inexistente |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-05.01 | Mapear las entidades MedioContacto y MensajeContacto a las tablas medios_contacto y mensajes_contacto | PostgreSQL, Spring Data JPA (@Entity) | Mantenibilidad | 4 |
| T-05.02 | Crear MedioContactoRepository (findByAgricultorId) y MensajeContactoRepository | Java 17, Spring Data JPA | Adecuación funcional | 3 |
| T-05.03 | Implementar ContactoService: listar medios de contacto y registrar mensajes validando que el agricultor exista y tenga medios definidos | Spring (@Service, @Transactional) | Adecuación funcional | 6 |
| T-05.04 | Implementar los endpoints de consulta de contactos y de envío de mensaje con sus DTO y mapper | Spring Boot (@RestController), DTO | Compatibilidad | 6 |
| T-05.05 | Validar el contenido del mensaje (no vacío, longitud máxima) con @Valid | Jakarta Validation | Seguridad | 3 |
| T-05.06 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 6 |
| | **Total estimado** | | | **28** |

### HU-06: Medios de Pago (8 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| GET | `/api/v1/medios-pago` | 200 OK | - |
| POST | `/api/v1/pagos` | 201 Created | 400 datos inválidos, 404 pedido inexistente, 409 pedido ya pagado |
| GET | `/api/v1/pagos/{pedidoId}` | 200 OK | 404 pago inexistente |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-06.01 | Crear la entidad Pedido básica y mapear MedioPago y Pago a las tablas pedidos, medios_pago y pagos | PostgreSQL, Spring Data JPA (@Entity) | Mantenibilidad | 6 |
| T-06.02 | Crear MedioPagoRepository y PagoRepository (findByPedidoId) | Java 17, Spring Data JPA | Adecuación funcional | 3 |
| T-06.03 | Implementar el catálogo de medios de pago (efectivo, transferencia, tarjeta) | Spring (@Service), datos iniciales SQL | Adecuación funcional | 3 |
| T-06.04 | Implementar PagoService: validar pedido, medio de pago, monto mayor que cero y un solo pago por pedido | Spring (@Service, @Transactional) | Fiabilidad | 8 |
| T-06.05 | Definir la interfaz PasarelaPago y una implementación simulada que apruebe o rechace el pago | Java 17, inversión de dependencias | Mantenibilidad | 6 |
| T-06.06 | Actualizar el estado del pago (PENDIENTE, CONFIRMADO, RECHAZADO) según la respuesta de la pasarela | Spring (@Service) | Fiabilidad | 4 |
| T-06.07 | Implementar los endpoints de pagos y de medios de pago con sus DTO | Spring Boot (@RestController), DTO | Compatibilidad | 6 |
| T-06.08 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 8 |
| | **Total estimado** | | | **44** |


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

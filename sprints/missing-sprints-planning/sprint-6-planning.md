# Sprint Planning: Sprint 6

**Proyecto:** AgroValle Connect  
**Sprint:** 6: Precios regionales y actualización de inventario  
**Capacidad:** 10 Story Points (rango permitido: 8 a 12)  
**Estado:** Proyectado. Esta planificación se confirma y refina en el Sprint Planning al inicio del sprint.

## Sprint Goal

> Permitir que los compradores consulten los precios de los productos agrícolas en diferentes regiones y garantizar que el inventario se actualice correctamente tras confirmar una compra.

## Historias Seleccionadas

| ID | Historia de Usuario | Prioridad MoSCoW | Story Points |
|---|---|---|---|
| HU-03 | Visualización de Precios Regionales | Should Have | 5 |
| HU-09 | Actualización de Stock tras Confirmación de Pedido | Must Have | 5 |
| | **Total** | | **10** |

## Justificación

- **HU-03:** Ayuda al comprador a comparar los precios de un mismo producto entre municipios del Valle.
- **HU-09:** Evita la sobreventa al descontar automáticamente el stock cuando se confirma un pedido.

## Dependencias y Supuestos

- HU-03 no crea tablas: calcula el promedio sobre detalle_pedidos, manteniendo la tercera forma normal.
- HU-09 introduce la creación real de pedidos (POST /api/v1/pedidos). A partir de este sprint, las pruebas de HU-06, HU-07, HU-08, HU-10 y HU-15 pueden apoyarse en el flujo real y no en datos de prueba.
- Se recomienda revisar las pruebas de los sprints anteriores al terminar HU-09.
- Mientras no exista autenticación con JWT, el identificador del usuario o del agricultor viaja como parámetro o en el cuerpo de la petición. La protección por token se planifica como trabajo técnico aparte.

## Desglose Técnico

Cada tarea tiene un máximo de 8 horas. Las horas son una estimación inicial que el equipo ajusta en el Sprint Planning.

### HU-03: Visualización de Precios Regionales (5 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| GET | `/api/v1/precios/regionales?producto=Café&municipio=Dagua` | 200 OK | 400 parámetros inválidos, 404 producto sin transacciones |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-03.01 | Diseñar el cálculo del promedio sin crear tablas nuevas, usando detalle_pedidos para respetar la 3FN | PostgreSQL, JPQL (AVG) | Mantenibilidad | 3 |
| T-03.02 | Crear la consulta del precio promedio por producto, municipio y ventana de 24 horas | Spring Data JPA (@Query) | Adecuación funcional | 5 |
| T-03.03 | Implementar PrecioRegionalService: media aritmética en pesos colombianos y comparación entre municipios | Spring (@Service) | Adecuación funcional | 6 |
| T-03.04 | Implementar el endpoint de precios regionales con su DTO | Spring Boot (@RestController), DTO | Compatibilidad | 4 |
| T-03.05 | Validar los parámetros y definir la respuesta cuando no hay transacciones | Jakarta Validation, Spring (@Service) | Fiabilidad | 4 |
| T-03.06 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 7 |
| | **Total estimado** | | | **29** |

### HU-09: Actualización de Stock tras Confirmación de Pedido (5 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| POST | `/api/v1/pedidos` | 201 Created | 400 datos inválidos, 404 producto inexistente, 409 stock insuficiente |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-09.01 | Verificar la columna cantidad_disponible y su restricción CHECK mayor o igual a cero | PostgreSQL, Spring Data JPA | Fiabilidad | 2 |
| T-09.02 | Consultar el producto con bloqueo pesimista (@Lock) para evitar compras simultáneas del mismo lote | Spring Data JPA (@Lock) | Fiabilidad | 4 |
| T-09.03 | Implementar PedidoService.confirmar: crear Pedido y DetallePedido, validar el stock y descontarlo | Spring (@Service, @Transactional) | Fiabilidad | 8 |
| T-09.04 | Crear StockInsuficienteException y su traducción a 409 Conflict | Spring (@RestControllerAdvice) | Adecuación funcional | 3 |
| T-09.05 | Implementar el endpoint de creación de pedidos con sus DTO | Spring Boot (@RestController), DTO | Compatibilidad | 5 |
| T-09.06 | Probar que la transacción se revierte completa si falla un producto del pedido | JUnit 5, Spring Boot Test | Fiabilidad | 5 |
| T-09.07 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 7 |
| | **Total estimado** | | | **34** |

## Tareas Transversales del Sprint

| ID | Tarea | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|
| T-S6.01 | Ejecutar `./mvnw clean verify` y auditar Checkstyle (0 advertencias) y JaCoCo (instrucciones y ramas de al menos 60%) | Mantenibilidad | 3 |
| T-S6.02 | Actualizar el DER, el diagrama de secuencia y el README si el sprint modifica el diseño | Mantenibilidad | 3 |
| T-S6.03 | Registrar la bitácora de Daily Scrum, el Sprint Review y la Retrospectiva en `docs/` | Adecuación funcional | 3 |

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

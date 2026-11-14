# Sprint Planning: Sprint 5

**Proyecto:** AgroValle Connect  
**Sprint:** 5: Reseñas y panel de gestión del agricultor  
**Capacidad:** 11 Story Points (rango permitido: 8 a 12)  
**Estado:** Proyectado. Esta planificación se confirma y refina en el Sprint Planning al inicio del sprint.

## Sprint Goal

> Mejorar la experiencia de los usuarios permitiendo calificar una compra y dejar reseñas, y ofrecer a los agricultores un panel para consultar su actividad comercial.

## Historias Seleccionadas

| ID | Historia de Usuario | Prioridad MoSCoW | Story Points |
|---|---|---|---|
| HU-08 | Satisfacción y Reseñas | Could Have | 3 |
| HU-15 | Panel de Gestión para el Agricultor | Could Have | 8 |
| | **Total** | | **11** |

## Justificación

- **HU-08:** Permite conocer la opinión de los compradores y genera confianza entre los usuarios.
- **HU-15:** Da al agricultor un solo lugar para administrar sus publicaciones, pedidos y ventas.

## Dependencias y Supuestos

- HU-08 requiere pedidos en estado ENTREGADO; en las pruebas se usan pedidos de ese estado como datos de prueba.
- La reseña se asocia al pedido (tabla resenas), no al producto: un pedido agrupa productos de un solo agricultor.
- HU-15 depende de pedidos y ventas registrados; las consultas se validan con datos de prueba hasta que exista el flujo completo de pedidos.
- Mientras no exista autenticación con JWT, el identificador del usuario o del agricultor viaja como parámetro o en el cuerpo de la petición. La protección por token se planifica como trabajo técnico aparte.

## Desglose Técnico

Cada tarea tiene un máximo de 8 horas. Las horas son una estimación inicial que el equipo ajusta en el Sprint Planning.

### HU-08: Satisfacción y Reseñas (3 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| POST | `/api/v1/resenas` | 201 Created | 400 calificación fuera de rango, 404 pedido inexistente, 409 pedido no entregado o ya reseñado |
| GET | `/api/v1/agricultores/{id}/resenas` | 200 OK | 404 agricultor inexistente |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-08.01 | Mapear la entidad Resena a la tabla resenas (calificación de calidad, de puntualidad y comentario) | PostgreSQL, Spring Data JPA (@Entity) | Mantenibilidad | 3 |
| T-08.02 | Crear ResenaRepository (existsByPedidoId, consulta por agricultor) | Spring Data JPA | Adecuación funcional | 3 |
| T-08.03 | Implementar ResenaService: validar pedido entregado, calificaciones de 1 a 5 y una reseña por pedido | Spring (@Service, @Transactional) | Fiabilidad | 6 |
| T-08.04 | Implementar los endpoints de reseñas con sus DTO | Spring Boot (@RestController), DTO | Compatibilidad | 5 |
| T-08.05 | Validar los rangos de calificación con @Min y @Max | Jakarta Validation | Fiabilidad | 2 |
| T-08.06 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 6 |
| | **Total estimado** | | | **25** |

### HU-15: Panel de Gestión para el Agricultor (8 Story Points)

**Contrato REST derivado de los escenarios BDD:**

| Método | Ruta | Respuesta exitosa | Respuestas de error |
|---|---|---|---|
| GET | `/api/v1/agricultores/{id}/panel` | 200 OK | 404 agricultor inexistente |

**Tareas:**

| ID | Tarea | Componente Java 17 / Spring Boot | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|---|
| T-15.01 | Definir los indicadores del panel: productos activos, pedidos por estado y ventas totales | Análisis funcional | Adecuación funcional | 3 |
| T-15.02 | Crear consultas JPQL de productos activos, pedidos por estado y ventas del agricultor | Spring Data JPA (@Query) | Eficiencia de desempeño | 6 |
| T-15.03 | Implementar PanelService que reúna los indicadores en una sola respuesta | Spring (@Service, @Transactional(readOnly = true)) | Adecuación funcional | 6 |
| T-15.04 | Crear PanelResponseDTO y su mapper | DTO, Mapper | Mantenibilidad | 4 |
| T-15.05 | Implementar el endpoint del panel | Spring Boot (@RestController) | Compatibilidad | 4 |
| T-15.06 | Garantizar que cada agricultor consulte únicamente sus propios datos | Spring (@Service) | Seguridad | 5 |
| T-15.07 | Revisar las consultas para evitar el problema N+1 (JOIN FETCH o proyecciones) | Spring Data JPA, Hibernate | Eficiencia de desempeño | 5 |
| T-15.08 | Traducir los escenarios BDD (Given-When-Then) a pruebas automatizadas [OBLIGATORIA] | JUnit 5, Mockito, MockMvc | Fiabilidad | 8 |
| | **Total estimado** | | | **41** |

## Tareas Transversales del Sprint

| ID | Tarea | Criterio ISO/IEC 25010 | Horas |
|---|---|---|---|
| T-S5.01 | Ejecutar `./mvnw clean verify` y auditar Checkstyle (0 advertencias) y JaCoCo (instrucciones y ramas de al menos 60%) | Mantenibilidad | 3 |
| T-S5.02 | Actualizar el DER, el diagrama de secuencia y el README si el sprint modifica el diseño | Mantenibilidad | 3 |
| T-S5.03 | Registrar la bitácora de Daily Scrum, el Sprint Review y la Retrospectiva en `docs/` | Adecuación funcional | 3 |

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

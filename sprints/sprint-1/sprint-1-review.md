# Sprint Review — Sprint 1

## Incremento Ejecutable

Durante el Sprint 1 se configuró la infraestructura de calidad, se definió la arquitectura
y se implementaron los endpoints base de registro de agricultores, publicación de productos
y registro de compradores. El incremento se encuentra ejecutable sobre H2 y preparado para
PostgreSQL mediante el perfil `postgres`.

## Estado del Sprint 1

| Entregable | Estado | Observación |
| :--- | :--- | :--- |
| Configuración del repositorio (README, .gitignore, checkstyle.xml) | Completado | Infraestructura de calidad definida |
| Contrato técnico DoD | Completado | Checklist formalizado y firmado por el equipo |
| Product Backlog (15 HUs con BDD) | Completado | Priorización MoSCoW y estimación Fibonacci |
| Sprint Planning y desglose técnico | Completado | 3 historias seleccionadas, 11 Story Points |
| Tablero Kanban | Completado | Columnas y WIP Limits configurados |
| Bitácora Daily Scrum | Completado | Registro de sesiones diarias |
| Implementación de endpoints | Completado | HU-01, HU-02 y HU-12 implementadas con pruebas |
| Evidencias de ejecución | Completado | Verificables con `./mvnw clean verify` y los endpoints REST |

## Evidencias

- [x] Pruebas de integración de los endpoints con MockMvc y respuesta `201 Created`.
- [x] Persistencia verificada con H2 en memoria durante las pruebas automatizadas.
- [x] Perfil PostgreSQL disponible en `application-postgres.properties`.
- [x] Colección de endpoints documentada en `README.md`.

## Verificación

- [x] Pruebas JUnit 5 pasando.
- [x] Checkstyle con 0 violaciones.
- [x] Cobertura ≥ 60% con JaCoCo.
- [x] Pipeline de GitHub Actions en verde sobre `main` y `develop`.

## Conclusión

El Sprint 1 dejó definida la arquitectura y la infraestructura de calidad, y completó la
primera implementación ejecutable del producto. Las evidencias quedaron automatizadas en
Maven y en el pipeline de integración continua.

## Sprint Goal

> "Permitir que compradores y productores encuentren la oferta agrícola del Valle del Cauca, filtrando el catálogo por categoría y municipio y buscando productos por disponibilidad, sobre la arquitectura REST y la persistencia en PostgreSQL definidas en el Sprint 1."

**Capacidad del equipo:** 11 Story Points (velocity inicial 8-11 SP, igual que el Sprint 1).

El objetivo del Sprint 2 es que el catálogo publicado en el Sprint 1 (HU-02) se pueda consultar de forma útil: filtrar, buscar y devolver resultados con los datos del productor, siempre con pruebas unitarias y cobertura mínima del 60%.

## Historias Seleccionadas para el Sprint 2

| ID | Historia | Story Points | Prioridad |
|----|----------|---------------|-----------|
| HU-04 | Filtro de Categorías y Municipio | 5 | Must Have |
| HU-13 | Búsqueda Inteligente por Municipio y Disponibilidad | 5 | Must Have |

**Total: 5 + 5 = 10 Story Points** (1 SP de holgura frente a la capacidad de 11).

### ¿Por qué estas historias?

- **HU-04:** Ambas son *Must Have* y dependen directamente del catálogo de productos de HU-02. Introduce las entidades `Categoria` y `Municipio` del DER en 3FN y las consultas filtradas con `@Query`.
- **HU-13:** Reutiliza `ProductoRepository` y el filtro por municipio de HU-04, y añade la búsqueda por término y por disponibilidad. Devuelve un DTO con los datos del productor, sin exponer información sensible.

### Dependencias y trabajo arrastrado del Sprint 1

- HU-04 y HU-13 requieren la entidad `Producto` y el endpoint de publicación de HU-02. No pueden cerrarse sin ella.
- Según la Sprint Review del Sprint 1, el código de HU-01, HU-02 y HU-12 quedó pendiente de ejecución. Ese trabajo debe estar integrado a `main` antes de que HU-04 y HU-13 pasen a *Done*.

### Lecciones del Sprint 1 aplicadas (retrospectiva)

- Las pruebas unitarias se escriben junto con el código de cada tarea, no al final del sprint.
- Cada tarea se desarrolla en su rama `feature/HUxx-nombre` y se integra solo mediante Pull Request con revisión cruzada.
- Los endpoints se implementan desde el inicio del sprint, antes que la documentación de apoyo.

## Desglose Técnico del Sprint 2

### HU-04: Filtro de Categorías y Municipio

| ID Tarea | Descripción técnica | Componente / Tecnología | Atributo ISO 25010 |
|----------|---------------------|--------------------------|----------------------|
| T04.01 | Crear entidades `Categoria` (nombre único) y `Municipio` (nombre único, departamento) con sus repositorios, según el DER | PostgreSQL + JPA / `@Entity` | Integridad |
| T04.02 | Implementar en `ProductoRepository` consultas por categoría y por municipio del productor (join `producto → agricultor → municipio`) | Spring Data JPA / `@Query` | Eficiencia de desempeño |
| T04.03 | Crear `ProductoService.filtrar(categoriaId, municipioId)` con ordenamiento por precio unitario | Spring / `@Service` | Mantenibilidad |
| T04.04 | Crear `GET /api/v1/productos/filtro?categoriaId=&municipioId=` que responda `200 OK` con la lista de productos en DTO | Spring MVC / `@RestController` | Funcionalidad |
| T04.05 | Validar parámetros (categoría y municipio existentes) y responder `404`/`400` con mensaje claro | Jakarta Validation + manejo de excepciones | Usabilidad |
| T04.06 | Escribir pruebas unitarias del filtro (casos con resultados, sin resultados y parámetros inválidos) con cobertura ≥60% | JUnit 5 + JaCoCo | Fiabilidad |

### HU-13: Búsqueda Inteligente por Municipio y Disponibilidad

| ID Tarea | Descripción técnica | Componente / Tecnología | Atributo ISO 25010 |
|----------|---------------------|--------------------------|----------------------|
| T13.01 | Implementar en `ProductoRepository` la búsqueda por término (sin distinguir mayúsculas), municipio y `cantidad_disponible > 0` | Spring Data JPA / `@Query` | Eficiencia de desempeño |
| T13.02 | Crear `BusquedaService` que combine criterios opcionales (término, municipio, disponibilidad) | Spring / `@Service` | Mantenibilidad |
| T13.03 | Crear `GET /api/v1/productos/busqueda?q=&municipioId=&disponible=` que responda `200 OK` | Spring MVC / `@RestController` | Funcionalidad |
| T13.04 | Crear `ProductoResultadoDTO` con cantidad, precio unitario y datos del productor, sin exponer `password_hash` | DTO | Seguridad |
| T13.05 | Devolver lista vacía con `200 OK` cuando no hay coincidencias y validar parámetros inválidos | Jakarta Validation | Usabilidad |
| T13.06 | Escribir pruebas unitarias de la búsqueda (con y sin filtros, sin resultados) con cobertura ≥60% | JUnit 5 + JaCoCo | Fiabilidad |

**Total Sprint 2: 5 + 5 = 10 Story Points**

## Criterios de aceptación

Los escenarios BDD de HU-04 y HU-13 están definidos en [`BACKLOG.md`](../BACKLOG.md). Cada Pull Request describe los escenarios probados y cumple el Definition of Done de [`dod.md`](dod.md).
# AgroValle Connect

![Build Status](https://img.shields.io/github/actions/workflow/status/kbalantamezu-ops/AgroValle-Connect/ci.yml?branch=main&label=build)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)

> El badge de build refleja el pipeline de GitHub Actions definido en `.github/workflows/ci.yml`.

## Visión del Producto

> Para **los productores del Valle del Cauca**, que **necesitan vender directo sin intermediarios excesivos**, **AgroValle Connect** es **una plataforma web desarrollada en Java 17 / Spring Boot** que **conecta la oferta agrícola de las fincas del Valle con la demanda comercial urbana de Cali a precio justo**. A diferencia de **los intermediarios tradicionales**, nuestro producto **garantiza trazabilidad del despacho y contratos de API transparentes entre productor y comprador**.

### El problema

El Valle del Cauca es una despensa agrícola con municipios de alta vocación agrícola como Dagua, Palmira, Buga, Tuluá, Caicedonia y Jamundí, pero la cadena de comercialización tradicional tiene fallas estructurales: intermediación excesiva que baja el precio al productor y lo sube al comprador final, falta de visibilidad en tiempo real de lo que se está cosechando, y ausencia de trazabilidad y programación logística en los despachos.

**AgroValle Connect** elimina la intermediación innecesaria, conectando directamente la oferta agrícola de las fincas del Valle del Cauca con la demanda comercial urbana de Cali y sus alrededores.

### Módulos principales

- **Productores y Ofertas**: registro de fincas por municipio y publicación de cosechas disponibles (cantidad, precio, categoría, fecha estimada de recolección).
- **Catálogo y Búsqueda Inteligente**: filtrado de la oferta por municipio de origen y disponibilidad.
- **Pedidos Directos y Stock**: reserva de inventario, carrito de compra y órdenes de compra directas.
- **Logística y Trazabilidad**: confirmación de alistamiento y seguimiento en tiempo real del despacho.

## Integrantes del equipo

- Shalom Sofia Vargas Muñoz
- Yeison Andres Cifuentes Reina
- Jadith Milena Saenz Magallanes
- Kevin Andres Balanta Mezu

## Stack Tecnológico

- **Backend**: Java 17, Spring Boot, Maven
- **Base de datos**: PostgreSQL
- **Arquitectura**: patrón MVC (/models, /views, /controllers)
- **Calidad**: Checkstyle (Google Java Style), JUnit 5 + JaCoCo (cobertura mínima 60%)
- **Automatización**: Husky (pre-commit hooks)

## Estrategia de Ramas: GitFlow

Elegimos **GitFlow** porque el proyecto está pensado para entregas versionadas a lo largo del curso (incrementos funcionales por sesión) y no para despliegue continuo diario. GitFlow nos da ramas aisladas por funcionalidad y por release, lo que:

- Permite trabajar varias Historias de Usuario en paralelo (una rama `feature/` por HU) sin que el trabajo incompleto de un integrante bloquee a los demás.
- Facilita el control de calidad: cada `feature` pasa por Pull Request y Code Review antes de integrarse a `develop`, y cada `release` se estabiliza antes de llegar a `main`.
- Da trazabilidad clara del historial mediante tags de versión (`v1.0.0`, etc.), útil para la entrega y evaluación de cada incremento.

El costo de GitFlow —mayor complejidad de ramas y posible acumulación de deuda si una rama vive demasiado tiempo— lo mitigamos manteniendo las ramas `feature/` de corta duración y sincronizando frecuentemente con `develop`.

```mermaid
gitGraph
    commit id: "Initial"
    branch develop
    checkout develop
    commit id: "Setup-Project"
    branch feature/HU-01
    checkout feature/HU-01
    commit id: "feat: logic-hu-01"
    checkout develop
    merge feature/HU-01
    branch release/v1.0.0
    checkout release/v1.0.0
    commit id: "fix: minor-bug"
    checkout main
    merge release/v1.0.0 tag: "v1.0.0"
    checkout develop
    merge release/v1.0.0
```

## Convención de Commits

Usamos [Conventional Commits](https://www.conventionalcommits.org/) ligados a Versionamiento Semántico:

- `feat:` nueva funcionalidad (incrementa MINOR)
- `fix:` corrección de error (incrementa PATCH)
- `docs:` / `style:` / `refactor:` / `test:` / `chore:` no afectan la versión pública
- `BREAKING CHANGE:` cambio que rompe compatibilidad (incrementa MAJOR)

Ejemplo: `feat(estudiantes): implementar logica de validacion de correo institucional`

## Calidad y Automatización

Husky y Checkstyle reducen la deuda técnica antes de escribir lógica de negocio: el hook `.husky/pre-commit` ejecuta `./mvnw test` y `./mvnw checkstyle:check` en cada commit. El workflow `.github/workflows/ci.yml` ejecuta `./mvnw verify` en cada cambio de `main` y en cada Pull Request; además, JaCoCo exige una cobertura mínima del 60%.

Para activar Husky localmente, cada integrante debe ejecutar `npm install` desde la raíz del repositorio.



## Tablero Kanban y Políticas Explícitas
 
El equipo gestiona el flujo de trabajo del Sprint 1 en un tablero de **GitHub Projects** vinculado a este repositorio.
 
**Tablero:** [URL del tablero en GitHub Projects](URL_DEL_TABLERO)
 
### Columnas y límites de trabajo en progreso (WIP Limits)
 
| Columna | Límite WIP | Regla de entrada | Regla de salida |
|---|---|---|---|
| **Product Backlog** | Sin límite | Historia de Usuario (HU) con prioridad MoSCoW y puntos Fibonacci asignados | La HU se selecciona en el Sprint Planning |
| **Sprint Backlog (To Do)** | Sin límite | Tarea técnica de máximo 8 horas, vinculada a su HU como sub-issue | Un integrante la toma y crea su rama `feature/*` |
| **In Progress** | **Máximo 3 tareas** | La tarea tiene un responsable y una rama corta (`feature/HUxx-nombre`) | Se abre el Pull Request |
| **Code Review / Testing** | **Máximo 2 Pull Requests** | PR abierto, con `mvn test` y `mvn checkstyle:check` pasando en local | Aprobación explícita de un revisor |
| **Done** | Sin límite | 1 aprobación (Approve), pruebas JUnit 5 en verde y 0 errores de Checkstyle | Merge a `main` |
 
### Políticas explícitas
 
1. **Regla de Done:** Ninguna tarjeta se mueve a *Done* si el Pull Request no cuenta con la aprobación de al menos un revisor (Peer Review), las pruebas JUnit 5 pasando y 0 errores de Checkstyle.
2. **Cumplimiento del DoD:** Toda tarjeta en *Done* cumple el Definition of Done definido en [`docs/dod.md`](docs/dod.md).
3. **Respeto del WIP:** Si una columna alcanza su límite, ningún integrante toma una tarjeta nueva hasta que se libere un cupo. Primero se apoya en terminar el trabajo en curso.
4. **Flujo sin saltos:** Ninguna tarjeta omite columnas. Todas pasan por *Code Review / Testing* antes de *Done*.
5. **Revisión cruzada:** Ningún integrante aprueba su propio Pull Request. El revisor es siempre una persona distinta al autor.
6. **Contenido del Pull Request:** Cada PR describe los escenarios BDD probados y adjunta evidencia de las pruebas ejecutadas.
7. **Convención de ramas y commits:** Cada tarea se desarrolla en una rama corta (`feature/HUxx-nombre`) y los commits siguen Conventional Commits (`feat`, `fix`, `test`).
8. **Unidad de trabajo del tablero:** Las tarjetas que avanzan por las columnas son las tareas técnicas (`T-xx.xx`). Las HUs se consideran terminadas cuando todas sus sub-issues están en *Done*.
 


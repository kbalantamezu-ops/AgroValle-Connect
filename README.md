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

## Cómo ejecutar

```bash
# Desarrollo (base de datos H2 en memoria + datos de ejemplo)
./mvnw spring-boot:run

# Producción / integración con PostgreSQL
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres

# Verificación completa (checkstyle + tests + cobertura)
./mvnw verify
```

La aplicación queda disponible en `http://localhost:8080`:

| Página | Descripción |
| --- | --- |
| `/` | Catálogo con búsqueda inteligente, filtros por municipio/categoría/disponibilidad, precios promedio regionales, carrito de compra, contacto directo con el productor y reseñas. |
| `/publicar.html` | Registro de agricultores y publicación de productos. |
| `/pedidos.html` | Consulta del estado de un pedido, línea de tiempo, avance de estado y notificaciones. |

La consola de H2 está disponible en `/h2-console` (JDBC URL `jdbc:h2:mem:agrovalle`, usuario `sa`).

## API REST (`/api/v1`)

| Método | Endpoint | Historia de Usuario |
| --- | --- | --- |
| `POST` | `/agricultores` | HU-01 Registro de Agricultores |
| `GET` / `PUT` | `/agricultores`, `/agricultores/{id}` | HU-12 Editar Perfil |
| `POST` | `/productos` | HU-02 Publicación de Productos |
| `GET` | `/productos?q=&municipio=&categoria=&disponible=` | HU-04 / HU-13 Búsqueda y Filtros |
| `GET` | `/precios/promedio?categoria=CAFE` | HU-03 Precios Regionales |
| `POST` | `/pedidos` | HU-09 / HU-11 Pedido y Descuento de Stock |
| `GET` | `/medios-pago` | HU-06 Medios de Pago |
| `GET` | `/pedidos`, `/pedidos/{id}` | HU-10 Estado del Pedido |
| `PATCH` | `/pedidos/{id}/estado` | HU-10 / HU-14 Cambio de Estado y Notificación |
| `GET` | `/pedidos/{id}/notificaciones` | HU-14 Notificaciones |
| `POST` / `GET` | `/resenas`, `/resenas?agricultorId=` | HU-08 Satisfacción y Reseñas |
| `GET` | `/catalogo/municipios`, `/catalogo/categorias` | Catálogo del Valle |

Los errores siguen el formato `{ timestamp, status, error, message, path, fieldErrors }`.
Cada pedido queda asociado a las tablas del DER `MEDIOS_PAGO` (efectivo, transferencia, tarjeta) y `PAGOS`
(monto `NUMERIC(12,2)`, estado y fecha de pago); el medio se selecciona al confirmar el pedido (HU-06).


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

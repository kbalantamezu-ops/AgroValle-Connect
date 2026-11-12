# Changelog

Todos los cambios relevantes del proyecto se documentan en este archivo.
El formato sigue [Keep a Changelog](https://keepachangelog.com/es-ES/1.0.0/) y
[Versionamiento Semántico](https://semver.org/lang/es/).

## [1.1.0] - 2026-11-11

### Agregado

- Autenticación stateless con JWT HS256 y roles `ADMIN`, `AGRICULTOR` y `COMPRADOR`.
- Endpoints `/api/v1/auth/login`, `/auth/me`, `/auth/registro/comprador` y `/auth/registro/agricultor`.
- Autorización por propiedad: cada comprador ve sus pedidos y cada agricultor gestiona sus productos y pedidos.
- Página `/login.html` con inicio de sesión, registro de compradores y registro de agricultores con cuenta vinculada.
- El frontend adjunta `Authorization: Bearer` y adapta catálogo, checkout, pedidos y panel según el rol.
- Cuentas demo de comprador, agricultor y administrador en el perfil de desarrollo.

### Cambiado

- El listado de pedidos se filtra por comprador, agricultor o administrador.
- Publicar productos, crear pedidos, reseñar y consultar pedidos requieren autenticación.
- `Usuario` ahora incluye `rol` y `agricultorId`, y las respuestas nunca exponen la contraseña.
- La configuración JWT se toma de `app.jwt.secret` / `APP_JWT_SECRET` (obligatoria en el perfil `postgres`).

### Seguridad

- Filtro JWT, respuestas JSON de 401/403 y `PasswordEncoder` BCrypt gestionado por Spring Security.
- Consola H2 deshabilitada en el perfil `postgres`.

### Pendiente para próximas versiones

- Migraciones de base de datos con Flyway o Liquibase.
- Integración del costo de flete en el checkout y despliegue público.
- Reposición de stock al cancelar pedidos y edición/eliminación de productos.

## [1.0.0] - 2026-11-11

### Agregado

- Catálogo web con búsqueda, filtros por municipio/categoría/disponibilidad y precios promedio.
- Carrito de compras, checkout, medios de pago y creación de pedidos con descuento de stock.
- Consulta de pedidos, línea de tiempo, notificaciones y reseñas de agricultores.
- Panel del agricultor con publicaciones, pedidos recibidos y ventas acumuladas (HU-15).
- Cálculo de fletes con patrón Strategy.
- Documentación de Sprint 2, retrospectiva y bitácora actualizada.
- Dockerfile, Docker Compose y variables de entorno de ejemplo.

### Corregido

- Marcadores de conflicto de merge en `Producto.java` y `pom.xml`.
- Configuración de Checkstyle y compilación del proyecto.
- Advertencias de estilo en `TarifaFleteBase` y `UsuarioService`.

### Cambiado

- Endpoints de fletes y usuarios versionados bajo `/api/v1`.
- CORS configurable mediante `APP_CORS_ALLOWED_ORIGINS`.
- Los datos de ejemplo se controlan con `APP_SEED_DEMO_DATA` y quedan desactivados en PostgreSQL.

### Seguridad

- Contraseñas de compradores cifradas con BCrypt.

### Pendiente para próximas versiones

- Autenticación y autorización con JWT.
- Migraciones de base de datos con Flyway o Liquibase.
- Integración del costo de flete en el checkout y despliegue público.

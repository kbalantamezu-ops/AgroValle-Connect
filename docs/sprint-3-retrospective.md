# Retrospectiva Sprint 3 — Start-Stop-Continue

**Sprint:** 3 — Despliegue Continuo a Staging
**Fecha:** 2026-11-12
**Objetivo:** Publicar AgroValle Connect en un ambiente de Staging en la nube y
automatizar el despliegue desde la rama `main`.

## Resultado del sprint

El sprint cumplió su objetivo: la aplicación corre en Render con PostgreSQL en
Neon, el pipeline de GitHub Actions ejecuta los quality gates y dispara el deploy,
y el endpoint `GET /api/v1/estado` permite verificar la salud del servicio y la
conexión con la base de datos.

| Entregable | Estado |
| --- | --- |
| Perfil `staging` (`application-staging.yml`) | Completado |
| Esquema PostgreSQL para Staging (`docs/diseno/schema.sql`) | Completado |
| Endpoint de salud `GET /api/v1/estado` | Completado |
| Pipeline de CD (`.github/workflows/cd.yml`) | Completado |
| ADR de despliegue en PaaS | Completado |
| Diagramas de diseño en Markdown/Mermaid | Completado |
| Evidencia en vivo con captura del `200 OK` | Pendiente |

## Start (Empezar a hacer)

- Probar el pipeline en una rama de corta duración antes de fusionar a `main`.
- Validar el esquema y las credenciales de Neon antes del primer despliegue.
- Configurar `STAGING_APP_URL` para que el smoke test contra Render se ejecute
  automáticamente en cada despliegue.
- Mantener los diagramas de diseño en texto (Mermaid) dentro del repositorio para
  que evolucionen junto con el código.
- Revisar los logs de Render y ejecutar el health check como parte del cierre de
  cada sprint.

## Stop (Dejar de hacer)

- Escribir credenciales o URLs reales dentro de archivos versionados.
- Asumir que el primer despliegue funcionará sin haber ejecutado el esquema en la
  base de datos.
- Dejar la evidencia y la documentación del sprint para el último momento.
- Depender de `ddl-auto=update` como estrategia definitiva de esquema.
- Cambiar variables de entorno sin actualizar el README ni el ADR.

## Continue (Seguir haciendo)

- Usar Conventional Commits y ramas cortas por historia.
- Ejecutar `./mvnw clean verify` (Checkstyle + pruebas + cobertura) antes de cada
  push.
- Documentar las decisiones de arquitectura con ADR.
- Mantener la cobertura por encima del 60% y el pipeline en verde.
- Revisar los endpoints con pruebas de integración y smoke tests.
- Actualizar el tablero Kanban y cerrar las issues completadas al final del sprint.

## Acciones de mejora

| Acción | Responsable | Estado |
| --- | --- | --- |
| Convertir los diagramas de diseño a Markdown/Mermaid en `docs/diagramas-uml/` | Equipo | Completado |
| Documentar la retrospectiva del Sprint 3 | Equipo | Completado |
| Agregar la captura del `200 OK` de Staging al `sprint-3-review.md` | Equipo | Pendiente |
| Configurar `STAGING_APP_URL` para habilitar el smoke test automático | Equipo | Pendiente |
| Reemplazar `ddl-auto=update` por migraciones con Flyway o Liquibase | Equipo | Pendiente |
| Ejecutar el pipeline en una rama de prueba antes de fusionar a `main` | Equipo | Pendiente |

## Lecciones aprendidas

1. **La cadena de conexión de Neon no es JDBC por defecto.** Hay que agregar el
   prefijo `jdbc:` y conservar `?sslmode=require`; si se omite, Hikari falla al
   arrancar en Render.
2. **El esquema debe existir antes del primer deploy.** Si la base de datos está
   vacía, la aplicación arranca pero falla al consultar las tablas.
3. **Los secretos van fuera del repositorio.** GitHub Secrets y las variables de
   entorno de Render mantienen las credenciales separadas del código.
4. **Render suspende los servicios gratuitos.** El primer request tras un periodo
   de inactividad puede tardar entre 30 y 60 segundos, algo que debe considerarse
   al tomar la evidencia.
5. **La documentación en texto envejece mejor.** Los diagramas en Mermaid se
   revisan en el mismo Pull Request que el cambio de código, a diferencia de una
   imagen exportada.

## Conclusión

El Sprint 3 dejó un incremento desplegable y verificable de extremo a extremo:
código, base de datos, pipeline y evidencia conviven en el repositorio. Las
acciones pendientes se concentran en la evidencia visual del `200 OK`, el smoke
test automático y la migración a un esquema versionado con Flyway o Liquibase.

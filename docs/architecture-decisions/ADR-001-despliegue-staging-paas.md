# ADR-001: Adopción de Plataforma PaaS y GitHub Actions para Despliegue Continuo (CD) en Staging

* **Estado:** Aprobado
* **Fecha:** 2026-11-12
* **Autores:** Célula de Desarrollo AgroValle Connect — Shalom Sofia Vargas Muñoz, Yeison Andres Cifuentes Reina, Jadith Milena Saenz Magallanes y Kevin Andres Balanta Mezu
* **Contexto Técnico:** Asignatura Ingeniería de Software II — UNIAJC

## Contexto y Problemática

El proyecto integrador AgroValle Connect requiere transicionar de la ejecución local en máquinas de desarrollo hacia un ambiente de Staging ejecutable en la nube. Se necesita una solución automatizable, integrada con el pipeline de GitHub Actions, que permita publicar incrementos funcionales de manera continua sin incurrir en costos de infraestructura ni en administración manual de servidores.

El proyecto ya cuenta con Integración Continua (CI): `./mvnw clean verify` ejecuta Checkstyle, las pruebas JUnit 5 y el umbral de cobertura de JaCoCo (≥ 60 %). El siguiente paso es que una integración verificada en `main` llegue automáticamente a un ambiente de preproducción con PostgreSQL gestionado.

## Opciones Consideradas

1. **Despliegue Manual en Servidor VPS:** Ofrece control completo del sistema operativo, pero exige configuraciones manuales complejas, gestión de parches de seguridad y scripts de despliegue propensos al error humano.
2. **Despliegue Automatizado en Plataforma PaaS (Render + Neon):** Permite integración mediante Webhooks de despliegue, aprovisionamiento gestionado de PostgreSQL y ejecución transparente del contenedor Spring Boot en Java 17. La capa gratuita es suficiente para un ambiente de Staging académico.
3. **Plataforma PaaS alternativa (Railway):** Cumple los mismos objetivos, pero el disparo de despliegue requiere tokens o CLI adicionales. Se descarta para mantener el pipeline simple y basado en un único Webhook.

## Decisión Adoptada

Se aprueba la **Opción 2: Render como PaaS, Neon como PostgreSQL gestionado y GitHub Actions como orquestador de CD**.

Cada integración validada en la rama `main` disparará automáticamente el pipeline `.github/workflows/cd.yml`, el cual:

1. Ejecuta los quality gates de CI (`./mvnw clean verify`).
2. Solo si el job anterior pasa, invoca el Webhook de despliegue de Render (`DEPLOY_HOOK_URL`).
3. Opcionalmente, ejecuta un smoke test contra `GET /api/v1/estado` cuando la variable `STAGING_APP_URL` está definida.

La aplicación se ejecuta con el perfil `staging` (`application-staging.yml`), que extrae la cadena JDBC y las credenciales desde variables de entorno. Por compatibilidad con el código existente se usa el prefijo `app.jwt.*` (clase `JwtProperties`) en lugar de `security.jwt.*`; el valor del secreto se inyecta desde `JWT_SECRET_KEY`.

### Configuración de secretos

Los secretos viven en GitHub Actions y se duplican como variables de entorno en Render, porque GitHub Secrets solo están disponibles durante la ejecución del workflow:

| Secreto | Uso |
| --- | --- |
| `STAGING_DB_URL` | Cadena JDBC de Neon con `sslmode=require`. |
| `STAGING_DB_USER` | Rol de PostgreSQL de Staging. |
| `STAGING_DB_PASSWORD` | Contraseña del rol de Staging. |
| `JWT_SECRET_KEY` | Clave HS256 de al menos 32 caracteres. |
| `DEPLOY_HOOK_URL` | Webhook de despliegue de Render. |

En Render se configuran además `SPRING_PROFILES_ACTIVE=staging` y, opcionalmente, `APP_SEED_DEMO_DATA=true` para una demostración con datos de ejemplo.

### Estructura de la base de datos

El esquema de Staging se crea con `docs/diseno/schema.sql` en la consola SQL de Neon. El perfil `staging` mantiene `ddl-auto=update` para que Hibernate pueda completar ajustes menores, pero la fuente de verdad del esquema es el script versionado. La adopción de Flyway o Liquibase queda como evolución futura para reemplazar este paso manual.

## Consecuencias y Mantenibilidad

### Consecuencias Positivas

* Automatización completa del ciclo de entrega (*Continuous Deployment*).
* Seguridad garantizada al inyectar contraseñas y llaves JWT desde GitHub Secrets y variables de entorno, sin credenciales en el repositorio.
* Disponibilidad de una URL pública para validación del Product Owner y pruebas de integración.
* Separación de responsabilidades: GitHub Actions verifica la calidad y Render ejecuta el artefacto.
* Bajo costo: capa gratuita de Render y Neon.

### Consecuencias Negativas / Limitaciones

* La capa gratuita de Render puede experimentar un tiempo de latencia inicial (*Cold Start*) de aproximadamente 30 a 60 segundos tras periodos prolongados de inactividad.
* Las credenciales deben mantenerse sincronizadas en dos paneles (GitHub Secrets y Render Environment Variables); una rotación exige actualizar ambos.
* El esquema se inicializa manualmente en Neon; una migración automática con Flyway queda pendiente.
* El Webhook de Render no devuelve el resultado del build; el smoke test contra `/api/v1/estado` es la verificación efectiva del despliegue.

## Cumplimiento y Trazabilidad

* **ISO/IEC 25010 — Mantenibilidad:** la configuración por perfiles elimina valores fijos del código y permite cambiar de entorno sin recompilar.
* **ISO/IEC 25010 — Seguridad:** las credenciales no se versionan; se inyectan en tiempo de ejecución.
* **DoD del laboratorio:** perfil `application-staging.yml`, secretos obligatorios, pipeline `cd.yml`, ADR y evidencia en `docs/sprint-3-review.md`.

## Referencias

* Documento del laboratorio: *Despliegue Continuo (CD), GitHub Secrets y Arquitectura Evolutiva (ADRs)* — UNIAJC, Sesión 12.
* Plantilla de ADR de Michael Nygard.
* Render Deploy Hooks: <https://render.com/docs/deploy-hooks>
* Neon connection strings: <https://neon.tech/docs/connect/connect-from-any-app>

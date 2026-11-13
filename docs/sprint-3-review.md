# Sprint 3 Review — Despliegue Continuo a Staging

**Fecha:** 2026-11-12
**Objetivo:** Publicar AgroValle Connect en un ambiente de Staging en la nube y automatizar el despliegue desde la rama `main`.

## Entregables

| Entregable | Archivo | Estado |
| --- | --- | --- |
| Perfil de Staging | `src/main/resources/application-staging.yml` | Completado |
| Script de base de datos | `docs/diseno/schema.sql` | Completado |
| Endpoint de salud | `GET /api/v1/estado` | Completado |
| Pipeline de CD | `.github/workflows/cd.yml` | Completado |
| ADR de despliegue | `docs/architecture-decisions/ADR-001-despliegue-staging-paas.md` | Completado |
| Evidencia en vivo | Este documento | Pendiente de la captura final del `200 OK` |

## Infraestructura

| Componente | Servicio |
| --- | --- |
| Base de datos | Neon PostgreSQL — `agrovalle_staging` |
| Aplicación | Render Web Service `AgroValle-Connect` (Docker, branch `main`) |
| Orquestación | GitHub Actions — `.github/workflows/cd.yml` |
| Secretos | GitHub Secrets + Render Environment Variables |

**URL base de Staging:** `https://agrovalle-connect-mrv2.onrender.com`

> El servicio de Render se identifica con el Service ID `srv-dbr6msl9fdbs73dfq5g`.
> Existe otro servicio con una URL parecida (`agrovalle-connect.onrender.com`) que pertenece a
> un proyecto distinto y **no** debe usarse como evidencia.

## Evidencia del pipeline

1. Abrir la pestaña **Actions** del repositorio.
2. Entrar al run **Continuous Deployment (CD) - Staging Pipeline**.
3. Verificar que los jobs `Verification & Quality Gates (CI)` y `Deploy Application to Staging Cloud` estén en verde.

**URL del run:** `https://github.com/kbalantamezu-ops/AgroValle-Connect/actions/runs/43352052011`

## Evidencia de la API en Staging

**URL base de Staging:** `https://agrovalle-connect-mrv2.onrender.com`

### Health check

```bash
curl -i https://agrovalle-connect-mrv2.onrender.com/api/v1/estado
```

Respuesta esperada (HTTP 200 OK):

```json
{
  "aplicacion": "AgroValle Connect",
  "perfil": "staging",
  "estado": "OK",
  "baseDatos": "PostgreSQL Connected"
}
```

### Prueba de escritura en PostgreSQL

```bash
curl -X POST https://agrovalle-connect-mrv2.onrender.com/api/v1/auth/registro/agricultor \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Prueba Staging",
    "cedula": "9999999999",
    "municipio": "CALI",
    "telefono": "3000000000",
    "email": "staging@agrovalle.co",
    "password": "Demo1234*"
  }'
```

Respuesta esperada: `201 Created` con el usuario creado. Esto demuestra la comunicación con PostgreSQL remoto.

> Nota: el documento del laboratorio menciona `/api/v1/auth/register`, pero el proyecto implementa la ruta real `/api/v1/auth/registro/agricultor`.

## Checklist del Definition of Done

- [x] `application-staging.yml` presente y con referencias a variables de entorno.
- [x] Secretos `STAGING_DB_URL`, `STAGING_DB_USER`, `STAGING_DB_PASSWORD`, `JWT_SECRET_KEY` y `DEPLOY_HOOK_URL` configurados en GitHub.
- [x] `cd.yml` ejecutado con éxito en `main`.
- [x] ADR `ADR-001-despliegue-staging-paas.md` firmado por el equipo.
- [ ] Evidencia en vivo con URL y captura del `200 OK` (completar después del primer despliegue).

## Observaciones

* El primer despliegue de Render puede tardar varios minutos y fallar si el esquema aún no se ejecutó en Neon. En ese caso, ejecutar `docs/diseno/schema.sql` y relanzar el pipeline desde **Actions → Run workflow**.
* Los servicios gratuitos de Render se suspenden por inactividad; el primer request puede tardar entre 30 y 60 segundos.

### Formato obligatorio de `STAGING_DB_URL`

Neon entrega la cadena con el esquema `postgresql://`, pero Spring Boot necesita una URL JDBC.
Hay que agregar el prefijo `jdbc:` y dejar los parámetros de conexión:

```text
postgresql://usuario:clave@ep-xxxx-123456.us-east-2.aws.neon.tech/agrovalle_staging?sslmode=require
```

queda como:

```text
jdbc:postgresql://ep-xxxx-123456.us-east-2.aws.neon.tech/agrovalle_staging?sslmode=require
```

Recomendaciones:

* Usar el host **sin** el sufijo `-pooler` mientras Hibernate gestione el esquema.
* No incluir usuario ni contraseña dentro de la URL; van en `STAGING_DB_USER` y `STAGING_DB_PASSWORD`.
* Terminar siempre en `?sslmode=require`, porque Neon rechaza conexiones sin TLS.
* Si la cadena trae `channel_binding=require`, se puede omitir sin afectar la conexión.

Si el build de Render falla con un error de Hikari o `SQLException`, casi siempre es este valor.

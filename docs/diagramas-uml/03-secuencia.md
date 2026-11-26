# Diagrama de Secuencia

Flujos de registro de agricultores (HU-01) y publicación de productos (HU-02) a
través de las cuatro capas.

```mermaid
sequenceDiagram
    autonumber
    actor A as Agricultor
    participant C as Controller
    participant S as Service
    participant R as Repository
    participant DB as PostgreSQL

    rect rgb(235, 240, 250)
        Note over A,DB: HU-01 · Registro de agricultores
        A->>C: POST /api/v1/auth/registro/agricultor
        C->>S: registrar(datos)
        S->>S: cifrar contraseña (BCrypt)
        S->>R: guardar agricultor
        R->>DB: INSERT
        DB-->>R: OK
        R-->>S: agricultor persistido
        S-->>C: DTO de respuesta
        C-->>A: 201 Created
    end

    rect rgb(235, 240, 250)
        Note over A,DB: HU-02 · Publicación de productos
        A->>C: POST /api/v1/productos (JWT)
        C->>S: publicar(producto)
        S->>S: validar fecha, precio y cantidad
        S->>R: guardar producto
        R->>DB: INSERT
        DB-->>R: OK
        R-->>S: producto persistido
        S-->>C: DTO con ID
        C-->>A: 201 Created + ID
    end
```

## Notas

- El diagrama original mostraba `POST /api/v1/auth/register`; la ruta realmente
  implementada es `POST /api/v1/auth/registro/agricultor`.
- La publicación de productos exige un token JWT con rol `AGRICULTOR` o `ADMIN`.
- El controlador no conoce el repositorio: delega toda la lógica en el servicio.
- El servicio aplica las reglas de negocio antes de persistir: unicidad de cédula
  y correo, cifrado BCrypt, fechas válidas, precio positivo y stock no negativo.

> Fuente original: `docs/diseno/diagrama-secuencia (1).png`.

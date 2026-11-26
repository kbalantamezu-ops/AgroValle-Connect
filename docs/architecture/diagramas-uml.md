# Diagramas UML AgroValle-Connect

# Diagrama de Componentes

Muestra la separación de AgroValle Connect en las cuatro capas del patrón MVC y
las dependencias permitidas entre ellas. Cada capa solo conoce a la inmediatamente
inferior: **controlador → servicio → repositorio → modelo/persistencia**.

```mermaid
flowchart TB
    subgraph controller["Capa controller"]
        PC["ProductoController"]
        AC["AuthController"]
    end

    subgraph service["Capa service"]
        PS["ProductoService"]
        AS["AgricultorService"]
    end

    subgraph repository["Capa repository"]
        PR["ProductoRepository"]
        AR["AgricultorRepository"]
    end

    subgraph model["Capa model"]
        P["Producto"]
        A["Agricultor"]
    end

    DB[("PostgreSQL")]

    PC --> PS
    AC --> AS
    PS --> PR
    AS --> AR
    PR --> P
    AR --> A
    P --> DB
    A --> DB
```

# Diagrama de Despliegue

## 1. Despliegue lógico

Vista general de los nodos que participan en una petición: el cliente, el servidor
de aplicación Spring Boot y la base de datos PostgreSQL.

```mermaid
flowchart LR
    C["Cliente<br/>Navegador · Postman"] -->|"HTTPS · JSON"| A["Servidor de aplicación<br/>Spring Boot · Java 17"]
    A -->|JDBC| DB[("Base de datos<br/>PostgreSQL")]
```

## 2. Despliegue en Staging (Sprint 3)

El ambiente de preproducción se despliega automáticamente desde la rama `main`.

```mermaid
flowchart LR
    DEV["Desarrollador"] -->|"push a main"| GH["GitHub Actions<br/>ci.yml + cd.yml"]
    GH -->|"quality gates OK"| R["Render<br/>Web Service (Docker)"]
    GH -.->|"deploy hook"| R
    R -->|"JDBC · sslmode=require"| N[("Neon<br/>PostgreSQL agrovalle_staging")]
    GH -.->|"smoke test GET /api/v1/estado"| R
```

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

# Diagrama Entidad-Relación (DER)

Modelo de datos de AgroValle Connect en Tercera Forma Normal (3FN). Cada tabla
tiene llave primaria simple y las dependencias entre entidades se resuelven con
llaves foráneas, sin grupos repetitivos ni dependencias transitivas.

```mermaid
erDiagram
    MUNICIPIOS {
        bigint id PK
        varchar nombre UK
        varchar departamento
    }

    AGRICULTORES {
        bigint id PK
        varchar nombre
        varchar cedula UK
        varchar email UK
        varchar password_hash
        bigint municipio_id FK
        timestamp fecha_registro
    }

    USUARIOS {
        bigint id PK
        varchar nombre
        varchar cedula UK
        varchar email UK
        varchar password_hash
        bigint municipio_id FK
        timestamp fecha_registro
    }

    CATEGORIAS {
        bigint id PK
        varchar nombre UK
    }

    PRODUCTOS {
        bigint id PK
        bigint agricultor_id FK
        bigint categoria_id FK
        varchar tipo
        text descripcion
        numeric precio_unitario
        varchar unidad_medida
        numeric cantidad_disponible
        date fecha_cosecha
        timestamp fecha_publicacion
        boolean activo
    }

    MEDIOS_CONTACTO {
        bigint id PK
        bigint agricultor_id FK
        varchar tipo
        varchar valor
    }

    MEDIOS_PAGO {
        bigint id PK
        varchar nombre UK
    }

    PEDIDOS {
        bigint id PK
        bigint usuario_id FK
        bigint agricultor_id FK
        varchar estado
        timestamp fecha_pedido
    }

    PAGOS {
        bigint id PK
        bigint pedido_id FK
        bigint medio_pago_id FK
        numeric monto
        varchar estado
        timestamp fecha_pago
    }

    DETALLE_PEDIDOS {
        bigint id PK
        bigint pedido_id FK
        bigint producto_id FK
        numeric cantidad
        numeric precio_unitario
    }

    ITEMS_CARRITO {
        bigint id PK
        bigint usuario_id FK
        bigint producto_id FK
        numeric cantidad
        timestamp fecha_agregado
    }

    MENSAJES_CONTACTO {
        bigint id PK
        bigint usuario_id FK
        bigint agricultor_id FK
        bigint producto_id FK
        text contenido
        timestamp fecha_envio
    }

    DESPACHOS {
        bigint id PK
        bigint pedido_id FK
        bigint vehiculo_id FK
        bigint municipio_origen_id FK
        bigint municipio_destino_id FK
        varchar estado
        timestamp fecha_estimada_entrega
    }

    VEHICULOS {
        bigint id PK
        varchar tipo
        varchar placa UK
        numeric capacidad_kg
        boolean disponible
    }

    RESENAS {
        bigint id PK
        bigint pedido_id FK
        smallint calificacion_calidad
        smallint calificacion_puntualidad
        text comentario
        timestamp fecha_resena
    }

    NOTIFICACIONES {
        bigint id PK
        bigint pedido_id FK
        varchar estado
        varchar mensaje
        boolean leida
        timestamp fecha_creacion
    }

    MUNICIPIOS ||--o{ AGRICULTORES : "ubica a"
    MUNICIPIOS ||--o{ USUARIOS : "ubica a"
    USUARIOS ||--o{ AGRICULTORES : contacta
    AGRICULTORES ||--o{ MEDIOS_CONTACTO : define
    AGRICULTORES ||--o{ PRODUCTOS : publica
    CATEGORIAS ||--o{ PRODUCTOS : clasifica
    USUARIOS ||--o{ ITEMS_CARRITO : "agrega al carrito"
    PRODUCTOS ||--o{ ITEMS_CARRITO : "se agrega en"
    USUARIOS ||--o{ MENSAJES_CONTACTO : envia
    AGRICULTORES ||--o{ MENSAJES_CONTACTO : recibe
    PRODUCTOS ||--o{ MENSAJES_CONTACTO : motiva
    USUARIOS ||--o{ PEDIDOS : realiza
    AGRICULTORES ||--o{ PEDIDOS : atiende
    PEDIDOS ||--o{ DETALLE_PEDIDOS : contiene
    PRODUCTOS ||--o{ DETALLE_PEDIDOS : "se incluye en"
    MEDIOS_PAGO ||--o{ PAGOS : define
    PEDIDOS ||--o| PAGOS : "se paga con"
    MUNICIPIOS ||--o{ PEDIDOS : origen
    MUNICIPIOS ||--o{ PEDIDOS : destino
    PEDIDOS ||--o| DESPACHOS : "se despacha en"
    VEHICULOS ||--o{ DESPACHOS : transporta
    PEDIDOS ||--o| RESENAS : recibe
    PEDIDOS ||--o{ NOTIFICACIONES : genera
```
# Arquitectura MVC en 4 Capas

Vista de paquetes y capas de la aplicación. La "vista" es el cliente HTTP que
consume la API REST; el controlador valida, el servicio aplica las reglas de
negocio y el modelo/repositorio persiste en PostgreSQL.

```mermaid
flowchart TD
    A["1. Vista<br/>Cliente HTTP: Postman, Swagger o front web"] -->|"JSON sobre HTTP"| B["2. Controlador<br/>@RestController + DTO + validación"]
    B --> C["3. Servicio<br/>@Service: reglas de negocio"]
    C --> D["4. Modelo y persistencia<br/>@Repository + @Entity"]
    D -->|"JPA / JDBC"| E[("PostgreSQL")]
```

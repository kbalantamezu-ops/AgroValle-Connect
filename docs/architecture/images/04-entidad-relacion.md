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

## Justificación de la 3FN

- **1FN:** todos los atributos son atómicos y cada tabla tiene llave primaria. Los
  productos de un pedido van en `DETALLE_PEDIDOS`, no en columnas `producto1`,
  `producto2`.
- **2FN:** todas las tablas usan llave primaria simple (`id`), por lo que ningún
  atributo depende de solo una parte de la llave.
- **3FN:** no hay dependencias transitivas. El nombre del municipio y el de la
  categoría viven en sus propias tablas y se referencian por FK.
- **Decisión de diseño:** `DETALLE_PEDIDOS.precio_unitario` guarda una copia del
  precio al momento de la compra. No es redundancia que viole la 3FN, sino un dato
  histórico: si el productor cambia el precio mañana, el pedido antiguo no cambia.

## Correspondencia con el esquema de Staging

`docs/diseno/schema.sql` es la versión simplificada que se ejecuta en Neon para el
laboratorio de Despliegue Continuo (tablas `agricultores`, `usuarios`, `productos`,
`pedidos`, `items_pedido`, `pagos`, etc.). El DER de este documento es el modelo de
diseño completo y canónico.

> Fuente original: `docs/architecture/images/modelo-entidad-relacion.png` y
> [`docs/architecture/images/modelo-datos-der.png`](modelo-datos-der.png).
> Ver también [`docs/diseno/modelo-datos.md`](../../diseno/modelo-datos.md).

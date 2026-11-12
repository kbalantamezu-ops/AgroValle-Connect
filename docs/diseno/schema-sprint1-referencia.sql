
CREATE TYPE estado_producto AS ENUM ('DISPONIBLE', 'AGOTADO', 'INACTIVO');
CREATE TYPE estado_pedido   AS ENUM ('PENDIENTE', 'CONFIRMADO', 'EN_TRANSPORTE', 'ENTREGADO', 'CANCELADO');
CREATE TYPE metodo_pago     AS ENUM ('TARJETA', 'PSE', 'NEQUI', 'EFECTIVO_CONTRAENTREGA');
CREATE TYPE estado_pago     AS ENUM ('PENDIENTE', 'APROBADO', 'RECHAZADO');
CREATE TYPE tipo_remitente  AS ENUM ('USUARIO', 'AGRICULTOR');

-- 1. municipios (HU-04, HU-13)
CREATE TABLE municipios (
    id            SERIAL PRIMARY KEY,
    nombre        VARCHAR(100) NOT NULL,
    departamento  VARCHAR(100) NOT NULL DEFAULT 'Valle del Cauca',
    UNIQUE (nombre, departamento)
);

-- 2. categorias (HU-02, HU-04)
CREATE TABLE categorias (
    id      SERIAL PRIMARY KEY,
    nombre  VARCHAR(80) NOT NULL UNIQUE
);

-- 3. agricultores (HU-01)
CREATE TABLE agricultores (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(150) NOT NULL,
    cedula_nit    VARCHAR(20)  NOT NULL UNIQUE,
    predio        VARCHAR(150) NOT NULL,
    municipio_id  INT          NOT NULL REFERENCES municipios(id),
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,          -- BCrypt
    telefono      VARCHAR(20),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- 4. usuarios / compradores (HU-12)
CREATE TABLE usuarios (
    id            BIGSERIAL PRIMARY KEY,
    nombre        VARCHAR(150) NOT NULL,
    email         VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,          -- BCrypt
    municipio_id  INT          NOT NULL REFERENCES municipios(id),
    telefono      VARCHAR(20),
    created_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- 5. productos (HU-02, HU-04, HU-09, HU-13)
CREATE TABLE productos (
    id                BIGSERIAL PRIMARY KEY,
    agricultor_id     BIGINT NOT NULL REFERENCES agricultores(id) ON DELETE CASCADE,
    categoria_id      INT    NOT NULL REFERENCES categorias(id),
    municipio_id      INT    NOT NULL REFERENCES municipios(id),
    nombre            VARCHAR(150)  NOT NULL,
    descripcion       TEXT,
    cantidad_kg       NUMERIC(10,2) NOT NULL CHECK (cantidad_kg >= 0),
    precio            NUMERIC(12,2) NOT NULL CHECK (precio > 0),
    fecha_recoleccion DATE          NOT NULL,
    foto_url          VARCHAR(255),
    estado            estado_producto NOT NULL DEFAULT 'DISPONIBLE',
    created_at        TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX idx_productos_categoria ON productos(categoria_id);
CREATE INDEX idx_productos_municipio ON productos(municipio_id);

-- 6. precios_regionales (HU-03)
CREATE TABLE precios_regionales (
    id            BIGSERIAL PRIMARY KEY,
    categoria_id  INT NOT NULL REFERENCES categorias(id),
    municipio_id  INT NOT NULL REFERENCES municipios(id),
    producto      VARCHAR(150)  NOT NULL,
    precio_kg     NUMERIC(12,2) NOT NULL CHECK (precio_kg > 0),
    fecha         DATE NOT NULL DEFAULT CURRENT_DATE,
    UNIQUE (producto, municipio_id, fecha)
);

-- 7. carritos (HU-11)
CREATE TABLE carritos (
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 8. carrito_items (HU-11)
CREATE TABLE carrito_items (
    id          BIGSERIAL PRIMARY KEY,
    carrito_id  BIGINT NOT NULL REFERENCES carritos(id)  ON DELETE CASCADE,
    producto_id BIGINT NOT NULL REFERENCES productos(id) ON DELETE CASCADE,
    cantidad_kg NUMERIC(10,2) NOT NULL CHECK (cantidad_kg > 0),
    UNIQUE (carrito_id, producto_id)
);

-- 9. pedidos (HU-10)
CREATE TABLE pedidos (
    id                BIGSERIAL PRIMARY KEY,
    usuario_id        BIGINT NOT NULL REFERENCES usuarios(id),
    municipio_entrega INT    NOT NULL REFERENCES municipios(id),
    direccion_entrega VARCHAR(255) NOT NULL,
    subtotal          NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
    costo_flete       NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (costo_flete >= 0),
    total             NUMERIC(12,2) NOT NULL CHECK (total >= 0),
    estado            estado_pedido NOT NULL DEFAULT 'PENDIENTE',
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 10. pedido_items (HU-09: al confirmar se descuenta cantidad_kg de productos)
CREATE TABLE pedido_items (
    id              BIGSERIAL PRIMARY KEY,
    pedido_id       BIGINT NOT NULL REFERENCES pedidos(id)   ON DELETE CASCADE,
    producto_id     BIGINT NOT NULL REFERENCES productos(id),
    cantidad_kg     NUMERIC(10,2) NOT NULL CHECK (cantidad_kg > 0),
    precio_unitario NUMERIC(12,2) NOT NULL CHECK (precio_unitario > 0)
);

-- 11. pagos (HU-06)
CREATE TABLE pagos (
    id          BIGSERIAL PRIMARY KEY,
    pedido_id   BIGINT NOT NULL REFERENCES pedidos(id),
    metodo      metodo_pago   NOT NULL,
    monto       NUMERIC(12,2) NOT NULL CHECK (monto > 0),
    estado      estado_pago   NOT NULL DEFAULT 'PENDIENTE',
    referencia  VARCHAR(100),
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 12. envios (HU-07)
CREATE TABLE envios (
    id              BIGSERIAL PRIMARY KEY,
    pedido_id       BIGINT NOT NULL UNIQUE REFERENCES pedidos(id) ON DELETE CASCADE,
    transportista   VARCHAR(150),
    tipo_vehiculo   VARCHAR(50),
    tipo_tarifa     VARCHAR(30) NOT NULL,         -- Strategy de fletes: POR_PESO, POR_DISTANCIA...
    costo           NUMERIC(12,2) NOT NULL CHECK (costo >= 0),
    fecha_estimada  DATE,
    fecha_entrega   DATE
);

-- 13. resenas (HU-08)
CREATE TABLE resenas (
    id            BIGSERIAL PRIMARY KEY,
    pedido_id     BIGINT   NOT NULL REFERENCES pedidos(id)      ON DELETE CASCADE,
    usuario_id    BIGINT   NOT NULL REFERENCES usuarios(id)     ON DELETE CASCADE,
    agricultor_id BIGINT   NOT NULL REFERENCES agricultores(id) ON DELETE CASCADE,
    calificacion  SMALLINT NOT NULL CHECK (calificacion BETWEEN 1 AND 5),
    comentario    TEXT,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (pedido_id, agricultor_id)
);

-- 14. mensajes (HU-05)
CREATE TABLE mensajes (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT NOT NULL REFERENCES usuarios(id)     ON DELETE CASCADE,
    agricultor_id BIGINT NOT NULL REFERENCES agricultores(id) ON DELETE CASCADE,
    producto_id   BIGINT REFERENCES productos(id) ON DELETE SET NULL,
    remitente     tipo_remitente NOT NULL,
    contenido     TEXT NOT NULL,
    leido         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 15. notificaciones (HU-14): el destinatario es un usuario o un agricultor
CREATE TABLE notificaciones (
    id            BIGSERIAL PRIMARY KEY,
    usuario_id    BIGINT REFERENCES usuarios(id)     ON DELETE CASCADE,
    agricultor_id BIGINT REFERENCES agricultores(id) ON DELETE CASCADE,
    pedido_id     BIGINT REFERENCES pedidos(id)      ON DELETE CASCADE,
    titulo        VARCHAR(150) NOT NULL,
    mensaje       TEXT NOT NULL,
    leida         BOOLEAN NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    CHECK ((usuario_id IS NOT NULL)::int + (agricultor_id IS NOT NULL)::int = 1)
);

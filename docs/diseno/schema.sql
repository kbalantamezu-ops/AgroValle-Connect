-- AgroValle Connect - Esquema de Staging (PostgreSQL / Neon)
-- Ejecutar sobre la base de datos agrovalle_staging.
--
-- ADVERTENCIA: este script elimina y recrea las tablas. No ejecutarlo sobre
-- una base de datos con informacion real. Su objetivo es dejar el ambiente de
-- staging reproducible para el laboratorio de Despliegue Continuo.

DROP TABLE IF EXISTS pagos CASCADE;
DROP TABLE IF EXISTS items_pedido CASCADE;
DROP TABLE IF EXISTS notificaciones CASCADE;
DROP TABLE IF EXISTS resenas CASCADE;
DROP TABLE IF EXISTS pedidos CASCADE;
DROP TABLE IF EXISTS productos CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS medios_pago CASCADE;
DROP TABLE IF EXISTS agricultores CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;

CREATE TABLE agricultores (
  id               BIGSERIAL PRIMARY KEY,
  nombre           VARCHAR(120) NOT NULL,
  cedula           VARCHAR(20)  NOT NULL UNIQUE,
  municipio        VARCHAR(30)  NOT NULL,
  telefono         VARCHAR(20),
  email            VARCHAR(120),
  finca            VARCHAR(120),
  fecha_registro   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE usuarios (
  id             BIGSERIAL PRIMARY KEY,
  nombre         VARCHAR(120) NOT NULL,
  email          VARCHAR(150) NOT NULL UNIQUE,
  password_hash  VARCHAR(255) NOT NULL,
  telefono       VARCHAR(20),
  rol            VARCHAR(20)  NOT NULL DEFAULT 'COMPRADOR',
  agricultor_id  BIGINT       UNIQUE
);

CREATE TABLE categorias (
  id      BIGSERIAL PRIMARY KEY,
  nombre  VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE productos (
  id                   BIGSERIAL PRIMARY KEY,
  nombre               VARCHAR(120)     NOT NULL,
  categoria            VARCHAR(30)      NOT NULL,
  cantidad_disponible  DOUBLE PRECISION NOT NULL,
  precio_unitario      NUMERIC(12, 2)   NOT NULL,
  unidad               VARCHAR(20)      NOT NULL,
  fecha_cosecha        DATE             NOT NULL,
  fecha_publicacion    TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  disponible           BOOLEAN          NOT NULL DEFAULT TRUE,
  agricultor_id        BIGINT           NOT NULL REFERENCES agricultores (id)
);

CREATE TABLE pedidos (
  id                   BIGSERIAL PRIMARY KEY,
  comprador_nombre     VARCHAR(120)   NOT NULL,
  comprador_telefono   VARCHAR(20),
  estado               VARCHAR(30)    NOT NULL,
  fecha_creacion       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  total                NUMERIC(12, 2) NOT NULL,
  flete_costo          NUMERIC(12, 2),
  flete_zona           VARCHAR(30),
  municipio_destino    VARCHAR(30),
  usuario_id           BIGINT
);

CREATE TABLE items_pedido (
  id               BIGSERIAL PRIMARY KEY,
  producto_id      BIGINT           NOT NULL REFERENCES productos (id),
  pedido_id        BIGINT           NOT NULL REFERENCES pedidos (id),
  cantidad         DOUBLE PRECISION NOT NULL,
  precio_unitario  NUMERIC(12, 2)   NOT NULL,
  subtotal         NUMERIC(12, 2)   NOT NULL
);

CREATE TABLE medios_pago (
  id      BIGSERIAL PRIMARY KEY,
  nombre  VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE pagos (
  id              BIGSERIAL PRIMARY KEY,
  pedido_id       BIGINT           NOT NULL UNIQUE REFERENCES pedidos (id),
  medio_pago_id   BIGINT           NOT NULL REFERENCES medios_pago (id),
  monto           NUMERIC(12, 2)   NOT NULL,
  estado          VARCHAR(20)      NOT NULL,
  fecha_pago      TIMESTAMP
);

CREATE TABLE notificaciones (
  id               BIGSERIAL PRIMARY KEY,
  pedido_id        BIGINT       NOT NULL,
  mensaje          VARCHAR(255) NOT NULL,
  fecha_creacion   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE resenas (
  id               BIGSERIAL PRIMARY KEY,
  agricultor_id    BIGINT       NOT NULL REFERENCES agricultores (id),
  calificacion     INTEGER      NOT NULL,
  comentario       VARCHAR(500),
  autor_nombre     VARCHAR(120) NOT NULL,
  fecha_creacion   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_productos_agricultor ON productos (agricultor_id);
CREATE INDEX idx_items_pedido_pedido ON items_pedido (pedido_id);
CREATE INDEX idx_items_pedido_producto ON items_pedido (producto_id);
CREATE INDEX idx_pedidos_usuario ON pedidos (usuario_id);
CREATE INDEX idx_notificaciones_pedido ON notificaciones (pedido_id);
CREATE INDEX idx_resenas_agricultor ON resenas (agricultor_id);

INSERT INTO medios_pago (nombre) VALUES
  ('EFECTIVO'),
  ('TRANSFERENCIA'),
  ('TARJETA')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO categorias (nombre) VALUES
  ('Frutas'),
  ('Verduras'),
  ('Granos'),
  ('Café'),
  ('Tubérculos'),
  ('Lácteos'),
  ('Otros')
ON CONFLICT (nombre) DO NOTHING;

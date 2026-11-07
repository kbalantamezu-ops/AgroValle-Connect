-- Referencia del Sprint 1 (HU-01, HU-02, HU-12). Compararla con V1__crear_esquema.sql del equipo.
CREATE TABLE agricultores (
  id               BIGSERIAL PRIMARY KEY,
  nombre           VARCHAR(120) NOT NULL,
  ubicacion_valle  VARCHAR(120) NOT NULL,
  cedula           VARCHAR(10)  NOT NULL UNIQUE,
  email            VARCHAR(150) NOT NULL UNIQUE,
  password_hash    VARCHAR(255) NOT NULL
);

CREATE TABLE usuarios (
  id             BIGSERIAL PRIMARY KEY,
  nombre         VARCHAR(120) NOT NULL,
  email          VARCHAR(150) NOT NULL UNIQUE,
  password_hash  VARCHAR(255) NOT NULL,
  telefono       VARCHAR(20)
);

CREATE TABLE categorias (
  id      BIGSERIAL PRIMARY KEY,
  nombre  VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE productos (
  id                   BIGSERIAL PRIMARY KEY,
  agricultor_id        BIGINT NOT NULL REFERENCES agricultores (id),
  categoria_id         BIGINT NOT NULL REFERENCES categorias (id),
  nombre               VARCHAR(120) NOT NULL,
  descripcion          VARCHAR(500),
  precio               NUMERIC(12, 2) NOT NULL CHECK (precio > 0),
  cantidad_disponible  INTEGER NOT NULL CHECK (cantidad_disponible >= 1),
  unidad               VARCHAR(30) NOT NULL,
  foto_url             VARCHAR(500),
  fecha_publicacion    TIMESTAMP NOT NULL
);

INSERT INTO categorias (nombre) VALUES ('Frutas'), ('Verduras'), ('Granos'), ('Tubérculos'), ('Lácteos');

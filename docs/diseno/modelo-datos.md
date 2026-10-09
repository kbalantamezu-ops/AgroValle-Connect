# Modelo de Datos y Persistencia: AgroValle Connect

Artefacto del Sprint de Diseño:

## 1. Modelo Entidad-Relación (MER)

## 2. Diagrama Entidad-Relación (DER en 3FN)

## 3. Justificación de la Tercera Forma Normal (3FN)
 
- **1FN:** todos los atributos son atómicos y cada tabla tiene llave primaria. No hay grupos repetitivos (los productos de un pedido van en `detalle_pedido`, no en columnas `producto1`, `producto2`).
- **2FN:** todas las tablas usan llave primaria simple (`id`), por lo que ningún atributo depende de solo una parte de la llave.
- **3FN:** no hay dependencias transitivas. El nombre del municipio y el de la categoría viven en sus propias tablas y se referencian por FK, en lugar de repetir texto como `ubicacion_valle = 'Dagua'` en cada productor.
- **Decisión de diseño:** `detalle_pedido.precio_unitario` guarda una copia del precio al momento de la compra. No es redundancia que viole la 3FN, sino un dato histórico: si el productor cambia el precio mañana, el pedido antiguo no debe cambiar.

## 4. Script DDL (PostgreSQL 15/16)

## 5. Mapeo a Entidades JPA (Spring Boot 3, Java 17)

## 6. Correspondencia con el DTO de la HU-01


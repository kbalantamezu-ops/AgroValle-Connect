# Modelo de Datos y Persistencia: AgroValle Connect

Artefacto del Sprint de Diseño. Ruta sugerida en el repositorio: `docs/diseno/modelo-datos.md`.

## 1. Modelo Entidad-Relación (MER)

### Entidades

| Entidad | Descripción | Sprint |
|---|---|---|
| `municipios` | Municipios del Valle del Cauca donde se ubican productores y comerciantes | 1 |
| `categorias` | Categorías de producto (Frutas, Verduras, Tubérculos, etc.) | 1 |
| `usuarios_productores` | Agricultores registrados en la plataforma | 1 |
| `productos` | Productos agrícolas publicados por un productor | 1 |
| `comerciantes` | Comerciantes urbanos y restaurantes compradores | 2 |
| `pedidos` | Pedido realizado por un comerciante | 2 |
| `detalle_pedido` | Líneas de un pedido (producto, cantidad, precio) | 2 |

### Relaciones y cardinalidades

| Relación | Cardinalidad | Regla de negocio |
|---|---|---|
| Municipio ubica a Productor | 1 : N | Un productor pertenece a un solo municipio; un municipio tiene muchos productores |
| Productor publica Producto | 1 : N | Un producto pertenece a un solo productor |
| Categoría clasifica Producto | 1 : N | Un producto tiene una sola categoría |
| Municipio ubica a Comerciante | 1 : N | Un comerciante pertenece a un solo municipio |
| Comerciante realiza Pedido | 1 : N | Un pedido pertenece a un solo comerciante |
| Pedido contiene Producto | N : M | Se resuelve con la entidad asociativa `detalle_pedido` |

## 2. Diagrama Entidad-Relación (DER en 3FN)
imagen.png

## 3. Justificación de la Tercera Forma Normal (3FN)

- **1FN:** todos los atributos son atómicos y cada tabla tiene llave primaria. No hay grupos repetitivos (los productos de un pedido van en `detalle_pedido`, no en columnas `producto1`, `producto2`).
- **2FN:** todas las tablas usan llave primaria simple (`id`), por lo que ningún atributo depende de solo una parte de la llave.
- **3FN:** no hay dependencias transitivas. El nombre del municipio y el de la categoría viven en sus propias tablas y se referencian por FK, en lugar de repetir texto como `ubicacion_valle = 'Dagua'` en cada productor.
- **Decisión de diseño:** `detalle_pedido.precio_unitario` guarda una copia del precio al momento de la compra. No es redundancia que viole la 3FN, sino un dato histórico: si el productor cambia el precio mañana, el pedido antiguo no debe cambiar.

# Sprint Review — Sprint 2

## Sprint Goal

Permitir que compradores y productores encuentren la oferta agrícola del Valle del Cauca,
filtrando el catálogo por categoría y municipio y buscando productos por disponibilidad.

## Incremento Ejecutable

El incremento incluye el catálogo web completo, la búsqueda y los filtros de HU-04 y HU-13,
los precios promedio regionales, el carrito, la creación de pedidos con descuento de stock,
las reseñas, las notificaciones y la plataforma web que consume la API REST.

## Historias Completadas

| ID | Historia | Story Points | Estado |
| --- | --- | --- | --- |
| HU-03 | Visualización de Precios Regionales | 5 | Completado |
| HU-04 | Filtro de Categorías y Municipio | 5 | Completado |
| HU-05 | Contacto Directo | 3 | Completado |
| HU-06 | Medios de Pago | 8 | Completado |
| HU-08 | Satisfacción y Reseñas | 3 | Completado |
| HU-09 | Actualización de Stock | 5 | Completado |
| HU-10 | Consultar Estado de un Pedido | 3 | Completado |
| HU-11 | Agregar Productos al Carrito | 5 | Completado |
| HU-13 | Búsqueda Inteligente | 5 | Completado |
| HU-14 | Notificaciones de Cambio de Estado | 5 | Completado |
| HU-15 | Panel de Gestión del Agricultor | 8 | Completado |

## Evidencias de Calidad

```text
./mvnw clean verify
BUILD SUCCESS
Tests run: 73, Failures: 0, Errors: 0
Checkstyle: 0 violaciones
Cobertura JaCoCo: superior al 60%
```

- Pipeline de GitHub Actions en verde.
- Pruebas de integración con MockMvc para los controladores.
- Pruebas unitarias para servicios y estrategias de flete.
- Aplicación web verificada en `http://localhost:8080`.

## Demostración

1. Abrir `/` y filtrar por municipio, categoría y disponibilidad.
2. Consultar los precios promedio regionales.
3. Agregar productos al carrito y confirmar un pedido.
4. Consultar el estado y las notificaciones en `/pedidos.html`.
5. Publicar un agricultor y una cosecha en `/publicar.html`.
6. Consultar publicaciones, pedidos y ventas en `/panel.html`.

## Conclusión

El Sprint 2 entregó un incremento funcional de extremo a extremo: API REST, persistencia,
reglas de negocio, interfaz web y pruebas automatizadas.

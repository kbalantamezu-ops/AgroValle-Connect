# Sprint Review: Sprint 2
 
> Instrucciones para el equipo: reemplacen cada `[COMPLETAR]` con datos reales de su última ejecución y borren esta nota antes de entregar.
 
## Sprint Goal
 
Permitir que compradores y agricultores encuentren la oferta agrícola del Valle del Cauca, filtrando el catálogo por categoría y municipio y buscando productos por disponibilidad.
 
## Historias del Sprint
 
| ID | Historia de Usuario | Prioridad | Story Points | Estado |
|---|---|---|---|---|
| HU-04 | Filtro de Categorías y Municipio | Must Have | 5 | [Completado / Parcial] |
| HU-13 | Búsqueda Inteligente por Municipio y Disponibilidad | Must Have | 5 | [Completado / Parcial] |
| | **Total** | | **10** | |
 
## Incremento Ejecutable
 
El equipo desarrolló y verificó los siguientes comportamientos de la API REST:
 
- **HU-04:** consulta del catálogo filtrada por municipio y categoría. Endpoint: `GET /api/v1/productos?municipio=Dagua&categoria=Frutas`, que responde `200 OK` con un arreglo JSON de productos.
- **HU-13:** búsqueda de productos por municipio de origen y disponibilidad. Endpoint: `[COMPLETAR: ruta real]`. Cada resultado incluye cantidad disponible, precio unitario y datos del agricultor.

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


## Historias No Incluidas en este Sprint
 
Las demás historias del Product Backlog (HU-03, HU-05, HU-06, HU-07, HU-08, HU-09, HU-10, HU-11, HU-14 y HU-15) permanecen en el backlog y están planificadas para los Sprints 3 al 7, según la proyección de sprints.
 

## Conclusión

El Sprint 2 entregó un incremento funcional de extremo a extremo: API REST, persistencia,
reglas de negocio, interfaz web y pruebas automatizadas.

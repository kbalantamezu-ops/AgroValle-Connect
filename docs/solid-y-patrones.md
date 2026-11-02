# SOLID y patrones de diseño en AgroValle Connect

Aquí se muestra dónde se aplican los cinco principios SOLID y el patrón GoF Strategy en el código real.
Las tarifas de flete son valores de ejemplo.

## Los cinco principios SOLID

**S – Responsabilidad única.** Cada clase hace una sola cosa. `AgricultorService` solo registra agricultores,
`AgricultorMapper` solo convierte entre entidad y DTO, y `GlobalExceptionHandler` solo traduce errores a HTTP.

**O – Abierto/cerrado.** Para agregar una zona de flete nueva se crea otra clase que implemente `EstrategiaFlete`
y se marca con `@Component`. `FleteService` no se modifica.

**L – Sustitución de Liskov.** `FleteZonaCercanaStrategy`, `FleteZonaIntermediaStrategy` y `FleteZonaLejanaStrategy`
se pueden usar en lugar de `EstrategiaFlete` sin que `FleteService` note la diferencia. Todas respetan el mismo contrato.

**I – Segregación de interfaces.** `EstrategiaFlete` es pequeña (`zona`, `aplicaA`, `calcular`) y los repositorios
(`AgricultorRepository`, `UsuarioRepository`, `ProductoRepository`) son interfaces separadas. Ninguna clase depende de métodos que no usa.

**D – Inversión de dependencias.** Los servicios dependen de abstracciones (`PasswordEncoder`, los repositorios y
`EstrategiaFlete`) y Spring se las entrega por el constructor. Por eso en las pruebas se pueden reemplazar con Mockito.

## Patrón de comportamiento: Strategy (HU-07)

El costo del flete cambia según el municipio. En vez de un `if/else` gigante, cada zona es una estrategia:

| Zona | Clase | Tarifa base | Por kilo |
|------|-------|-------------|----------|
| Cercana (Cali, Yumbo, Palmira, Jamundí, Candelaria) | `FleteZonaCercanaStrategy` | 5.000 | 300 |
| Intermedia (Tuluá, Buga, Cartago, Sevilla, Roldanillo) | `FleteZonaIntermediaStrategy` | 12.000 | 500 |
| Lejana (cualquier otro municipio) | `FleteZonaLejanaStrategy` | 20.000 | 800 |

`FleteService` recibe la lista de estrategias ordenadas con `@Order` y usa la primera que aplique al municipio.
La zona lejana va de última y aplica siempre, así que siempre hay una tarifa.

Evidencia en pruebas: `FleteEstrategiasTest` (cada estrategia), `FleteServiceTest` (con `@Mock`) y `FleteControllerTest` (MockMvc).

## Otros patrones usados

- **Repository:** `AgricultorRepository`, `UsuarioRepository`, `ProductoRepository`, `CategoriaRepository`.
- **Inversión de control (IoC):** Spring crea e inyecta los objetos.
- **DTO:** los `*Request` y `*Response` (records), para no exponer las entidades.
- **Mapper / Builder:** `AgricultorMapper`, `UsuarioMapper`, `ProductoMapper` y `@Builder` de Lombok en las entidades.

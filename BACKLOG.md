| ID | Historia de Usuario | MoSCoW | Story Points |
| --- | --- | --- | --- |
| HU-01 | Registro de Agricultores | M | 3 |
| HU-02 | Publicación de Productos | M | 5 |
| HU-03 | Visualización de Precios Regionales | S | 5 |
| HU-04 | Filtro de Categorías y Municipio | M | 5 |
| HU-05 | Contacto Directo | M | 3 |
| HU-06 | Medios de Pago | S | 8 |
| HU-07 | Transporte | S | 8 |
| HU-08 | Satisfacción y Reseñas | C | 3 |
| HU-09 | Actualización de Stock tras Confirmación de Pedido | M | 5 |
| HU-10 | Consultar el Estado de un Pedido | M | 3 |
| HU-11 | Agregar Productos al Carrito | M | 5 |
| HU-12 | Registro de Usuario | S | 3 |
| HU-13 | Búsqueda Inteligente por Municipio y Disponibilidad | M | 5 |
| HU-14 | Notificaciones de Cambio de Estado del Pedido | S | 5 |
| HU-15 | Panel de Gestión para el Agricultor | C | 8 |

**Total: 15 Historias de Usuario — 74 Story Points**

---

## Detalle de cada Historia de Usuario

### HU-01: Registro de Agricultores
**Historia:** Como Agricultor, quiero registrarme en la plataforma para ofrecer mis productos.
**Priorización:** M (Must Have) — **Estimación:** 3 Points

**Escenario BDD:**
- **Given** que el usuario ingresa a `/api/v1/auth/register`.
- **When** envía un JSON con `nombre`, `ubicacion_valle` y `cedula` válida.
- **Then** el sistema responde con status `201 Created` y el registro persiste en la base de datos PostgreSQL.

---

### HU-02: Publicación de Productos
**Historia:** Como Agricultor, quiero publicar mis cosechas para que sean visibles.
**Priorización:** M (Must Have) — **Estimación:** 5 Points

**Escenario BDD:**
- **Given** un agricultor autenticado con token JWT.
- **When** publica un producto con `tipo`, `cantidad` y `fecha_cosecha`.
- **Then** el sistema valida que la fecha no sea anterior a hoy y retorna un ID de producto único.

---

### HU-03: Visualización de Precios Regionales
**Historia:** Como Usuario, quiero ver los precios promedio del Valle para negociar mejor.
**Priorización:** S (Should Have) — **Estimación:** 5 Points

**Escenario BDD:**
- **Given** que existen 50 transacciones de "Café" en las últimas 24 horas.
- **When** solicito el precio promedio de "Café".
- **Then** el sistema calcula la media aritmética y despliega el valor exacto en pesos colombianos.

---

### HU-04: Filtro de Categorías y Municipio
**Historia:** Como Usuario, quiero comparar precios, calidad y disponibilidad de productos existentes en diferentes municipios.
**Priorización:** M (Must Have) — **Estimación:** 5 Points

**Escenario BDD:**
- **Given** que los productores están distribuidos en distintos municipios del Valle y no cuentan con un canal común donde comparar precios entre zonas ni contactar directamente a los compradores interesados.
- **When** esto los obliga a depender de terceros o de referencias informales para saber cuánto vale su producto y con quién negociar.
- **Then** el sistema centraliza esa información (precios, categorías, municipios) y habilita el contacto directo comprador-vendedor.

---

### HU-05: Contacto Directo
**Historia:** Como Usuario, quiero contactarme con el proveedor para realizar mi compra de manera exitosa. Como Agricultor, debo dar disponibilidad a los diferentes medios de contacto para que los clientes puedan acceder a cualquier información sobre la venta de los productos.
**Priorización:** M (Must Have) — **Estimación:** 3 Points

**Escenario BDD:**
- **Given** que el Agricultor tiene definidos sus medios de contacto y está disponible.
- **When** el usuario selecciona el contacto del agricultor al que desea contactar para la negociación del producto y envía su respectivo mensaje.
- **Then** el usuario logra contactar al agricultor de manera exitosa para los fines de la negociación.

---

### HU-06: Medios de Pago
**Historia:** Como Usuario, quiero poder acceder a diferentes medios de pago.
**Priorización:** S (Should Have) — **Estimación:** 8 Points

**Escenario BDD:**
- **Given** que el sistema tiene habilitados distintos medios de pago (efectivo, transferencia, tarjeta, etc.).
- **When** el comprador selecciona el medio de pago de su preferencia al confirmar la compra.
- **Then** el sistema procesa el pago de forma segura y notifica al comprador y al agricultor la confirmación de la transacción.

---

### HU-07: Transporte
**Historia:** Como Usuario, quiero seleccionar un medio de transporte para el envío del producto que adquiero, con el fin de recibirlo de manera segura y oportuna.
**Priorización:** S (Should Have) — **Estimación:** 8 Points

**Escenario BDD:**
- **Given** que un agricultor autenticado tiene un lote de cosecha listo para despachar y confirma el alistamiento del pedido.
- **When** el agricultor programa la ruta de despacho seleccionando el municipio de origen, el destino y el vehículo disponible.
- **Then** el sistema registra la programación logística, genera un seguimiento en tiempo real del despacho y notifica al comprador con la hora estimada de entrega.

---

### HU-08: Satisfacción y Reseñas
**Historia:** Como Usuario, quiero poder expresar mi grado de satisfacción después de mi experiencia con el proceso de compra.
**Priorización:** C (Could Have) — **Estimación:** 3 Points

**Escenario BDD:**
- **Given** que un comprador ha recibido un pedido completado exitosamente.
- **When** el comprador califica la calidad del producto, la puntualidad de la entrega y deja una reseña opcional.
- **Then** el sistema registra la calificación y reseña, y la muestra en el perfil del productor para que otros usuarios la consulten.

---

### HU-09: Actualización de Stock tras Confirmación de Pedido
**Historia:** Como Sistema, quiero descontar automáticamente el stock disponible cuando se confirma un pedido, para evitar la sobreventa de un mismo lote.
**Priorización:** M (Must Have) — **Estimación:** 5 Points

**Escenario BDD:**
- **Given** un producto con una cantidad disponible registrada en PostgreSQL.
- **When** un comprador confirma un pedido por una cantidad específica mediante `POST /api/v1/pedidos`.
- **Then** el sistema descuenta esa cantidad del stock disponible y retorna la confirmación del pedido con status `201 Created`.

---

### HU-10: Consultar el Estado de un Pedido
**Historia:** Como Usuario, quiero ver el estado de mi pedido y que se me notifique cuando haya una actualización.
**Priorización:** M (Must Have) — **Estimación:** 3 Points

**Escenario BDD:**
- **Given** un usuario autenticado con al menos un pedido activo.
- **When** consulto mis pedidos.
- **Then** el sistema muestra si el pedido está pendiente, en preparación, enviado o entregado.

---

### HU-11: Agregar Productos al Carrito
**Historia:** Como Usuario, quiero agregar productos al carrito de compras para consolidar mi pedido y realizar la compra de manera directa.
**Priorización:** M (Must Have) — **Estimación:** 5 Points

**Escenario BDD:**
- **Given** que un usuario ha encontrado un producto disponible en el catálogo.
- **When** el usuario selecciona el producto, indica la cantidad deseada y lo agrega al carrito de compras.
- **Then** el sistema valida que la cantidad esté disponible, agrega el producto al carrito y muestra un subtotal actualizado en pesos colombianos.

---

### HU-12: Registro de Usuario
**Historia:** Como Usuario, quiero registrarme a la plataforma de ventas.
**Priorización:** S (Should Have) — **Estimación:** 3 Points

**Escenario BDD:**
- **Given** que un usuario quiera registrarse e iniciar sesion.
- **When** el usuario ingresa sus datos como nombre, ubicación del Valle, contraseña, etc.
- **Then** el sistema valida los datos, crea el perfil en la base de datos y confirma el registro.

---

### HU-13: Búsqueda Inteligente por Municipio y Disponibilidad
**Historia:** Como Usuario, quiero buscar productos por municipio de origen y disponibilidad para encontrar rápidamente la oferta que necesito.
**Priorización:** M (Must Have) — **Estimación:** 5 Points

**Escenario BDD:**
- **Given** que el catálogo contiene productos publicados por agricultores de distintos municipios del Valle.
- **When** el usuario ingresa un término de búsqueda y aplica filtros de municipio y disponibilidad.
- **Then** el sistema devuelve los resultados filtrados con cantidad, precio unitario y datos del productor.

---

### HU-14: Notificaciones de Cambio de Estado del Pedido
**Historia:** Como Usuario, quiero recibir notificaciones cuando cambie el estado de mi pedido para estar informado sin tener que consultar constantemente.
**Priorización:** S (Should Have) — **Estimación:** 5 Points

**Escenario BDD:**
- **Given** que un usuario tiene un pedido activo en el sistema.
- **When** el estado del pedido cambia (por ejemplo, de "en preparación" a "enviado").
- **Then** el sistema genera una notificación automática y la envía al usuario.

---

### HU-15: Panel de Gestión para el Agricultor
**Historia:** Como Agricultor, quiero consultar un panel con mis publicaciones, pedidos y ventas para administrar mi actividad en un solo lugar.
**Priorización:** C (Could Have) — **Estimación:** 8 Points

**Escenario BDD:**
- **Given** que un agricultor ha publicado productos y recibido pedidos.
- **When** el agricultor accede a su panel de gestión.
- **Then** el sistema muestra un resumen de sus publicaciones activas, pedidos recibidos y ventas realizadas.

---




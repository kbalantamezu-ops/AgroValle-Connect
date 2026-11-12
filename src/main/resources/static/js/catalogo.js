import { api } from './api.js';
import { carrito } from './carrito.js';
import { sesion } from './sesion.js';
import { escapeHtml, etiquetaDe, fecha, fechaHora, moneda, mostrarToast } from './ui.js';

let municipios = [];
let categorias = [];
let mediosPago = [];
let productosActuales = [];
let productoEnDetalle = null;

const $ = (selector) => document.querySelector(selector);

function etiquetaMunicipio(nombre) {
    return etiquetaDe(municipios, nombre);
}

function etiquetaCategoria(nombre) {
    return etiquetaDe(categorias, nombre);
}

async function cargarCatalogo() {
    [municipios, categorias, mediosPago] = await Promise.all([
        api.get('/catalogo/municipios'),
        api.get('/catalogo/categorias'),
        api.get('/medios-pago'),
    ]);
    const selectMunicipio = $('#filtroMunicipio');
    const selectCategoria = $('#filtroCategoria');
    municipios.forEach((m) => {
        selectMunicipio.add(new Option(m.etiqueta, m.nombre));
    });
    categorias.forEach((c) => {
        selectCategoria.add(new Option(c.etiqueta, c.nombre));
    });
}

async function cargarPrecios() {
    const grid = $('#gridPrecios');
    const prioritarias = ['CAFE', 'FRUTAS', 'VERDURAS', 'GRANOS', 'TUBERCULOS'];
    grid.innerHTML = '';
    for (const categoria of prioritarias) {
        try {
            const precio = await api.get(`/precios/promedio?categoria=${categoria}`);
            const tarjeta = document.createElement('div');
            tarjeta.className = 'tarjeta-precio';
            tarjeta.innerHTML = `
                <div class="nombre">${escapeHtml(etiquetaCategoria(categoria))}</div>
                <div class="valor">${moneda(precio.promedio)} <small>/ kg</small></div>
                <div class="detalle">Promedio de ${precio.cantidadProductos} publicación(es)</div>`;
            grid.appendChild(tarjeta);
        } catch {
            const tarjeta = document.createElement('div');
            tarjeta.className = 'tarjeta-precio';
            tarjeta.innerHTML = `
                <div class="nombre">${escapeHtml(etiquetaCategoria(categoria))}</div>
                <div class="detalle">Aún no hay publicaciones en esta categoría.</div>`;
            grid.appendChild(tarjeta);
        }
    }
}

async function cargarProductos() {
    const grid = $('#gridProductos');
    grid.innerHTML = '<div class="cargando">Cargando productos...</div>';
    const parametros = new URLSearchParams();
    const q = $('#busqueda').value.trim();
    const municipio = $('#filtroMunicipio').value;
    const categoria = $('#filtroCategoria').value;
    const disponible = $('#filtroDisponible').checked;
    if (q) {
        parametros.set('q', q);
    }
    if (municipio) {
        parametros.set('municipio', municipio);
    }
    if (categoria) {
        parametros.set('categoria', categoria);
    }
    parametros.set('disponible', disponible ? 'true' : 'false');

    try {
        productosActuales = await api.get(`/productos?${parametros}`);
        renderProductos(productosActuales);
    } catch (error) {
        grid.innerHTML = '';
        mostrarToast(error.message, 'error');
    }
}

function renderProductos(productos) {
    const grid = $('#gridProductos');
    grid.innerHTML = '';
    if (productos.length === 0) {
        grid.innerHTML = '<div class="vacio">No hay productos que coincidan con tu búsqueda.</div>';
        return;
    }
    productos.forEach((producto) => {
        const tarjeta = document.createElement('article');
        tarjeta.className = `tarjeta ${producto.disponible ? '' : 'agotado'}`;
        tarjeta.innerHTML = `
            <span class="categoria">${escapeHtml(etiquetaCategoria(producto.categoria))}</span>
            <h3>${escapeHtml(producto.nombre)}</h3>
            <div class="municipio">${escapeHtml(etiquetaMunicipio(producto.municipio))}
                · ${escapeHtml(producto.agricultor.nombre)}</div>
            <div class="precio">${moneda(producto.precioUnitario)}
                <small>/ ${escapeHtml(producto.unidad)}</small></div>
            <div class="stock">Disponible: ${producto.cantidadDisponible} ${escapeHtml(producto.unidad)}
                · Cosecha: ${fecha(producto.fechaCosecha)}</div>
            ${producto.disponible ? '' : '<span class="badge-agotado">Agotado</span>'}
            <div class="acciones">
                <button class="btn btn-secundario" type="button" data-ver="${producto.id}">Ver detalles</button>
                <button class="btn btn-primario" type="button" data-agregar="${producto.id}"
                        ${producto.disponible ? '' : 'disabled'}>Agregar</button>
            </div>`;
        grid.appendChild(tarjeta);
    });
}

function agregarAlCarrito(productoId) {
    const producto = productosActuales.find((p) => p.id === Number(productoId));
    if (!producto) {
        return;
    }
    carrito.agregar(producto, 1);
    mostrarToast(`${producto.nombre} agregado al carrito`);
    actualizarCarrito();
}

async function abrirDetalle(productoId) {
    try {
        const producto = await api.get(`/productos/${productoId}`);
        productoEnDetalle = producto;
        const [resenas] = await Promise.all([
            api.get(`/resenas?agricultorId=${producto.agricultor.id}`),
        ]);
        $('#contenidoDetalle').innerHTML = detalleHtml(producto, resenas);
        $('#overlayDetalle').classList.add('abierto');
        $('#formResena')?.addEventListener('submit', enviarResena);
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

function detalleHtml(producto, resenas) {
    const agricultor = producto.agricultor;
    const contacto = [];
    if (agricultor.telefono) {
        contacto.push(`<a href="tel:${escapeHtml(agricultor.telefono)}">${escapeHtml(agricultor.telefono)}</a>`);
    }
    if (agricultor.email) {
        contacto.push(`<a href="mailto:${escapeHtml(agricultor.email)}">${escapeHtml(agricultor.email)}</a>`);
    }
    const listaResenas = resenas.map((r) => `
        <div class="resena">
            <span class="estrellas">${'★'.repeat(r.calificacion)}${'☆'.repeat(5 - r.calificacion)}</span>
            <strong>${escapeHtml(r.autorNombre)}</strong>
            <span style="color: var(--gris);">· ${fechaHora(r.fechaCreacion)}</span>
            ${r.comentario ? `<p>${escapeHtml(r.comentario)}</p>` : ''}
        </div>`).join('');

    const usuario = sesion.usuario();
    const formularioResena = usuario ? `
            <form class="form-resena" id="formResena">
                <div class="campo">
                    <label for="rAutor">Tu nombre</label>
                    <input type="text" id="rAutor" required maxlength="120" value="${escapeHtml(usuario.nombre)}">
                </div>
                <div class="campo">
                    <label for="rCalificacion">Calificación (1 a 5)</label>
                    <select id="rCalificacion" required>
                        <option value="5">5 - Excelente</option>
                        <option value="4">4 - Muy bueno</option>
                        <option value="3">3 - Bueno</option>
                        <option value="2">2 - Regular</option>
                        <option value="1">1 - Malo</option>
                    </select>
                </div>
                <div class="campo">
                    <label for="rComentario">Comentario (opcional)</label>
                    <textarea id="rComentario" rows="2" maxlength="500"></textarea>
                </div>
                <button class="btn btn-acento" type="submit">Enviar reseña</button>
            </form>`
        : '<p class="ayuda"><a href="/login.html?volver=/">Inicia sesión</a> para dejar una reseña a este productor.</p>';

    return `
        <h2>${escapeHtml(producto.nombre)}</h2>
        <div class="detalle-grid">
            <div>
                <div class="dato"><strong>Categoría:</strong>
                    ${escapeHtml(etiquetaCategoria(producto.categoria))}</div>
                <div class="dato"><strong>Municipio:</strong>
                    ${escapeHtml(etiquetaMunicipio(producto.municipio))}</div>
                <div class="dato"><strong>Precio:</strong>
                    ${moneda(producto.precioUnitario)} / ${escapeHtml(producto.unidad)}</div>
                <div class="dato"><strong>Disponible:</strong>
                    ${producto.cantidadDisponible} ${escapeHtml(producto.unidad)}</div>
                <div class="dato"><strong>Fecha de cosecha:</strong>
                    ${fecha(producto.fechaCosecha)}</div>
                <div class="dato"><strong>Publicado:</strong>
                    ${fechaHora(producto.fechaPublicacion)}</div>
                ${producto.disponible ? `
                    <button class="btn btn-primario" type="button" id="btnAgregarDetalle">Agregar al carrito</button>`
                    : '<span class="badge-agotado">Agotado</span>'}
            </div>
            <div class="tarjeta-agricultor">
                <h4>${escapeHtml(agricultor.nombre)}</h4>
                <p>${agricultor.finca ? escapeHtml(agricultor.finca) : 'Finca sin registrar'}
                    · ${escapeHtml(etiquetaMunicipio(producto.municipio))}</p>
                <div class="contacto">
                    ${contacto.length ? contacto.join('') : '<span>Sin medios de contacto registrados</span>'}
                </div>
            </div>
        </div>
        <div class="resenas">
            <h3 style="color: var(--verde-800);">Reseñas del productor</h3>
            ${listaResenas || '<p style="color: var(--gris);">Aún no hay reseñas para este productor.</p>'}
            ${formularioResena}
        </div>`;
}

async function enviarResena(evento) {
    evento.preventDefault();
    if (!productoEnDetalle) {
        return;
    }
    const usuario = sesion.usuario();
    if (!usuario) {
        mostrarToast('Inicia sesión para dejar una reseña', 'error');
        return;
    }
    const cuerpo = {
        agricultorId: productoEnDetalle.agricultor.id,
        calificacion: Number($('#rCalificacion').value),
        comentario: $('#rComentario').value.trim() || null,
        autorNombre: $('#rAutor').value.trim() || usuario.nombre,
    };
    try {
        await api.post('/resenas', cuerpo);
        mostrarToast('Reseña registrada. ¡Gracias por tu calificación!');
        $('#overlayDetalle').classList.remove('abierto');
        await abrirDetalle(productoEnDetalle.id);
        $('#overlayDetalle').classList.add('abierto');
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

function actualizarCarrito() {
    const items = carrito.items();
    const contador = $('#contadorCarrito');
    contador.textContent = carrito.contar();
    $('#totalCarrito').textContent = moneda(carrito.total());
    $('#btnFinalizarPedido').disabled = items.length === 0;
    renderItemsCarrito(items);
}

function renderItemsCarrito(items) {
    const contenedor = $('#itemsCarrito');
    contenedor.innerHTML = '';
    if (items.length === 0) {
        contenedor.innerHTML = '<p style="color: var(--gris);">Tu carrito está vacío.</p>';
        return;
    }
    items.forEach((item) => {
        const fila = document.createElement('div');
        fila.className = 'item-carrito';
        fila.innerHTML = `
            <div class="info">
                <div class="nombre">${escapeHtml(item.nombre)}</div>
                <div>${escapeHtml(etiquetaMunicipio(item.municipio))} ·
                    ${moneda(item.precioUnitario)} / ${escapeHtml(item.unidad)}</div>
                <div>Subtotal: ${moneda(item.precioUnitario * item.cantidad)}</div>
            </div>
            <div class="cantidad">
                <button type="button" data-menos="${item.productoId}">-</button>
                <span>${item.cantidad}</span>
                <button type="button" data-mas="${item.productoId}">+</button>
            </div>
            <button class="quitar" type="button" data-quitar="${item.productoId}">Quitar</button>`;
        contenedor.appendChild(fila);
    });
}

function abrirCheckout() {
    const items = carrito.items();
    if (items.length === 0) {
        return;
    }
    const usuario = sesion.usuario();
    if (!usuario) {
        cerrarCarrito();
        $('#contenidoCheckout').innerHTML = `
            <h2>Finalizar pedido</h2>
            <p class="ayuda" style="margin:0.8rem 0;">
                Para confirmar tu pedido necesitas iniciar sesión con una cuenta.
            </p>
            <a class="btn btn-primario" href="/login.html?volver=/">Iniciar sesión o registrarme</a>
            <p class="ayuda" style="margin-top:1rem;">Tu carrito se conserva mientras te registras.</p>`;
        $('#overlayCheckout').classList.add('abierto');
        return;
    }
    const resumen = items.map((item) => `
        <tr>
            <td>${escapeHtml(item.nombre)}</td>
            <td>${item.cantidad} ${escapeHtml(item.unidad)}</td>
            <td>${moneda(item.precioUnitario * item.cantidad)}</td>
        </tr>`).join('');
    $('#contenidoCheckout').innerHTML = `
        <h2>Finalizar pedido</h2>
        <table class="tabla">
            <thead><tr><th>Producto</th><th>Cantidad</th><th>Subtotal</th></tr></thead>
            <tbody>${resumen}</tbody>
        </table>
        <p style="margin: 0.8rem 0; font-weight: 700;">Total:
            ${moneda(carrito.total())}</p>
        <form id="formCheckout">
            <div class="campo">
                <label for="cNombre">Tu nombre</label>
                <input type="text" id="cNombre" required maxlength="120" value="${escapeHtml(usuario.nombre)}">
            </div>
            <div class="campo">
                <label for="cTelefono">Teléfono de contacto</label>
                <input type="tel" id="cTelefono" maxlength="20" value="${escapeHtml(usuario.telefono ?? '')}">
            </div>
            <div class="campo">
                <label for="cMedioPago">Medio de pago</label>
                <select id="cMedioPago" required>
                    ${mediosPago.map((m) => `<option value="${m.id}">${escapeHtml(m.nombre)}</option>`).join('')}
                </select>
            </div>
            <button class="btn btn-primario" type="submit">Confirmar pedido</button>
        </form>`;
    $('#formCheckout').addEventListener('submit', confirmarPedido);
    cerrarCarrito();
    $('#overlayCheckout').classList.add('abierto');
}

async function confirmarPedido(evento) {
    evento.preventDefault();
    const boton = $('#formCheckout button[type="submit"]');
    boton.disabled = true;
    const cuerpo = {
        compradorNombre: $('#cNombre').value.trim(),
        compradorTelefono: $('#cTelefono').value.trim() || null,
        medioPagoId: Number($('#cMedioPago').value),
        items: carrito.items().map((item) => ({
            productoId: item.productoId,
            cantidad: item.cantidad,
        })),
    };
    try {
        const pedido = await api.post('/pedidos', cuerpo);
        carrito.vaciar();
        actualizarCarrito();
        $('#contenidoCheckout').innerHTML = `
            <div class="exito">
                <div class="circulo">&#10003;</div>
                <h2>¡Pedido confirmado!</h2>
                <p>Tu pedido quedó registrado con el número</p>
                <div class="pedido-id">#${pedido.id}</div>
                <p style="color: var(--gris); margin: 0.8rem 0;">El stock fue descontado automáticamente.
                    Recibirás notificaciones de cada cambio de estado.</p>
                <a class="btn btn-primario" href="/pedidos.html?id=${pedido.id}">
                    Ver estado del pedido</a>
            </div>`;
        await cargarProductos();
    } catch (error) {
        mostrarToast(error.message, 'error');
        const mensaje = error.status === 401
            ? 'Tu sesión expiró. <a href="/login.html?volver=/">Inicia sesión de nuevo</a> para confirmar el pedido.'
            : escapeHtml(error.message);
        $('#contenidoCheckout').innerHTML = `
            <h2>Finalizar pedido</h2>
            <p style="color: var(--rojo); margin-bottom: 1rem;">${mensaje}</p>
            <button class="btn btn-secundario" type="button" id="btnVolverCheckout">Volver</button>`;
        $('#btnVolverCheckout').addEventListener('click', () => {
            $('#overlayCheckout').classList.remove('abierto');
            abrirCheckout();
        });
        await cargarProductos();
    } finally {
        boton.disabled = false;
    }
}

function abrirCarrito() {
    $('#drawerCarrito').classList.add('abierto');
    $('#overlayCarrito').classList.add('abierto');
}

function cerrarCarrito() {
    $('#drawerCarrito').classList.remove('abierto');
    $('#overlayCarrito').classList.remove('abierto');
}

function bindEventos() {
    $('#btnBuscar').addEventListener('click', cargarProductos);
    $('#busqueda').addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            cargarProductos();
        }
    });
    $('#filtroMunicipio').addEventListener('change', cargarProductos);
    $('#filtroCategoria').addEventListener('change', cargarProductos);
    $('#filtroDisponible').addEventListener('change', cargarProductos);
    $('#btnLimpiarFiltros').addEventListener('click', () => {
        $('#busqueda').value = '';
        $('#filtroMunicipio').value = '';
        $('#filtroCategoria').value = '';
        $('#filtroDisponible').checked = true;
        cargarProductos();
    });

    $('#gridProductos').addEventListener('click', (e) => {
        const ver = e.target.closest('[data-ver]');
        const agregar = e.target.closest('[data-agregar]');
        if (ver) {
            abrirDetalle(ver.dataset.ver);
        } else if (agregar) {
            agregarAlCarrito(agregar.dataset.agregar);
        }
    });

    $('#contenidoDetalle').addEventListener('click', (e) => {
        if (e.target.id === 'btnAgregarDetalle' && productoEnDetalle) {
            carrito.agregar(productoEnDetalle, 1);
            mostrarToast(`${productoEnDetalle.nombre} agregado al carrito`);
            actualizarCarrito();
        }
    });

    $('#btnAbrirCarrito').addEventListener('click', abrirCarrito);
    $('#btnCerrarCarrito').addEventListener('click', cerrarCarrito);
    $('#overlayCarrito').addEventListener('click', cerrarCarrito);
    $('#btnFinalizarPedido').addEventListener('click', abrirCheckout);

    $('#itemsCarrito').addEventListener('click', (e) => {
        const menos = e.target.closest('[data-menos]');
        const mas = e.target.closest('[data-mas]');
        const quitar = e.target.closest('[data-quitar]');
        if (menos) {
            const item = carrito.items().find((i) => i.productoId === Number(menos.dataset.menos));
            carrito.cambiarCantidad(Number(menos.dataset.menos), item.cantidad - 1);
            actualizarCarrito();
        } else if (mas) {
            const item = carrito.items().find((i) => i.productoId === Number(mas.dataset.mas));
            carrito.cambiarCantidad(Number(mas.dataset.mas), item.cantidad + 1);
            actualizarCarrito();
        } else if (quitar) {
            carrito.quitar(Number(quitar.dataset.quitar));
            actualizarCarrito();
        }
    });

    document.querySelectorAll('[data-cerrar]').forEach((boton) => {
        boton.addEventListener('click', () => {
            document.getElementById(boton.dataset.cerrar).classList.remove('abierto');
        });
    });

    window.addEventListener('carrito:actualizado', actualizarCarrito);
}

async function iniciar() {
    bindEventos();
    actualizarCarrito();
    try {
        await cargarCatalogo();
        await Promise.all([cargarProductos(), cargarPrecios()]);
    } catch (error) {
        mostrarToast(`No se pudo cargar el catálogo: ${error.message}`, 'error');
    }
}

iniciar();

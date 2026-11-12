import { api } from './api.js';
import { sesion } from './sesion.js';
import { escapeHtml, fechaHora, moneda, mostrarToast } from './ui.js';

const $ = (selector) => document.querySelector(selector);

let panelActual = null;
let agricultorActualId = null;
let productoEditando = null;
let categoriasCache = null;

function renderizarProductos(productos) {
    if (productos.length === 0) {
        return '<p class="ayuda">Aun no tienes publicaciones.</p>';
    }
    return `
        <table class="tabla">
            <thead><tr><th>Producto</th><th>Categoria</th><th>Cantidad</th>
                <th>Precio</th><th>Estado</th><th>Acciones</th></tr></thead>
            <tbody>
                ${productos.map((producto) => `
                    <tr>
                        <td>${escapeHtml(producto.nombre)}</td>
                        <td>${escapeHtml(producto.categoria)}</td>
                        <td>${producto.cantidadDisponible} ${escapeHtml(producto.unidad)}</td>
                        <td>${moneda(producto.precioUnitario)}</td>
                        <td>${producto.disponible ? 'Disponible' : 'Agotado'}</td>
                        <td class="acciones-producto">
                            <button class="btn btn-secundario btn-mini" type="button"
                                data-editar="${producto.id}">Editar</button>
                            <button class="btn btn-peligro btn-mini" type="button"
                                data-eliminar="${producto.id}">Eliminar</button>
                        </td>
                    </tr>`).join('')}
            </tbody>
        </table>`;
}

function renderizarPedidos(pedidos) {
    if (pedidos.length === 0) {
        return '<p class="ayuda">Aun no has recibido pedidos.</p>';
    }
    return `
        <table class="tabla">
            <thead><tr><th>Pedido</th><th>Comprador</th><th>Fecha</th>
                <th>Estado</th><th>Productos</th><th>Total para ti</th></tr></thead>
            <tbody>
                ${pedidos.map((pedido) => `
                    <tr>
                        <td>#${pedido.id}</td>
                        <td>${escapeHtml(pedido.compradorNombre)}</td>
                        <td>${fechaHora(pedido.fechaCreacion)}</td>
                        <td>${escapeHtml(pedido.estado)}</td>
                        <td>${pedido.items.map((item) =>
                            `${escapeHtml(item.producto)} (${item.cantidad} ${escapeHtml(item.unidad)})`).join('<br>')}</td>
                        <td>${moneda(pedido.totalAgricultor)}</td>
                    </tr>`).join('')}
            </tbody>
        </table>`;
}

function renderizarPanel(panel) {
    $('#contenidoPanel').innerHTML = `
        <div class="resumen-panel">
            <article class="tarjeta-resumen">
                <span class="numero">${panel.productos.length}</span>
                <span class="etiqueta">Publicaciones</span>
            </article>
            <article class="tarjeta-resumen">
                <span class="numero">${panel.pedidosRecibidos}</span>
                <span class="etiqueta">Pedidos recibidos</span>
            </article>
            <article class="tarjeta-resumen">
                <span class="numero">${moneda(panel.ventasTotales)}</span>
                <span class="etiqueta">Ventas acumuladas</span>
            </article>
        </div>
        <h3 class="seccion-titulo">Publicaciones</h3>
        ${renderizarProductos(panel.productos)}
        <h3 class="seccion-titulo">Pedidos recibidos</h3>
        ${renderizarPedidos(panel.pedidos)}`;
}

async function cargarPanel(id) {
    if (!id) {
        return;
    }
    $('#contenidoPanel').innerHTML = '<div class="cargando">Cargando panel...</div>';
    try {
        const panel = await api.get(`/agricultores/${id}/panel`);
        panelActual = panel;
        agricultorActualId = id;
        renderizarPanel(panel);
    } catch (error) {
        if (error.status === 401) {
            $('#contenidoPanel').innerHTML = '<p class="ayuda">Debes <a href="/login.html?volver=/panel.html">iniciar sesion</a> como agricultor para ver tu panel.</p>';
            return;
        }
        mostrarToast(error.message, 'error');
        $('#contenidoPanel').innerHTML = `<p class="ayuda">${escapeHtml(error.message)}</p>`;
    }
}

async function cargarCategorias() {
    if (categoriasCache) {
        return categoriasCache;
    }
    categoriasCache = await api.get('/catalogo/categorias');
    const select = $('#eCategoria');
    select.innerHTML = '';
    categoriasCache.forEach((categoria) => select.add(new Option(categoria.etiqueta, categoria.nombre)));
    return categoriasCache;
}

async function abrirEdicion(id) {
    const producto = panelActual?.productos.find((p) => p.id === id);
    if (!producto) {
        return;
    }
    try {
        await cargarCategorias();
    } catch (error) {
        mostrarToast(`No se pudieron cargar las categorias: ${error.message}`, 'error');
        return;
    }
    productoEditando = producto;
    $('#eNombre').value = producto.nombre;
    $('#eCategoria').value = producto.categoria;
    $('#eCantidad').value = producto.cantidadDisponible;
    $('#eUnidad').value = producto.unidad;
    $('#ePrecio').value = producto.precioUnitario;
    $('#eFechaCosecha').value = (producto.fechaCosecha ?? '').slice(0, 10);
    $('#overlayEditar').classList.add('abierto');
    $('#eNombre').focus();
}

function cerrarEdicion() {
    productoEditando = null;
    $('#overlayEditar').classList.remove('abierto');
}

async function guardarEdicion(evento) {
    evento.preventDefault();
    if (!productoEditando) {
        return;
    }
    const boton = $('#formEditar button[type="submit"]');
    boton.disabled = true;
    const cuerpo = {
        nombre: $('#eNombre').value.trim(),
        categoria: $('#eCategoria').value,
        cantidadDisponible: Number($('#eCantidad').value),
        precioUnitario: Number($('#ePrecio').value),
        unidad: $('#eUnidad').value.trim() || 'kg',
        fechaCosecha: $('#eFechaCosecha').value,
    };
    try {
        const actualizado = await api.put(`/productos/${productoEditando.id}`, cuerpo);
        mostrarToast(`Producto "${actualizado.nombre}" actualizado`);
        cerrarEdicion();
        await cargarPanel(agricultorActualId);
    } catch (error) {
        mostrarToast(error.message, 'error');
    } finally {
        boton.disabled = false;
    }
}

async function eliminarProducto(id) {
    const producto = panelActual?.productos.find((p) => p.id === id);
    if (!producto) {
        return;
    }
    const confirmado = window.confirm(
        `¿Eliminar la publicacion "${producto.nombre}"? Esta accion no se puede deshacer.`);
    if (!confirmado) {
        return;
    }
    try {
        await api.delete(`/productos/${id}`);
        mostrarToast(`Producto "${producto.nombre}" eliminado`);
        await cargarPanel(agricultorActualId);
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

function manejarAcciones(evento) {
    const botonEditar = evento.target.closest('[data-editar]');
    if (botonEditar) {
        abrirEdicion(Number(botonEditar.dataset.editar));
        return;
    }
    const botonEliminar = evento.target.closest('[data-eliminar]');
    if (botonEliminar) {
        eliminarProducto(Number(botonEliminar.dataset.eliminar));
    }
}

async function iniciar() {
    $('#contenidoPanel').addEventListener('click', manejarAcciones);
    $('#formEditar').addEventListener('submit', guardarEdicion);
    $('#cerrarEditar').addEventListener('click', cerrarEdicion);
    $('#overlayEditar').addEventListener('click', (evento) => {
        if (evento.target === evento.currentTarget) {
            cerrarEdicion();
        }
    });

    const usuario = sesion.usuario();
    const select = $('#agricultorPanel');
    const boton = $('#btnCargarPanel');

    if (!usuario) {
        select.innerHTML = '<option value="">Inicia sesion para ver tu panel</option>';
        boton.disabled = true;
        $('#contenidoPanel').innerHTML = '<p class="ayuda">Debes <a href="/login.html?volver=/panel.html">iniciar sesion</a> como agricultor para consultar tu panel.</p>';
        return;
    }

    if (usuario.rol === 'AGRICULTOR' && usuario.agricultorId) {
        select.innerHTML = `<option value="${usuario.agricultorId}">Mi panel</option>`;
        select.disabled = true;
        boton.style.display = 'none';
        await cargarPanel(usuario.agricultorId);
        return;
    }

    if (usuario.rol === 'ADMIN') {
        const agricultores = await api.get('/agricultores');
        select.innerHTML = agricultores
            .map((a) => `<option value="${a.id}">${escapeHtml(a.nombre)} - ${escapeHtml(a.finca ?? 'sin finca')}</option>`)
            .join('') || '<option value="">No hay agricultores</option>';
        boton.addEventListener('click', () => cargarPanel(select.value));
        if (select.value) {
            await cargarPanel(select.value);
        }
        return;
    }

    select.innerHTML = '<option value="">Cuenta sin panel de agricultor</option>';
    boton.disabled = true;
    $('#contenidoPanel').innerHTML = '<p class="ayuda">Tu cuenta es de comprador. El panel esta disponible para agricultores.</p>';
}

iniciar().catch((error) => mostrarToast(error.message, 'error'));

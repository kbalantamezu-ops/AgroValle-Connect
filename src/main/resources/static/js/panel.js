import { api } from './api.js';
import { escapeHtml, fechaHora, moneda, mostrarToast } from './ui.js';

const $ = (selector) => document.querySelector(selector);

async function cargarAgricultores() {
    const agricultores = await api.get('/agricultores');
    const select = $('#agricultorPanel');
    if (agricultores.length === 0) {
        select.innerHTML = '<option value="">No hay agricultores registrados</option>';
        return;
    }
    select.innerHTML = agricultores
        .map((agricultor) => `<option value="${agricultor.id}">${escapeHtml(agricultor.nombre)}
            - ${escapeHtml(agricultor.finca ?? 'sin finca')}</option>`)
        .join('');
    await cargarPanel();
}

function renderizarProductos(productos) {
    if (productos.length === 0) {
        return '<p class="ayuda">Aún no tienes publicaciones.</p>';
    }
    return `
        <table class="tabla">
            <thead><tr><th>Producto</th><th>Categoría</th><th>Cantidad</th>
                <th>Precio</th><th>Estado</th></tr></thead>
            <tbody>
                ${productos.map((producto) => `
                    <tr>
                        <td>${escapeHtml(producto.nombre)}</td>
                        <td>${escapeHtml(producto.categoria)}</td>
                        <td>${producto.cantidadDisponible} ${escapeHtml(producto.unidad)}</td>
                        <td>${moneda(producto.precioUnitario)}</td>
                        <td>${producto.disponible ? 'Disponible' : 'Agotado'}</td>
                    </tr>`).join('')}
            </tbody>
        </table>`;
}

function renderizarPedidos(pedidos) {
    if (pedidos.length === 0) {
        return '<p class="ayuda">Aún no has recibido pedidos.</p>';
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

async function cargarPanel() {
    const id = $('#agricultorPanel').value;
    if (!id) {
        return;
    }
    $('#contenidoPanel').innerHTML = '<div class="cargando">Cargando panel...</div>';
    try {
        const panel = await api.get(`/agricultores/${id}/panel`);
        renderizarPanel(panel);
    } catch (error) {
        mostrarToast(error.message, 'error');
        $('#contenidoPanel').innerHTML = `<p class="ayuda">${escapeHtml(error.message)}</p>`;
    }
}

$('#btnCargarPanel').addEventListener('click', cargarPanel);
$('#agricultorPanel').addEventListener('change', cargarPanel);
cargarAgricultores().catch((error) => mostrarToast(error.message, 'error'));

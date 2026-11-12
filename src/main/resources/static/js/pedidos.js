import { api } from './api.js';
import { carrito } from './carrito.js';
import { sesion } from './sesion.js';
import { escapeHtml, fechaHora, moneda, mostrarToast } from './ui.js';

const $ = (selector) => document.querySelector(selector);
const usuario = sesion.usuario();
const puedeGestionar = usuario?.rol === 'AGRICULTOR' || usuario?.rol === 'ADMIN';

function sinSesion(contenedor) {
    contenedor.innerHTML = `
        <div class="vacio">
            Inicia sesión para consultar pedidos.
            <a href="/login.html?volver=/pedidos.html">Entrar o registrarme</a>
        </div>`;
}

const PASOS = ['PENDIENTE', 'EN_PREPARACION', 'ENVIADO', 'ENTREGADO'];
const ETIQUETAS = ['Pendiente', 'En preparación', 'Enviado', 'Entregado'];
const SIGUIENTE = {
    PENDIENTE: 'EN_PREPARACION',
    EN_PREPARACION: 'ENVIADO',
    ENVIADO: 'ENTREGADO',
};

function claseEstado(estado) {
    return estado.toLowerCase().replaceAll('_', '-');
}

function pagoHtml(pedido) {
    if (!pedido.pago) {
        return '—';
    }
    const fechaPago = pedido.pago.fechaPago ? ` · ${fechaHora(pedido.pago.fechaPago)}` : '';
    return `${escapeHtml(pedido.pago.medioPago)} (${escapeHtml(pedido.pago.estado)})${fechaPago}`;
}

async function consultarPedido(id) {
    if (!id) {
        mostrarToast('Ingresa un número de pedido válido', 'error');
        return;
    }
    if (!usuario) {
        sinSesion($('#contenidoPedidos'));
        return;
    }
    try {
        const [pedido, notificaciones] = await Promise.all([
            api.get(`/pedidos/${id}`),
            api.get(`/pedidos/${id}/notificaciones`),
        ]);
        renderPedido(pedido, notificaciones);
    } catch (error) {
        if (error.status === 401) {
            sinSesion($('#contenidoPedidos'));
            return;
        }
        mostrarToast(error.message, 'error');
        $('#contenidoPedidos').innerHTML =
            `<div class="vacio">${escapeHtml(error.message)}</div>`;
    }
}

async function listarPedidos() {
    if (!usuario) {
        sinSesion($('#contenidoPedidos'));
        return;
    }
    try {
        const pedidos = await api.get('/pedidos');
        if (pedidos.length === 0) {
            $('#contenidoPedidos').innerHTML =
                '<div class="vacio">Todavía no tienes pedidos registrados.</div>';
            return;
        }
        const filas = pedidos.map((pedido) => `
            <tr>
                <td><a href="#" data-ver="${pedido.id}">#${pedido.id}</a></td>
                <td>${escapeHtml(pedido.compradorNombre)}</td>
                <td>${fechaHora(pedido.fechaCreacion)}</td>
                <td><span class="badge-estado badge-${claseEstado(pedido.estado)}">
                    ${escapeHtml(pedido.estado)}</span></td>
                <td>${moneda(pedido.total)}</td>
            </tr>`).join('');
        $('#contenidoPedidos').innerHTML = `
            <table class="tabla">
                <thead>
                    <tr><th>Pedido</th><th>Comprador</th><th>Fecha</th><th>Estado</th><th>Total</th></tr>
                </thead>
                <tbody>${filas}</tbody>
            </table>`;
    } catch (error) {
        if (error.status === 401) {
            sinSesion($('#contenidoPedidos'));
            return;
        }
        mostrarToast(error.message, 'error');
    }
}

let pedidoActual = null;

function renderPedido(pedido, notificaciones) {
    pedidoActual = pedido;
    const items = pedido.items.map((item) => `
        <tr>
            <td>${escapeHtml(item.productoNombre)}</td>
            <td>${escapeHtml(item.municipio)}</td>
            <td>${item.cantidad}</td>
            <td>${moneda(item.precioUnitario)}</td>
            <td>${moneda(item.subtotal)}</td>
        </tr>`).join('');

    const listaNotificaciones = notificaciones.map((n) => `
        <div class="notificacion">
            ${escapeHtml(n.mensaje)} <time>· ${fechaHora(n.fechaCreacion)}</time>
        </div>`).join('');

    const pasos = PASOS.map((paso, indice) => {
        const activo = pedido.estado !== 'CANCELADO' && PASOS.indexOf(pedido.estado) >= indice;
        return `
            <div class="paso ${activo ? 'activo' : ''}">
                <div class="punto">${indice + 1}</div>
                <span>${ETIQUETAS[indice]}</span>
            </div>`;
    }).join('');

    const cancelado = pedido.estado === 'CANCELADO' ? `
        <div class="paso cancelado" style="flex: 0.6;">
            <div class="punto">X</div>
            <span>Cancelado</span>
        </div>` : '';

    const puedeAvanzar = puedeGestionar && SIGUIENTE[pedido.estado] !== undefined;
    const puedeCancelar = puedeGestionar && pedido.estado !== 'CANCELADO' && pedido.estado !== 'ENTREGADO';

    $('#contenidoPedidos').innerHTML = `
        <article class="tarjeta-pedido">
            <div class="fila">
                <h2 style="color: var(--verde-800);">Pedido #${pedido.id}</h2>
                <span class="badge-estado badge-${claseEstado(pedido.estado)}">${pedido.estado}</span>
                <span class="espaciador"></span>
                <span style="color: var(--gris);">${fechaHora(pedido.fechaCreacion)}</span>
            </div>
            <div class="fila">
                <span><strong>Comprador:</strong> ${escapeHtml(pedido.compradorNombre)}</span>
                <span><strong>Teléfono:</strong> ${escapeHtml(pedido.compradorTelefono || '—')}</span>
                <span><strong>Medio de pago:</strong> ${pagoHtml(pedido)}</span>
            </div>

            <div class="timeline">${pasos}${cancelado}</div>

            <table class="tabla">
                <thead>
                    <tr><th>Producto</th><th>Origen</th><th>Cantidad</th>
                        <th>Precio unitario</th><th>Subtotal</th></tr>
                </thead>
                <tbody>${items}</tbody>
            </table>

            <div class="fila" style="margin-top: 0.8rem;">
                <span class="espaciador"></span>
                <strong style="font-size: 1.15rem; color: var(--verde-800);">
                    Total: ${moneda(pedido.total)}</strong>
            </div>

            ${puedeAvanzar || puedeCancelar ? `
            <div class="acciones-pedido">
                ${puedeAvanzar ? `<button class="btn btn-primario" type="button" data-avanzar="${pedido.id}">
                    Marcar como ${ETIQUETAS[PASOS.indexOf(SIGUIENTE[pedido.estado])].toLowerCase()}</button>` : ''}
                ${puedeCancelar ? `<button class="btn btn-peligro" type="button" data-cancelar="${pedido.id}">
                    Cancelar pedido</button>` : ''}
            </div>` : ''}

            <h3 style="color: var(--verde-800); margin-top: 1.2rem;">Notificaciones</h3>
            <div class="notificaciones">
                ${listaNotificaciones || '<p style="color: var(--gris);">Sin notificaciones.</p>'}
            </div>
        </article>`;
}

async function cambiarEstado(id, estado) {
    try {
        const pedido = await api.patch(`/pedidos/${id}/estado`, { estado });
        const notificaciones = await api.get(`/pedidos/${id}/notificaciones`);
        renderPedido(pedido, notificaciones);
        mostrarToast(`Pedido #${id} actualizado a ${estado}`);
    } catch (error) {
        if (error.status === 401) {
            sinSesion($('#contenidoPedidos'));
            return;
        }
        mostrarToast(error.message, 'error');
    }
}

function bindEventos() {
    $('#btnBuscarPedido').addEventListener('click', () => {
        consultarPedido(Number($('#buscarPedidoId').value));
    });
    $('#buscarPedidoId').addEventListener('keydown', (e) => {
        if (e.key === 'Enter') {
            consultarPedido(Number($('#buscarPedidoId').value));
        }
    });
    $('#btnListarPedidos').addEventListener('click', listarPedidos);

    $('#contenidoPedidos').addEventListener('click', (e) => {
        const ver = e.target.closest('[data-ver]');
        const avanzar = e.target.closest('[data-avanzar]');
        const cancelar = e.target.closest('[data-cancelar]');
        if (ver) {
            e.preventDefault();
            consultarPedido(Number(ver.dataset.ver));
        } else if (avanzar) {
            if (pedidoActual && SIGUIENTE[pedidoActual.estado]) {
                cambiarEstado(pedidoActual.id, SIGUIENTE[pedidoActual.estado]);
            }
        } else if (cancelar) {
            cambiarEstado(Number(cancelar.dataset.cancelar), 'CANCELADO');
        }
    });
}

function iniciar() {
    bindEventos();
    const parametros = new URLSearchParams(window.location.search);
    const id = parametros.get('id');
    if (!usuario) {
        $('#btnListarPedidos').disabled = true;
        $('#btnBuscarPedido').disabled = true;
        sinSesion($('#contenidoPedidos'));
    } else if (id) {
        consultarPedido(Number(id));
    } else {
        listarPedidos();
    }
    if (carrito.contar() > 0) {
        mostrarToast('Tienes productos en el carrito. Finaliza tu pedido desde el catálogo.');
    }
}

iniciar();

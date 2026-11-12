import { api } from './api.js';
import { sesion } from './sesion.js';
import { escapeHtml, mostrarToast } from './ui.js';

const $ = (selector) => document.querySelector(selector);

async function cargarMunicipios() {
    const municipios = await api.get('/catalogo/municipios');
    const select = $('#raMunicipio');
    municipios.forEach((m) => select.add(new Option(m.etiqueta, m.nombre)));
}

function mostrarSesion() {
    const contenedor = $('#sesionActual');
    const usuario = sesion.usuario();
    if (!usuario) {
        contenedor.style.display = 'none';
        return;
    }
    contenedor.style.display = 'block';
    contenedor.innerHTML = `
        <h2>Sesion activa</h2>
        <p><strong>${escapeHtml(usuario.nombre)}</strong> (${escapeHtml(usuario.email)})</p>
        <p>Rol: ${escapeHtml(usuario.rol)}${usuario.agricultorId ? ` · Agricultor #${usuario.agricultorId}` : ''}</p>
        <button class="btn btn-peligro" type="button" id="btnCerrarSesion">Cerrar sesion</button>`;
    $('#btnCerrarSesion').addEventListener('click', () => {
        sesion.cerrar();
        mostrarToast('Sesion cerrada');
        mostrarSesion();
    });
}

async function login(evento) {
    evento.preventDefault();
    try {
        const datos = await api.post('/auth/login', {
            email: $('#lEmail').value.trim(),
            password: $('#lPassword').value,
        });
        sesion.guardar(datos);
        const volver = new URLSearchParams(window.location.search).get('volver');
        if (volver && volver.startsWith('/')) {
            window.location.href = volver;
            return;
        }
        mostrarToast(`Bienvenido, ${datos.usuario.nombre}`);
        mostrarSesion();
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

async function registrarComprador(evento) {
    evento.preventDefault();
    try {
        await api.post('/auth/registro/comprador', {
            nombre: $('#rcNombre').value.trim(),
            email: $('#rcEmail').value.trim(),
            password: $('#rcPassword').value,
            telefono: $('#rcTelefono').value.trim() || null,
        });
        mostrarToast('Cuenta de comprador creada. Ahora puedes iniciar sesion.');
        evento.target.reset();
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

async function registrarAgricultor(evento) {
    evento.preventDefault();
    try {
        const usuario = await api.post('/auth/registro/agricultor', {
            nombre: $('#raNombre').value.trim(),
            cedula: $('#raCedula').value.trim(),
            municipio: $('#raMunicipio').value,
            telefono: $('#raTelefono').value.trim() || null,
            email: $('#raEmail').value.trim(),
            finca: $('#raFinca').value.trim() || null,
            password: $('#raPassword').value,
        });
        mostrarToast('Cuenta de agricultor creada. Inicia sesion para publicar.');
        evento.target.reset();
        if (usuario) {
            $('#lEmail').value = usuario.email;
        }
    } catch (error) {
        mostrarToast(error.message, 'error');
    }
}

async function iniciar() {
    $('#formLogin').addEventListener('submit', login);
    $('#formRegistroComprador').addEventListener('submit', registrarComprador);
    $('#formRegistroAgricultor').addEventListener('submit', registrarAgricultor);
    await cargarMunicipios().catch((e) => mostrarToast(e.message, 'error'));
    mostrarSesion();
}

iniciar();

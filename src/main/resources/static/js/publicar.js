import { api } from './api.js';
import { mostrarToast } from './ui.js';

const $ = (selector) => document.querySelector(selector);

function hoy() {
    return new Date().toISOString().split('T')[0];
}

async function cargarOpciones() {
    const [municipios, categorias] = await Promise.all([
        api.get('/catalogo/municipios'),
        api.get('/catalogo/categorias'),
    ]);
    const selectMunicipio = $('#aMunicipio');
    const selectCategoria = $('#pCategoria');
    municipios.forEach((m) => selectMunicipio.add(new Option(m.etiqueta, m.nombre)));
    categorias.forEach((c) => selectCategoria.add(new Option(c.etiqueta, c.nombre)));
}

async function cargarAgricultores() {
    const select = $('#pAgricultor');
    select.innerHTML = '<option value="">Selecciona un agricultor...</option>';
    const agricultores = await api.get('/agricultores');
    agricultores.forEach((a) => {
        select.add(new Option(`${a.nombre} (${a.municipio})`, a.id));
    });
}

async function registrarAgricultor(evento) {
    evento.preventDefault();
    const boton = $('#formAgricultor button[type="submit"]');
    boton.disabled = true;
    const cuerpo = {
        nombre: $('#aNombre').value.trim(),
        cedula: $('#aCedula').value.trim(),
        municipio: $('#aMunicipio').value,
        telefono: $('#aTelefono').value.trim() || null,
        email: $('#aEmail').value.trim() || null,
        finca: $('#aFinca').value.trim() || null,
    };
    try {
        const agricultor = await api.post('/agricultores', cuerpo);
        mostrarToast(`Agricultor ${agricultor.nombre} registrado con éxito`);
        evento.target.reset();
        await cargarAgricultores();
        $('#pAgricultor').value = agricultor.id;
    } catch (error) {
        mostrarToast(error.message, 'error');
    } finally {
        boton.disabled = false;
    }
}

async function publicarProducto(evento) {
    evento.preventDefault();
    const boton = $('#formProducto button[type="submit"]');
    boton.disabled = true;
    const cuerpo = {
        nombre: $('#pNombre').value.trim(),
        categoria: $('#pCategoria').value,
        cantidadDisponible: Number($('#pCantidad').value),
        precioUnitario: Number($('#pPrecio').value),
        unidad: $('#pUnidad').value.trim() || 'kg',
        fechaCosecha: $('#pFechaCosecha').value,
        agricultorId: Number($('#pAgricultor').value),
    };
    try {
        const producto = await api.post('/productos', cuerpo);
        mostrarToast(`Producto "${producto.nombre}" publicado con éxito`);
        evento.target.reset();
        $('#pUnidad').value = 'kg';
        $('#pFechaCosecha').value = hoy();
    } catch (error) {
        mostrarToast(error.message, 'error');
    } finally {
        boton.disabled = false;
    }
}

async function iniciar() {
    $('#pFechaCosecha').min = hoy();
    $('#pFechaCosecha').value = hoy();
    $('#formAgricultor').addEventListener('submit', registrarAgricultor);
    $('#formProducto').addEventListener('submit', publicarProducto);
    try {
        await cargarOpciones();
        await cargarAgricultores();
    } catch (error) {
        mostrarToast(`No se pudo cargar el formulario: ${error.message}`, 'error');
    }
}

iniciar();

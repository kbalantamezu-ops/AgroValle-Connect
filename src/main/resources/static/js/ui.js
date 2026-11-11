const formatoMoneda = new Intl.NumberFormat('es-CO', {
    style: 'currency',
    currency: 'COP',
    maximumFractionDigits: 0,
});

export function moneda(valor) {
    if (valor === null || valor === undefined) {
        return '—';
    }
    return formatoMoneda.format(valor);
}

export function fecha(texto) {
    if (!texto) {
        return '—';
    }
    const partes = texto.split('T')[0].split('-');
    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

export function fechaHora(texto) {
    if (!texto) {
        return '—';
    }
    const [fechaParte, horaParte] = texto.split('T');
    return `${fecha(fechaParte)} ${horaParte ? horaParte.slice(0, 5) : ''}`;
}

export function mostrarToast(mensaje, tipo = 'exito') {
    let contenedor = document.querySelector('.contenedor-toasts');
    if (!contenedor) {
        contenedor = document.createElement('div');
        contenedor.className = 'contenedor-toasts';
        document.body.appendChild(contenedor);
    }
    const toast = document.createElement('div');
    toast.className = `toast ${tipo}`;
    toast.textContent = mensaje;
    contenedor.appendChild(toast);
    setTimeout(() => toast.remove(), 4200);
}

export function etiquetaDe(opciones, nombre) {
    const encontrada = opciones.find((opcion) => opcion.nombre === nombre);
    return encontrada ? encontrada.etiqueta : nombre;
}

export function escapeHtml(texto) {
    return String(texto ?? '')
        .replaceAll('&', '&amp;')
        .replaceAll('<', '&lt;')
        .replaceAll('>', '&gt;')
        .replaceAll('"', '&quot;')
        .replaceAll("'", '&#39;');
}

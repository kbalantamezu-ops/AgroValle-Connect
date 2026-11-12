const CLAVE = 'agrovalle_sesion';

function leer() {
    try {
        const valor = localStorage.getItem(CLAVE);
        return valor ? JSON.parse(valor) : null;
    } catch {
        localStorage.removeItem(CLAVE);
        return null;
    }
}

export const sesion = {
    guardar(datos) {
        localStorage.setItem(CLAVE, JSON.stringify(datos));
        actualizarNav();
    },
    obtener() {
        return leer();
    },
    token() {
        return leer()?.token ?? null;
    },
    usuario() {
        return leer()?.usuario ?? null;
    },
    cerrar() {
        localStorage.removeItem(CLAVE);
        actualizarNav();
    },
    estaAutenticado() {
        return Boolean(this.token());
    },
};

function actualizarNav() {
    const usuario = leer()?.usuario;
    document.querySelectorAll('[data-nav-sesion]').forEach((enlace) => {
        enlace.textContent = usuario ? `Hola, ${usuario.nombre.split(' ')[0]}` : 'Cuenta';
        enlace.title = usuario ? `${usuario.nombre} (${usuario.rol})` : 'Iniciar sesión';
    });
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', actualizarNav);
} else {
    actualizarNav();
}

const CLAVE = 'agrovalle-carrito';

function leer() {
    try {
        return JSON.parse(localStorage.getItem(CLAVE)) ?? [];
    } catch {
        return [];
    }
}

function guardar(items) {
    localStorage.setItem(CLAVE, JSON.stringify(items));
    window.dispatchEvent(new CustomEvent('carrito:actualizado'));
}

export const carrito = {
    items: () => leer(),

    agregar(producto, cantidad) {
        const items = leer();
        const existente = items.find((item) => item.productoId === producto.id);
        if (existente) {
            existente.cantidad = Math.min(existente.cantidad + cantidad,
                producto.cantidadDisponible);
        } else {
            items.push({
                productoId: producto.id,
                nombre: producto.nombre,
                municipio: producto.municipio,
                unidad: producto.unidad,
                precioUnitario: producto.precioUnitario,
                cantidad,
                stock: producto.cantidadDisponible,
            });
        }
        guardar(items);
    },

    cambiarCantidad(productoId, cantidad) {
        const items = leer();
        const item = items.find((i) => i.productoId === productoId);
        if (!item) {
            return;
        }
        if (cantidad <= 0) {
            this.quitar(productoId);
            return;
        }
        item.cantidad = Math.min(cantidad, item.stock);
        guardar(items);
    },

    quitar(productoId) {
        guardar(leer().filter((item) => item.productoId !== productoId));
    },

    vaciar() {
        guardar([]);
    },

    total() {
        return leer().reduce((suma, item) =>
            suma + item.precioUnitario * item.cantidad, 0);
    },

    contar() {
        return leer().reduce((suma, item) => suma + item.cantidad, 0);
    },
};

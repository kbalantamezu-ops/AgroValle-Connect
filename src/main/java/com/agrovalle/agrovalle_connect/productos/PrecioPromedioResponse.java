package com.agrovalle.agrovalle_connect.productos;

import java.math.BigDecimal;

public record PrecioPromedioResponse(
        String categoria,
        BigDecimal promedio,
        long cantidadProductos,
        String moneda) {
}

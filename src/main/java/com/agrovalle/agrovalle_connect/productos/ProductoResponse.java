package com.agrovalle.agrovalle_connect.productos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Datos de salida de un producto publicado. */
public record ProductoResponse(
    Long id,
    Long agricultorId,
    Long categoriaId,
    String nombre,
    String descripcion,
    BigDecimal precio,
    Integer cantidadDisponible,
    String unidad,
    String fotoUrl,
    LocalDateTime fechaPublicacion) {
}

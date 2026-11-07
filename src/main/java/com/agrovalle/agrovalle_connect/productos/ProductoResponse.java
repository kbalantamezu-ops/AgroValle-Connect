package com.agrovalle.agrovalle_connect.productos;

import java.math.BigDecimal;
<<<<<<< HEAD
import java.time.LocalDate;
import java.time.LocalDateTime;

public record ProductoResponse(
        Long id,
        String nombre,
        String categoria,
        Double cantidadDisponible,
        BigDecimal precioUnitario,
        String unidad,
        LocalDate fechaCosecha,
        LocalDateTime fechaPublicacion,
        boolean disponible,
        String municipio,
        AgricultorResumen agricultor) {

    public record AgricultorResumen(Long id, String nombre, String finca, String telefono, String email) {
    }
=======
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
>>>>>>> origin/develop
}

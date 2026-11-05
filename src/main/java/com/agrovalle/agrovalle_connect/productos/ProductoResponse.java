package com.agrovalle.agrovalle_connect.productos;

import java.math.BigDecimal;
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
}

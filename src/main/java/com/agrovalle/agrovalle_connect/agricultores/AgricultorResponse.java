package com.agrovalle.agrovalle_connect.agricultores;

import java.time.LocalDateTime;

public record AgricultorResponse(
        Long id,
        String nombre,
        String cedula,
        String municipio,
        String telefono,
        String email,
        String finca,
        LocalDateTime fechaRegistro,
        Double promedioCalificacion) {
}

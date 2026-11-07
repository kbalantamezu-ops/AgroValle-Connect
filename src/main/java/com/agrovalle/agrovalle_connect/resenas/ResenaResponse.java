package com.agrovalle.agrovalle_connect.resenas;

import java.time.LocalDateTime;

public record ResenaResponse(
        Long id,
        Long agricultorId,
        Integer calificacion,
        String comentario,
        String autorNombre,
        LocalDateTime fechaCreacion) {
}

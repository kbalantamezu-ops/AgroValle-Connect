package com.agrovalle.agrovalle_connect.resenas;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResenaRequest(
        @NotNull(message = "El agricultor es obligatorio")
        Long agricultorId,
        @NotNull(message = "La calificación es obligatoria")
        @Min(value = 1, message = "La calificación mínima es 1")
        @Max(value = 5, message = "La calificación máxima es 5")
        Integer calificacion,
        @Size(max = 500, message = "El comentario no puede superar 500 caracteres")
        String comentario,
        @NotBlank(message = "El nombre del autor es obligatorio")
        @Size(max = 120, message = "El nombre del autor no puede superar 120 caracteres")
        String autorNombre) {
}

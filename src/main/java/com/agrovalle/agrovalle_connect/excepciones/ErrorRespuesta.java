package com.agrovalle.agrovalle_connect.excepciones;

import java.util.List;

/** Cuerpo estándar de las respuestas de error. */
public record ErrorRespuesta(int estado, String mensaje, List<String> detalles) {
}

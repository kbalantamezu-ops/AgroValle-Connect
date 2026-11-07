package com.agrovalle.agrovalle_connect.agricultores;

<<<<<<< HEAD
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
=======
/** Datos de salida de un agricultor (nunca incluye la contraseña). */
public record AgricultorResponse(Long id, String nombre, String ubicacionValle, String cedula, String email) {
>>>>>>> origin/develop
}

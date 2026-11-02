package com.agrovalle.agrovalle_connect.agricultores;

/** Datos de salida de un agricultor (nunca incluye la contraseña). */
public record AgricultorResponse(Long id, String nombre, String ubicacionValle, String cedula, String email) {
}

package com.agrovalle.agrovalle_connect.usuarios;

/** Datos de salida de un usuario (nunca incluye la contrasena). */
public record UsuarioResponse(Long id, String nombre, String email, String telefono, Rol rol,
        Long agricultorId) {
}

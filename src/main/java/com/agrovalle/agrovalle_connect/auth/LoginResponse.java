package com.agrovalle.agrovalle_connect.auth;

import com.agrovalle.agrovalle_connect.usuarios.UsuarioResponse;

/** Token emitido tras un inicio de sesion exitoso. */
public record LoginResponse(
        String token,
        String tokenType,
        long expiresInSeconds,
        UsuarioResponse usuario) {
}

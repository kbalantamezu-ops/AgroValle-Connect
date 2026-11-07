package com.agrovalle.agrovalle_connect.usuarios;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos de entrada para registrar un usuario comprador. */
public record UsuarioRequest(
    @NotBlank @Size(max = 120) String nombre,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(min = 8, max = 72) String password,
    @Size(max = 20) String telefono) {
}

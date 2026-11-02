package com.agrovalle.agrovalle_connect.agricultores;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos de entrada para registrar un agricultor. */
public record AgricultorRequest(
    @NotBlank @Size(max = 120) String nombre,
    @NotBlank @Size(max = 120) String ubicacionValle,
    @NotBlank @Pattern(regexp = "\\d{6,10}", message = "debe tener entre 6 y 10 dígitos") String cedula,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(min = 8, max = 72) String password) {
}

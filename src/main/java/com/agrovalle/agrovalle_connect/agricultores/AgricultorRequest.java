package com.agrovalle.agrovalle_connect.agricultores;

import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AgricultorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,
        @NotBlank(message = "La cédula es obligatoria")
        @Size(max = 20, message = "La cédula no puede superar 20 caracteres")
        String cedula,
        @NotNull(message = "El municipio es obligatorio")
        Municipio municipio,
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String telefono,
        @Email(message = "El correo no tiene un formato válido")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        String email,
        @Size(max = 120, message = "El nombre de la finca no puede superar 120 caracteres")
        String finca) {
}

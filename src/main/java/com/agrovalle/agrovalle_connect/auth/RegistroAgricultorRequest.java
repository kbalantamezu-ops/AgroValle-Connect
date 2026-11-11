package com.agrovalle.agrovalle_connect.auth;

import com.agrovalle.agrovalle_connect.agricultores.AgricultorRequest;
import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Registro conjunto del perfil de agricultor y su cuenta de acceso. */
public record RegistroAgricultorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,
        @NotBlank(message = "La cedula es obligatoria")
        @Size(max = 20, message = "La cedula no puede superar 20 caracteres")
        String cedula,
        @NotNull(message = "El municipio es obligatorio")
        Municipio municipio,
        @Size(max = 20, message = "El telefono no puede superar 20 caracteres")
        String telefono,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        @Size(max = 120, message = "El correo no puede superar 120 caracteres")
        String email,
        @Size(max = 120, message = "El nombre de la finca no puede superar 120 caracteres")
        String finca,
        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
        String password) {

    public AgricultorRequest aAgricultorRequest() {
        return new AgricultorRequest(nombre, cedula, municipio, telefono, email, finca);
    }
}

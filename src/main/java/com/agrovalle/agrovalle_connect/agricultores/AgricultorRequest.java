package com.agrovalle.agrovalle_connect.agricultores;

<<<<<<< HEAD
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
=======
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
>>>>>>> origin/develop
}

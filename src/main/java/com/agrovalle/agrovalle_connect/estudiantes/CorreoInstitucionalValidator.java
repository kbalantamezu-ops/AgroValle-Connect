package com.agrovalle.agrovalle_connect.estudiantes;

import java.util.regex.Pattern;

public class CorreoInstitucionalValidator {

    private static final String DOMINIO_INSTITUCIONAL = "@estudiante.uniajc.edu.co";

    // Valida formato general de correo + que termine en el dominio institucional
    private static final Pattern PATRON_CORREO = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+" + Pattern.quote(DOMINIO_INSTITUCIONAL) + "$"
    );

    public static boolean esCorreoInstitucionalValido(String correo) {
        if (correo == null || correo.isBlank()) {
            return false;
        }
        return PATRON_CORREO.matcher(correo.trim().toLowerCase()).matches();
    }
}
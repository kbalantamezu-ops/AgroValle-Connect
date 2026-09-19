package com.agrovalle.agrovalle_connect.estudiantes;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CorreoInstitucionalValidatorTest {

    @Test
    void acceptsInstitutionalEmail() {
        assertTrue(CorreoInstitucionalValidator
                .esCorreoInstitucionalValido("estudiante@estudiante.uniajc.edu.co"));
    }

    @Test
    void acceptsEmailWithWhitespaceAndUppercase() {
        assertTrue(CorreoInstitucionalValidator
                .esCorreoInstitucionalValido("  ESTUDIANTE@ESTUDIANTE.UNIAJC.EDU.CO  "));
    }

    @Test
    void rejectsNullOrBlankEmail() {
        assertFalse(CorreoInstitucionalValidator.esCorreoInstitucionalValido(null));
        assertFalse(CorreoInstitucionalValidator.esCorreoInstitucionalValido("   "));
    }

    @Test
    void rejectsExternalEmail() {
        assertFalse(CorreoInstitucionalValidator.esCorreoInstitucionalValido("persona@example.com"));
    }
}

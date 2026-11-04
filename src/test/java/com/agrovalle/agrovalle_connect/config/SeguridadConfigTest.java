package com.agrovalle.agrovalle_connect.config;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class SeguridadConfigTest {

  @Test
  void passwordEncoderCifraYVerificaContrasenas() {
    PasswordEncoder encoder = new SeguridadConfig().passwordEncoder();

    String hash = encoder.encode("clave1234");

    assertNotEquals("clave1234", hash);
    assertTrue(encoder.matches("clave1234", hash));
  }
}

package com.agrovalle.agrovalle_connect.auth;

import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Configuracion del token JWT. El secreto debe tener al menos 32 bytes (HS256). */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationMinutes, String issuer) {

    public JwtProperties {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "app.jwt.secret debe tener al menos 32 caracteres para HS256");
        }
        if (expirationMinutes <= 0) {
            expirationMinutes = 120;
        }
        if (issuer == null || issuer.isBlank()) {
            issuer = "agrovalle-connect";
        }
    }
}

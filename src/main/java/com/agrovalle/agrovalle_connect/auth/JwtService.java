package com.agrovalle.agrovalle_connect.auth;

import com.agrovalle.agrovalle_connect.usuarios.Rol;
import com.agrovalle.agrovalle_connect.usuarios.Usuario;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

/** Emite y valida los tokens JWT de la aplicacion. */
@Service
public class JwtService {

    private final JwtEncoder encoder;

    private final JwtDecoder decoder;

    private final JwtProperties properties;

    public JwtService(JwtEncoder encoder, JwtDecoder decoder, JwtProperties properties) {
        this.encoder = encoder;
        this.decoder = decoder;
        this.properties = properties;
    }

    public String generarToken(Usuario usuario) {
        Instant ahora = Instant.now();
        JwtClaimsSet.Builder claims = JwtClaimsSet.builder()
                .issuer(properties.issuer())
                .subject(usuario.getEmail())
                .issuedAt(ahora)
                .expiresAt(ahora.plus(properties.expirationMinutes(), ChronoUnit.MINUTES))
                .claim("uid", usuario.getId())
                .claim("rol", usuario.getRol().name());
        if (usuario.getAgricultorId() != null) {
            claims.claim("agricultorId", usuario.getAgricultorId());
        }
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        return encoder.encode(JwtEncoderParameters.from(header, claims.build())).getTokenValue();
    }

    public long expiracionEnSegundos() {
        return properties.expirationMinutes() * 60;
    }

    public Jwt decodificar(String token) {
        return decoder.decode(token);
    }

    public UsuarioPrincipal aPrincipal(Jwt jwt) {
        Number uid = jwt.getClaim("uid");
        Number agricultorId = jwt.getClaim("agricultorId");
        return new UsuarioPrincipal(
                uid == null ? null : uid.longValue(),
                jwt.getSubject(),
                Rol.valueOf(jwt.getClaimAsString("rol")),
                agricultorId == null ? null : agricultorId.longValue());
    }
}

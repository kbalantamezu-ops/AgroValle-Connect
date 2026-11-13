package com.agrovalle.agrovalle_connect.config;

import com.agrovalle.agrovalle_connect.auth.JwtAuthenticationFilter;
import com.agrovalle.agrovalle_connect.auth.JwtProperties;
import com.agrovalle.agrovalle_connect.auth.ManejadorErroresSeguridad;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

/** Configuracion de Spring Security: sesiones stateless y tokens JWT. */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(JwtProperties.class)
public class SeguridadConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder(JwtProperties properties) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(clave(properties)));
    }

    @Bean
    public JwtDecoder jwtDecoder(JwtProperties properties) {
        return NimbusJwtDecoder.withSecretKey(clave(properties))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ManejadorErroresSeguridad manejadorErrores,
            CorsConfigurationSource corsConfigurationSource) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .logout(logout -> logout.disable())
                .exceptionHandling(errores -> errores
                        .authenticationEntryPoint(manejadorErrores)
                        .accessDeniedHandler(manejadorErrores))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/favicon.ico",
                                "/error", "/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/estado").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/usuarios",
                                "/api/v1/agricultores", "/api/v1/fletes/calcular").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/catalogo/**",
                                "/api/v1/productos/**", "/api/v1/precios/**",
                                "/api/v1/medios-pago", "/api/v1/resenas/**",
                                "/api/v1/agricultores", "/api/v1/agricultores/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/resenas").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/productos")
                        .hasAnyRole("AGRICULTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/productos/*")
                        .hasAnyRole("AGRICULTOR", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/productos/*")
                        .hasAnyRole("AGRICULTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/agricultores/*")
                        .hasAnyRole("AGRICULTOR", "ADMIN")
                        .requestMatchers("/api/v1/agricultores/*/panel")
                        .hasAnyRole("AGRICULTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pedidos/*/estado")
                        .hasAnyRole("AGRICULTOR", "ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private SecretKey clave(JwtProperties properties) {
        return new SecretKeySpec(properties.secret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
    }
}

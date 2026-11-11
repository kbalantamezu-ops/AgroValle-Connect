package com.agrovalle.agrovalle_connect;

import com.agrovalle.agrovalle_connect.auth.JwtService;
import com.agrovalle.agrovalle_connect.pagos.MedioPago;
import com.agrovalle.agrovalle_connect.pagos.MedioPagoRepository;
import com.agrovalle.agrovalle_connect.usuarios.Rol;
import com.agrovalle.agrovalle_connect.usuarios.Usuario;
import com.agrovalle.agrovalle_connect.usuarios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseIntegracion {

    private static final String CLAVE_DEMO = "Clave1234*";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MedioPagoRepository medioPagoRepository;

    @Autowired
    protected UsuarioRepository usuarioRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected JwtService jwtService;

    protected long medioPagoId(String nombre) {
        return medioPagoRepository.findByNombre(nombre)
                .orElseGet(() -> medioPagoRepository.save(new MedioPago(nombre)))
                .getId();
    }

    protected String tokenComprador() {
        return tokenPara("comprador.test@agrovalle.co", "Comprador Test", Rol.COMPRADOR, null);
    }

    protected String tokenAdmin() {
        return tokenPara("admin.test@agrovalle.co", "Admin Test", Rol.ADMIN, null);
    }

    protected String tokenAgricultor(Long agricultorId) {
        return tokenPara("agricultor" + agricultorId + ".test@agrovalle.co",
                "Agricultor Test " + agricultorId, Rol.AGRICULTOR, agricultorId);
    }

    protected RequestPostProcessor conToken(String token) {
        return request -> {
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return request;
        };
    }

    private String tokenPara(String email, String nombre, Rol rol, Long agricultorId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> usuarioRepository.save(Usuario.builder()
                        .nombre(nombre)
                        .email(email)
                        .passwordHash(passwordEncoder.encode(CLAVE_DEMO))
                        .rol(rol)
                        .agricultorId(agricultorId)
                        .build()));
        return jwtService.generarToken(usuario);
    }
}

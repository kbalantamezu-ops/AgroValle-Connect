package com.agrovalle.agrovalle_connect.auth;

import com.agrovalle.agrovalle_connect.agricultores.AgricultorResponse;
import com.agrovalle.agrovalle_connect.agricultores.AgricultorService;
import com.agrovalle.agrovalle_connect.comun.ApiException;
import com.agrovalle.agrovalle_connect.usuarios.Rol;
import com.agrovalle.agrovalle_connect.usuarios.Usuario;
import com.agrovalle.agrovalle_connect.usuarios.UsuarioRepository;
import com.agrovalle.agrovalle_connect.usuarios.UsuarioRequest;
import com.agrovalle.agrovalle_connect.usuarios.UsuarioResponse;
import com.agrovalle.agrovalle_connect.usuarios.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Inicio de sesion y registro de cuentas. */
@Service
public class AuthService {

    private static final String CREDENCIALES_INVALIDAS = "Correo o contrasena incorrectos";

    private final UsuarioService usuarioService;

    private final UsuarioRepository usuarioRepository;

    private final AgricultorService agricultorService;

    private final JwtService jwtService;

    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioService usuarioService, UsuarioRepository usuarioRepository,
            AgricultorService agricultorService, JwtService jwtService,
            PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.usuarioRepository = usuarioRepository;
        this.agricultorService = agricultorService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioService.buscarPorEmail(request.email())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS));
        if (!passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, CREDENCIALES_INVALIDAS);
        }
        return new LoginResponse(jwtService.generarToken(usuario), "Bearer",
                jwtService.expiracionEnSegundos(), usuarioService.aResponse(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioResponse cuentaActual(UsuarioPrincipal principal) {
        Usuario usuario = usuarioRepository.findById(principal.id())
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,
                        "La cuenta ya no existe"));
        return usuarioService.aResponse(usuario);
    }

    @Transactional
    public UsuarioResponse registrarComprador(UsuarioRequest request) {
        return usuarioService.registrar(request);
    }

    /** Crea el perfil del agricultor y su cuenta de acceso en la misma transaccion. */
    @Transactional
    public UsuarioResponse registrarAgricultor(RegistroAgricultorRequest request) {
        AgricultorResponse agricultor = agricultorService.registrar(request.aAgricultorRequest());
        return usuarioService.registrarCuenta(request.nombre(), request.email(),
                request.password(), request.telefono(), Rol.AGRICULTOR, agricultor.id());
    }
}

package com.agrovalle.agrovalle_connect.auth;

import com.agrovalle.agrovalle_connect.usuarios.UsuarioRequest;
import com.agrovalle.agrovalle_connect.usuarios.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public UsuarioResponse cuentaActual(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return authService.cuentaActual(principal);
    }

    @PostMapping("/registro/comprador")
    public ResponseEntity<UsuarioResponse> registrarComprador(
            @Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registrarComprador(request));
    }

    @PostMapping("/registro/agricultor")
    public ResponseEntity<UsuarioResponse> registrarAgricultor(
            @Valid @RequestBody RegistroAgricultorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registrarAgricultor(request));
    }
}

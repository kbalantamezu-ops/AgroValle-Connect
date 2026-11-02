package com.agrovalle.agrovalle_connect.usuarios;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

  private final UsuarioService servicio;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public UsuarioResponse registrar(@Valid @RequestBody UsuarioRequest request) {
    return servicio.registrar(request);
  }
}

package com.agrovalle.agrovalle_connect.usuarios;

import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

  public Usuario toEntity(UsuarioRequest request, String passwordHash) {
    return Usuario.builder()
        .nombre(request.nombre())
        .email(request.email())
        .passwordHash(passwordHash)
        .telefono(request.telefono())
        .build();
  }

  public UsuarioResponse toResponse(Usuario usuario) {
    return new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail(), usuario.getTelefono());
  }
}

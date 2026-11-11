package com.agrovalle.agrovalle_connect.usuarios;

import com.agrovalle.agrovalle_connect.excepciones.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

  private final UsuarioRepository repositorio;

  private final UsuarioMapper mapper;

  private final PasswordEncoder passwordEncoder;

  /** HU-12: registra un usuario comprador validando que el correo no exista. */
  @Transactional
  public UsuarioResponse registrar(UsuarioRequest request) {
    if (repositorio.existsByEmail(request.email())) {
      throw new ReglaNegocioException("Ya existe un usuario con ese correo");
    }
    String hash = passwordEncoder.encode(request.password());
    Usuario guardado = repositorio.save(mapper.toEntity(request, hash));
    return mapper.toResponse(guardado);
  }
}

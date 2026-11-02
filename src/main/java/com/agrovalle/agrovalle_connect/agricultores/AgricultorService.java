package com.agrovalle.agrovalle_connect.agricultores;

import com.agrovalle.agrovalle_connect.excepciones.ReglaNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgricultorService {

  private final AgricultorRepository repositorio;
  private final AgricultorMapper mapper;
  private final PasswordEncoder passwordEncoder;

  /** HU-01: registra un agricultor validando que cédula y correo no existan. */
  @Transactional
  public AgricultorResponse registrar(AgricultorRequest request) {
    if (repositorio.existsByCedula(request.cedula())) {
      throw new ReglaNegocioException("Ya existe un agricultor con esa cédula");
    }
    if (repositorio.existsByEmail(request.email())) {
      throw new ReglaNegocioException("Ya existe un agricultor con ese correo");
    }
    String hash = passwordEncoder.encode(request.password());
    Agricultor guardado = repositorio.save(mapper.toEntity(request, hash));
    return mapper.toResponse(guardado);
  }
}

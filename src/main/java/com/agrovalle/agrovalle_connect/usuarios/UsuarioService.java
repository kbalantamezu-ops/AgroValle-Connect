package com.agrovalle.agrovalle_connect.usuarios;

import com.agrovalle.agrovalle_connect.excepciones.ReglaNegocioException;
import java.util.Optional;
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
    return registrarCuenta(request.nombre(), request.email(), request.password(),
        request.telefono(), Rol.COMPRADOR, null);
  }

  /** Crea una cuenta con el rol indicado y, opcionalmente, la vincula a un agricultor. */
  @Transactional
  public UsuarioResponse registrarCuenta(String nombre, String email, String password,
      String telefono, Rol rol, Long agricultorId) {
    String correo = normalizar(email);
    if (repositorio.existsByEmail(correo)) {
      throw new ReglaNegocioException("Ya existe un usuario con ese correo");
    }
    if (agricultorId != null && repositorio.existsByAgricultorId(agricultorId)) {
      throw new ReglaNegocioException("Ese agricultor ya tiene una cuenta asociada");
    }
    String hash = passwordEncoder.encode(password);
    Usuario usuario = Usuario.builder()
        .nombre(nombre)
        .email(correo)
        .passwordHash(hash)
        .telefono(telefono)
        .rol(rol)
        .agricultorId(agricultorId)
        .build();
    return mapper.toResponse(repositorio.save(usuario));
  }

  @Transactional(readOnly = true)
  public Optional<Usuario> buscarPorEmail(String email) {
    return repositorio.findByEmail(normalizar(email));
  }

  public UsuarioResponse aResponse(Usuario usuario) {
    return mapper.toResponse(usuario);
  }

  private String normalizar(String email) {
    return email == null ? null : email.trim().toLowerCase();
  }
}

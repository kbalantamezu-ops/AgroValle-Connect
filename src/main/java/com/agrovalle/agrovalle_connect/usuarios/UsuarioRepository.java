package com.agrovalle.agrovalle_connect.usuarios;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  boolean existsByEmail(String email);

  boolean existsByAgricultorId(Long agricultorId);

  Optional<Usuario> findByEmail(String email);
}

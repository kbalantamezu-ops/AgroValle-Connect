package com.agrovalle.agrovalle_connect.usuarios;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

  boolean existsByEmail(String email);
}

package com.agrovalle.agrovalle_connect.agricultores;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgricultorRepository extends JpaRepository<Agricultor, Long> {

<<<<<<< HEAD
    boolean existsByCedula(String cedula);
=======
  boolean existsByCedula(String cedula);

  boolean existsByEmail(String email);
>>>>>>> origin/develop
}

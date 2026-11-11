package com.agrovalle.agrovalle_connect.agricultores;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AgricultorRepository extends JpaRepository<Agricultor, Long> {

    boolean existsByCedula(String cedula);
}

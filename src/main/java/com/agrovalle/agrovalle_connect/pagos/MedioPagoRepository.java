package com.agrovalle.agrovalle_connect.pagos;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedioPagoRepository extends JpaRepository<MedioPago, Long> {

    Optional<MedioPago> findByNombre(String nombre);
}

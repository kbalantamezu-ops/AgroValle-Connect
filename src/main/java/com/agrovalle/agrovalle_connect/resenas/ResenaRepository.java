package com.agrovalle.agrovalle_connect.resenas;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByAgricultorIdOrderByFechaCreacionDesc(Long agricultorId);

    @Query("select avg(r.calificacion) from Resena r where r.agricultor.id = :agricultorId")
    Optional<Double> promedioCalificacion(@Param("agricultorId") Long agricultorId);
}

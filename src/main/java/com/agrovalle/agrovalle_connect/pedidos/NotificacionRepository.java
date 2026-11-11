package com.agrovalle.agrovalle_connect.pedidos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    List<Notificacion> findByPedidoIdOrderByFechaCreacionDesc(Long pedidoId);
}

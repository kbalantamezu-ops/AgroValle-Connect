package com.agrovalle.agrovalle_connect.pedidos;

import java.time.LocalDateTime;

public record NotificacionResponse(
        Long id,
        Long pedidoId,
        String mensaje,
        LocalDateTime fechaCreacion) {
}

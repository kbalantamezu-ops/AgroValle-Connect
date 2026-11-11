package com.agrovalle.agrovalle_connect.pedidos;

import jakarta.validation.constraints.NotNull;

public record EstadoRequest(
        @NotNull(message = "El estado es obligatorio")
        EstadoPedido estado) {
}

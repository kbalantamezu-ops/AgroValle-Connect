package com.agrovalle.agrovalle_connect.pedidos;

import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PedidoRequest(
        @NotBlank(message = "El nombre del comprador es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String compradorNombre,
        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String compradorTelefono,
        @NotNull(message = "El medio de pago es obligatorio")
        Long medioPagoId,
        Municipio municipioDestino,
        @NotEmpty(message = "El pedido debe incluir al menos un producto")
        List<@Valid ItemPedidoRequest> items) {
}

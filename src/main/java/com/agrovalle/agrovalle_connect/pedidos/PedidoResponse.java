package com.agrovalle.agrovalle_connect.pedidos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        String compradorNombre,
        String compradorTelefono,
        PagoResponse pago,
        String estado,
        LocalDateTime fechaCreacion,
        BigDecimal total,
        List<ItemResponse> items) {

    public record PagoResponse(
            String medioPago,
            BigDecimal monto,
            String estado,
            LocalDateTime fechaPago) {
    }

    public record ItemResponse(
            String productoNombre,
            String categoria,
            String municipio,
            Double cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal) {
    }
}

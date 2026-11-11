package com.agrovalle.agrovalle_connect.agricultores;

import com.agrovalle.agrovalle_connect.productos.ProductoResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Resumen de la actividad de un agricultor para la HU-15. */
public record PanelAgricultorResponse(
        AgricultorResponse agricultor,
        List<ProductoResponse> productos,
        List<PedidoResumen> pedidos,
        long pedidosRecibidos,
        BigDecimal ventasTotales) {

    public record PedidoResumen(
            Long id,
            String compradorNombre,
            String estado,
            LocalDateTime fechaCreacion,
            BigDecimal totalPedido,
            BigDecimal totalAgricultor,
            List<ItemResumen> items) {
    }

    public record ItemResumen(String producto, Double cantidad, String unidad, BigDecimal subtotal) {
    }
}

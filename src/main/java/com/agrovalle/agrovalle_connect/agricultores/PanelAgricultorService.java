package com.agrovalle.agrovalle_connect.agricultores;

import com.agrovalle.agrovalle_connect.pedidos.ItemPedido;
import com.agrovalle.agrovalle_connect.pedidos.Pedido;
import com.agrovalle.agrovalle_connect.pedidos.PedidoRepository;
import com.agrovalle.agrovalle_connect.productos.ProductoResponse;
import com.agrovalle.agrovalle_connect.productos.ProductoService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** HU-15: consolida publicaciones, pedidos y ventas de un agricultor. */
@Service
public class PanelAgricultorService {

    private final AgricultorService agricultorService;

    private final ProductoService productoService;

    private final PedidoRepository pedidoRepository;

    public PanelAgricultorService(AgricultorService agricultorService, ProductoService productoService,
            PedidoRepository pedidoRepository) {
        this.agricultorService = agricultorService;
        this.productoService = productoService;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public PanelAgricultorResponse obtener(Long agricultorId) {
        AgricultorResponse agricultor = agricultorService.obtener(agricultorId);
        List<ProductoResponse> productos = productoService.listarPorAgricultor(agricultorId);
        List<PanelAgricultorResponse.PedidoResumen> pedidos = new ArrayList<>();
        BigDecimal ventasTotales = BigDecimal.ZERO;

        for (Pedido pedido : pedidoRepository.buscarTodosConItems()) {
            List<PanelAgricultorResponse.ItemResumen> items = pedido.getItems().stream()
                    .filter(item -> item.getProducto().getAgricultor().getId().equals(agricultorId))
                    .map(this::toItemResumen)
                    .toList();
            if (items.isEmpty()) {
                continue;
            }
            BigDecimal totalAgricultor = items.stream()
                    .map(PanelAgricultorResponse.ItemResumen::subtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            ventasTotales = ventasTotales.add(totalAgricultor);
            pedidos.add(new PanelAgricultorResponse.PedidoResumen(pedido.getId(),
                    pedido.getCompradorNombre(), pedido.getEstado().name(), pedido.getFechaCreacion(),
                    pedido.getTotal(), totalAgricultor, items));
        }

        return new PanelAgricultorResponse(agricultor, productos, pedidos, pedidos.size(), ventasTotales);
    }

    private PanelAgricultorResponse.ItemResumen toItemResumen(ItemPedido item) {
        return new PanelAgricultorResponse.ItemResumen(item.getProducto().getNombre(),
                item.getCantidad(), item.getProducto().getUnidad(), item.getSubtotal());
    }
}

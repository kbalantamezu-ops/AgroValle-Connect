package com.agrovalle.agrovalle_connect.pedidos;

import com.agrovalle.agrovalle_connect.comun.ApiException;
import com.agrovalle.agrovalle_connect.pagos.MedioPago;
import com.agrovalle.agrovalle_connect.pagos.MedioPagoRepository;
import com.agrovalle.agrovalle_connect.pagos.Pago;
import com.agrovalle.agrovalle_connect.productos.Producto;
import com.agrovalle.agrovalle_connect.productos.ProductoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    private final ProductoRepository productoRepository;

    private final NotificacionRepository notificacionRepository;

    private final MedioPagoRepository medioPagoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
            NotificacionRepository notificacionRepository, MedioPagoRepository medioPagoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.notificacionRepository = notificacionRepository;
        this.medioPagoRepository = medioPagoRepository;
    }

    @Transactional
    public PedidoResponse crear(PedidoRequest request) {
        MedioPago medioPago = medioPagoRepository.findById(request.medioPagoId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                        "Medio de pago no encontrado con id " + request.medioPagoId()));
        Pedido pedido = new Pedido();
        pedido.setCompradorNombre(request.compradorNombre());
        pedido.setCompradorTelefono(request.compradorTelefono());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedidoRequest itemRequest : request.items()) {
            Producto producto = productoRepository.findById(itemRequest.productoId())
                    .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                            "Producto no encontrado con id " + itemRequest.productoId()));
            if (!producto.isDisponible() || itemRequest.cantidad() > producto.getCantidadDisponible()) {
                throw new ApiException(HttpStatus.CONFLICT,
                        "Stock insuficiente del producto '" + producto.getNombre()
                                + "': disponible " + producto.getCantidadDisponible() + " "
                                + producto.getUnidad());
            }
            BigDecimal subtotal = producto.getPrecioUnitario()
                    .multiply(BigDecimal.valueOf(itemRequest.cantidad()));
            ItemPedido item = new ItemPedido();
            item.setProducto(producto);
            item.setCantidad(itemRequest.cantidad());
            item.setPrecioUnitario(producto.getPrecioUnitario());
            item.setSubtotal(subtotal);
            pedido.agregarItem(item);
            total = total.add(subtotal);

            double nuevoStock = producto.getCantidadDisponible() - itemRequest.cantidad();
            producto.setCantidadDisponible(nuevoStock);
            if (nuevoStock <= 0) {
                producto.setDisponible(false);
            }
        }
        pedido.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        Pago pago = new Pago();
        pago.setMedioPago(medioPago);
        pago.setMonto(pedido.getTotal());
        pago.setEstado("PAGADO");
        pago.setFechaPago(LocalDateTime.now());
        pedido.asignarPago(pago);
        Pedido guardado = pedidoRepository.save(pedido);
        notificacionRepository.save(new Notificacion(guardado.getId(), "Pedido creado con estado PENDIENTE"));
        return toResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return pedidoRepository.buscarTodosConItems().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional
    public PedidoResponse cambiarEstado(Long id, EstadoRequest request) {
        Pedido pedido = buscarEntidad(id);
        if (pedido.getEstado() == EstadoPedido.CANCELADO || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "El pedido ya está " + pedido.getEstado() + " y no puede cambiar de estado");
        }
        pedido.setEstado(request.estado());
        notificacionRepository.save(new Notificacion(pedido.getId(),
                "El estado del pedido cambió a " + request.estado()));
        return toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarNotificaciones(Long id) {
        buscarEntidad(id);
        return notificacionRepository.findByPedidoIdOrderByFechaCreacionDesc(id).stream()
                .map(n -> new NotificacionResponse(n.getId(), n.getPedidoId(), n.getMensaje(),
                        n.getFechaCreacion()))
                .toList();
    }

    private Pedido buscarEntidad(Long id) {
        return pedidoRepository.buscarPorIdConItems(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pedido no encontrado con id " + id));
    }

    private PedidoResponse toResponse(Pedido pedido) {
        List<PedidoResponse.ItemResponse> items = pedido.getItems().stream()
                .map(i -> new PedidoResponse.ItemResponse(
                        i.getProducto().getNombre(),
                        i.getProducto().getCategoria().name(),
                        i.getProducto().getAgricultor().getMunicipio().name(),
                        i.getCantidad(),
                        i.getPrecioUnitario(),
                        i.getSubtotal()))
                .toList();
        Pago pago = pedido.getPago();
        PedidoResponse.PagoResponse pagoResponse = pago == null ? null
                : new PedidoResponse.PagoResponse(pago.getMedioPago().getNombre(), pago.getMonto(),
                        pago.getEstado(), pago.getFechaPago());
        return new PedidoResponse(pedido.getId(), pedido.getCompradorNombre(), pedido.getCompradorTelefono(),
                pagoResponse, pedido.getEstado().name(), pedido.getFechaCreacion(), pedido.getTotal(),
                items);
    }
}

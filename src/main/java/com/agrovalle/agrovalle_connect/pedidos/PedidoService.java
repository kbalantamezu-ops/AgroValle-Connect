package com.agrovalle.agrovalle_connect.pedidos;

import com.agrovalle.agrovalle_connect.auth.AutorizacionService;
import com.agrovalle.agrovalle_connect.auth.UsuarioPrincipal;
import com.agrovalle.agrovalle_connect.comun.ApiException;
import com.agrovalle.agrovalle_connect.fletes.FleteRequest;
import com.agrovalle.agrovalle_connect.fletes.FleteResponse;
import com.agrovalle.agrovalle_connect.fletes.FleteService;
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

    private static final BigDecimal FLETE_SIN_COSTO = new BigDecimal("0.00");

    private final PedidoRepository pedidoRepository;

    private final ProductoRepository productoRepository;

    private final NotificacionRepository notificacionRepository;

    private final MedioPagoRepository medioPagoRepository;

    private final AutorizacionService autorizacionService;

    private final FleteService fleteService;

    public PedidoService(PedidoRepository pedidoRepository, ProductoRepository productoRepository,
            NotificacionRepository notificacionRepository, MedioPagoRepository medioPagoRepository,
            AutorizacionService autorizacionService, FleteService fleteService) {
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
        this.notificacionRepository = notificacionRepository;
        this.medioPagoRepository = medioPagoRepository;
        this.autorizacionService = autorizacionService;
        this.fleteService = fleteService;
    }

    @Transactional
    public PedidoResponse crear(PedidoRequest request, UsuarioPrincipal principal) {
        autorizacionService.exigirAutenticado(principal);
        MedioPago medioPago = medioPagoRepository.findById(request.medioPagoId())
                .orElseThrow(() -> new ApiException(HttpStatus.BAD_REQUEST,
                        "Medio de pago no encontrado con id " + request.medioPagoId()));
        Pedido pedido = new Pedido();
        pedido.setCompradorNombre(request.compradorNombre());
        pedido.setCompradorTelefono(request.compradorTelefono());
        pedido.setUsuarioId(principal.id());
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(LocalDateTime.now());

        BigDecimal totalProductos = BigDecimal.ZERO;
        double pesoTotal = 0;
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
            totalProductos = totalProductos.add(subtotal);
            pesoTotal += itemRequest.cantidad();

            double nuevoStock = producto.getCantidadDisponible() - itemRequest.cantidad();
            producto.setCantidadDisponible(nuevoStock);
            if (nuevoStock <= 0) {
                producto.setDisponible(false);
            }
        }
        aplicarFlete(pedido, request, pesoTotal);
        pedido.setTotal(totalProductos.add(pedido.getFleteCosto()).setScale(2, RoundingMode.HALF_UP));
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
    public List<PedidoResponse> listar(UsuarioPrincipal principal) {
        autorizacionService.exigirAutenticado(principal);
        List<Pedido> pedidos;
        if (principal.esAdmin()) {
            pedidos = pedidoRepository.buscarTodosConItems();
        } else if (principal.esAgricultor()) {
            pedidos = principal.agricultorId() == null ? List.of()
                    : pedidoRepository.buscarPorAgricultorConItems(principal.agricultorId());
        } else {
            pedidos = pedidoRepository.buscarPorUsuarioConItems(principal.id());
        }
        return pedidos.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse obtener(Long id, UsuarioPrincipal principal) {
        Pedido pedido = buscarEntidad(id);
        autorizacionService.exigirAccesoPedido(principal, pedido);
        return toResponse(pedido);
    }

    @Transactional
    public PedidoResponse cambiarEstado(Long id, EstadoRequest request, UsuarioPrincipal principal) {
        Pedido pedido = buscarEntidad(id);
        autorizacionService.exigirGestionPedido(principal, pedido);
        if (pedido.getEstado() == EstadoPedido.CANCELADO || pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "El pedido ya esta " + pedido.getEstado() + " y no puede cambiar de estado");
        }
        pedido.setEstado(request.estado());
        if (request.estado() == EstadoPedido.CANCELADO) {
            reponerStock(pedido);
            notificacionRepository.save(new Notificacion(pedido.getId(),
                    "El pedido fue cancelado y el stock fue repuesto"));
        } else {
            notificacionRepository.save(new Notificacion(pedido.getId(),
                    "El estado del pedido cambio a " + request.estado()));
        }
        return toResponse(pedido);
    }

    @Transactional(readOnly = true)
    public List<NotificacionResponse> listarNotificaciones(Long id, UsuarioPrincipal principal) {
        Pedido pedido = buscarEntidad(id);
        autorizacionService.exigirAccesoPedido(principal, pedido);
        return notificacionRepository.findByPedidoIdOrderByFechaCreacionDesc(id).stream()
                .map(n -> new NotificacionResponse(n.getId(), n.getPedidoId(), n.getMensaje(),
                        n.getFechaCreacion()))
                .toList();
    }

    private void aplicarFlete(Pedido pedido, PedidoRequest request, double pesoTotal) {
        if (request.municipioDestino() == null) {
            pedido.setFleteCosto(FLETE_SIN_COSTO);
            return;
        }
        FleteResponse cotizacion = fleteService.calcular(new FleteRequest(
                request.municipioDestino().name(), BigDecimal.valueOf(pesoTotal)));
        pedido.setMunicipioDestino(request.municipioDestino().name());
        pedido.setFleteZona(cotizacion.zona());
        pedido.setFleteCosto(cotizacion.costo());
    }

    private void reponerStock(Pedido pedido) {
        for (ItemPedido item : pedido.getItems()) {
            Producto producto = item.getProducto();
            producto.setCantidadDisponible(producto.getCantidadDisponible() + item.getCantidad());
            producto.setDisponible(true);
        }
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
        BigDecimal flete = pedido.getFleteCosto() == null ? FLETE_SIN_COSTO : pedido.getFleteCosto();
        return new PedidoResponse(pedido.getId(), pedido.getCompradorNombre(), pedido.getCompradorTelefono(),
                pagoResponse, pedido.getEstado().name(), pedido.getFechaCreacion(), pedido.getTotal(),
                flete, pedido.getFleteZona(), pedido.getMunicipioDestino(), items);
    }
}

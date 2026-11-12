package com.agrovalle.agrovalle_connect.pedidos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import com.agrovalle.agrovalle_connect.auth.AutorizacionService;
import com.agrovalle.agrovalle_connect.auth.UsuarioPrincipal;
import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import com.agrovalle.agrovalle_connect.comun.ApiException;
import com.agrovalle.agrovalle_connect.pagos.MedioPago;
import com.agrovalle.agrovalle_connect.pagos.MedioPagoRepository;
import com.agrovalle.agrovalle_connect.productos.Producto;
import com.agrovalle.agrovalle_connect.productos.ProductoRepository;
import com.agrovalle.agrovalle_connect.usuarios.Rol;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PedidoServiceTest {

    private PedidoRepository pedidoRepository;

    private ProductoRepository productoRepository;

    private NotificacionRepository notificacionRepository;

    private MedioPagoRepository medioPagoRepository;

    private AutorizacionService autorizacionService;

    private PedidoService pedidoService;

    private UsuarioPrincipal comprador;

    @BeforeEach
    void configurar() {
        pedidoRepository = mock(PedidoRepository.class);
        productoRepository = mock(ProductoRepository.class);
        notificacionRepository = mock(NotificacionRepository.class);
        medioPagoRepository = mock(MedioPagoRepository.class);
        autorizacionService = mock(AutorizacionService.class);
        comprador = new UsuarioPrincipal(10L, "comprador@test.co", Rol.COMPRADOR, null);
        when(medioPagoRepository.findById(1L)).thenReturn(Optional.of(new MedioPago("EFECTIVO")));
        pedidoService = new PedidoService(pedidoRepository, productoRepository, notificacionRepository,
                medioPagoRepository, autorizacionService);
    }

    @Test
    void crearConStockInsuficienteLanzaConflictSinGuardar() {
        Producto producto = productoConStock(10);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        PedidoRequest request = new PedidoRequest("Comprador", null, 1L,
                List.of(new ItemPedidoRequest(1L, 15.0)));

        ApiException ex = assertThrows(ApiException.class, () -> pedidoService.crear(request, comprador));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(pedidoRepository, never()).save(any());
        assertEquals(10, producto.getCantidadDisponible());
    }

    @Test
    void crearConProductoNoDisponibleLanzaConflict() {
        Producto producto = productoConStock(100);
        producto.setDisponible(false);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        PedidoRequest request = new PedidoRequest("Comprador", null, 1L,
                List.of(new ItemPedidoRequest(1L, 2.0)));

        ApiException ex = assertThrows(ApiException.class, () -> pedidoService.crear(request, comprador));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
    }

    @Test
    void crearConProductoInexistenteLanzaNotFound() {
        when(productoRepository.findById(999L)).thenReturn(Optional.empty());
        PedidoRequest request = new PedidoRequest("Comprador", null, 1L,
                List.of(new ItemPedidoRequest(999L, 2.0)));

        ApiException ex = assertThrows(ApiException.class, () -> pedidoService.crear(request, comprador));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    @Test
    void crearConMedioPagoInexistenteLanzaBadRequestSinGuardar() {
        when(medioPagoRepository.findById(999L)).thenReturn(Optional.empty());
        PedidoRequest request = new PedidoRequest("Comprador", null, 999L,
                List.of(new ItemPedidoRequest(1L, 2.0)));

        ApiException ex = assertThrows(ApiException.class, () -> pedidoService.crear(request, comprador));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void crearDescuentaStockMarcaAgotadoYNotifica() {
        Producto producto = productoConStock(5);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(pedidoRepository.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
        PedidoRequest request = new PedidoRequest("Comprador", null, 1L,
                List.of(new ItemPedidoRequest(1L, 5.0)));

        PedidoResponse respuesta = pedidoService.crear(request, comprador);

        assertEquals(0, producto.getCantidadDisponible());
        assertFalse(producto.isDisponible());
        assertEquals(EstadoPedido.PENDIENTE.name(), respuesta.estado());
        assertEquals(new BigDecimal("500.00"), respuesta.total());
        assertEquals("EFECTIVO", respuesta.pago().medioPago());
        assertEquals(new BigDecimal("500.00"), respuesta.pago().monto());
        assertEquals("PAGADO", respuesta.pago().estado());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    @Test
    void cambiarEstadoDePedidoTerminalLanzaConflict() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.ENTREGADO);
        when(pedidoRepository.buscarPorIdConItems(1L)).thenReturn(Optional.of(pedido));

        ApiException ex = assertThrows(ApiException.class,
                () -> pedidoService.cambiarEstado(1L, new EstadoRequest(EstadoPedido.ENVIADO), comprador));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());
        verify(notificacionRepository, never()).save(any());
    }

    @Test
    void cambiarEstadoGeneraNotificacion() {
        Pedido pedido = new Pedido();
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setFechaCreacion(java.time.LocalDateTime.now());
        pedido.setTotal(BigDecimal.ZERO);
        when(pedidoRepository.buscarPorIdConItems(1L)).thenReturn(Optional.of(pedido));

        PedidoResponse respuesta = pedidoService.cambiarEstado(1L, new EstadoRequest(EstadoPedido.ENVIADO), comprador);

        assertEquals("ENVIADO", respuesta.estado());
        verify(notificacionRepository).save(any(Notificacion.class));
    }

    private Producto productoConStock(double stock) {
        Producto producto = new Producto();
        producto.setNombre("Producto Test");
        producto.setCategoria(com.agrovalle.agrovalle_connect.catalogo.Categoria.TUBERCULOS);
        producto.setCantidadDisponible(stock);
        producto.setPrecioUnitario(new BigDecimal("100.00"));
        producto.setUnidad("kg");
        producto.setDisponible(true);
        producto.setAgricultor(new Agricultor("Agricultor Test", "123", Municipio.CALI, null, null, null));
        return producto;
    }
}

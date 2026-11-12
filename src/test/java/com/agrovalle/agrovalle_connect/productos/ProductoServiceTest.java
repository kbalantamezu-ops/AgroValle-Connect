package com.agrovalle.agrovalle_connect.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import com.agrovalle.agrovalle_connect.agricultores.AgricultorService;
import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import com.agrovalle.agrovalle_connect.comun.ApiException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private AgricultorService agricultorService;

    @InjectMocks
    private ProductoService servicio;

    private Agricultor crearAgricultor() {
        Agricultor agricultor = new Agricultor("María Ospina", "1111111111", Municipio.DAGUA,
                "3125550101", "maria@finca.co", "Finca El Porvenir");
        ReflectionTestUtils.setField(agricultor, "id", 1L);
        return agricultor;
    }

    private ProductoRequest crearRequest() {
        return new ProductoRequest("Tomate chonto", Categoria.VERDURAS, 40.0,
                new BigDecimal("2500.00"), "kg", LocalDate.now().plusDays(3), 1L);
    }

    private Producto crearProducto() {
        Producto producto = new Producto();
        ReflectionTestUtils.setField(producto, "id", 20L);
        producto.setNombre("Producto original");
        producto.setCategoria(Categoria.FRUTAS);
        producto.setCantidadDisponible(10.0);
        producto.setPrecioUnitario(new BigDecimal("1000.00"));
        producto.setUnidad("kg");
        producto.setFechaCosecha(LocalDate.now().plusDays(2));
        producto.setFechaPublicacion(LocalDateTime.now().minusDays(1));
        producto.setDisponible(true);
        producto.setAgricultor(crearAgricultor());
        return producto;
    }

    private ProductoUpdateRequest crearRequestActualizado() {
        return new ProductoUpdateRequest("Tomate actualizado", Categoria.VERDURAS, 25.0,
                new BigDecimal("3200.00"), "caja", LocalDate.now().plusDays(4));
    }

    @Test
    void publicarGuardaProductoYDevuelveRespuesta() {
        when(agricultorService.buscarEntidad(1L)).thenReturn(crearAgricultor());
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> {
            Producto producto = inv.getArgument(0);
            ReflectionTestUtils.setField(producto, "id", 10L);
            return producto;
        });

        ProductoResponse respuesta = servicio.publicar(crearRequest());

        assertEquals(10L, respuesta.id());
        assertEquals("Tomate chonto", respuesta.nombre());
        assertEquals("VERDURAS", respuesta.categoria());
        assertEquals("DAGUA", respuesta.municipio());
        assertEquals(new BigDecimal("2500.00"), respuesta.precioUnitario());
        assertEquals("Finca El Porvenir", respuesta.agricultor().finca());
        assertTrue(respuesta.disponible());
    }

    @Test
    void publicarFallaSiElAgricultorNoExiste() {
        when(agricultorService.buscarEntidad(1L))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "no existe"));

        assertThrows(ApiException.class, () -> servicio.publicar(crearRequest()));

        verify(productoRepository, never()).save(any());
    }

    @Test
    void obtenerProductoInexistenteLanza404() {
        when(productoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        ApiException error = assertThrows(ApiException.class, () -> servicio.obtener(99L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
    }

    @Test
    void actualizarModificaDatosYConservaPropietarioYPublicacion() {
        Producto producto = crearProducto();
        LocalDateTime publicacion = producto.getFechaPublicacion();
        when(productoRepository.buscarPorId(20L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse respuesta = servicio.actualizar(20L, crearRequestActualizado());

        assertEquals("Tomate actualizado", respuesta.nombre());
        assertEquals("VERDURAS", respuesta.categoria());
        assertEquals(25.0, respuesta.cantidadDisponible());
        assertEquals(new BigDecimal("3200.00"), respuesta.precioUnitario());
        assertEquals("caja", respuesta.unidad());
        assertEquals(1L, respuesta.agricultor().id());
        assertEquals(publicacion, respuesta.fechaPublicacion());
        assertTrue(respuesta.disponible());
    }

    @Test
    void actualizarProductoInexistenteLanza404() {
        when(productoRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        ApiException error = assertThrows(ApiException.class,
                () -> servicio.actualizar(99L, crearRequestActualizado()));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
        verify(productoRepository, never()).save(any());
    }

    @Test
    void actualizarCantidadCeroMarcaElProductoComoAgotado() {
        Producto producto = crearProducto();
        when(productoRepository.buscarPorId(20L)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProductoResponse respuesta = servicio.actualizar(20L, new ProductoUpdateRequest(
                "Producto sin stock", Categoria.FRUTAS, 0.0, new BigDecimal("1000.00"),
                "kg", LocalDate.now().plusDays(2)));

        assertEquals(0.0, respuesta.cantidadDisponible());
        assertFalse(respuesta.disponible());
    }

    @Test
    void eliminarSinPedidosAsociadosEliminaElProducto() {
        Producto producto = crearProducto();
        when(productoRepository.buscarPorId(20L)).thenReturn(Optional.of(producto));
        when(productoRepository.contarPedidosAsociados(20L)).thenReturn(0L);

        servicio.eliminar(20L);

        verify(productoRepository).delete(producto);
    }

    @Test
    void eliminarConPedidosAsociadosLanza409() {
        Producto producto = crearProducto();
        when(productoRepository.buscarPorId(20L)).thenReturn(Optional.of(producto));
        when(productoRepository.contarPedidosAsociados(20L)).thenReturn(2L);

        ApiException error = assertThrows(ApiException.class, () -> servicio.eliminar(20L));

        assertEquals(HttpStatus.CONFLICT, error.getStatus());
        assertTrue(error.getMessage().contains("pedidos asociados"));
        verify(productoRepository, never()).delete(any());
    }
}

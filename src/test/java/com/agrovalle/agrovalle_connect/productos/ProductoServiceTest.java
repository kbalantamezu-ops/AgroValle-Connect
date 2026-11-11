package com.agrovalle.agrovalle_connect.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}

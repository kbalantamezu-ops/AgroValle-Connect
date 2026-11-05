package com.agrovalle.agrovalle_connect.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import com.agrovalle.agrovalle_connect.comun.ApiException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class PrecioServiceTest {

    private ProductoRepository productoRepository;

    private PrecioService precioService;

    @BeforeEach
    void configurar() {
        productoRepository = mock(ProductoRepository.class);
        precioService = new PrecioService(productoRepository);
    }

    @Test
    void promedioCalculaLaMediaAritmetica() {
        when(productoRepository.findByCategoria(Categoria.LACTEOS))
                .thenReturn(List.of(productoConPrecio("1000"), productoConPrecio("2000")));

        PrecioPromedioResponse respuesta = precioService.promedioPorCategoria(Categoria.LACTEOS);

        assertEquals(new BigDecimal("1500.00"), respuesta.promedio());
        assertEquals(2, respuesta.cantidadProductos());
        assertEquals("COP", respuesta.moneda());
    }

    @Test
    void promedioRedondeaADosDecimales() {
        when(productoRepository.findByCategoria(Categoria.GRANOS))
                .thenReturn(List.of(productoConPrecio("1000"), productoConPrecio("2000"),
                        productoConPrecio("2000")));

        PrecioPromedioResponse respuesta = precioService.promedioPorCategoria(Categoria.GRANOS);

        assertEquals(new BigDecimal("1666.67"), respuesta.promedio());
    }

    @Test
    void promedioSinProductosLanzaNotFound() {
        when(productoRepository.findByCategoria(Categoria.OTROS)).thenReturn(List.of());

        ApiException ex = assertThrows(ApiException.class,
                () -> precioService.promedioPorCategoria(Categoria.OTROS));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
    }

    private Producto productoConPrecio(String precio) {
        Producto producto = new Producto();
        producto.setPrecioUnitario(new BigDecimal(precio));
        return producto;
    }
}

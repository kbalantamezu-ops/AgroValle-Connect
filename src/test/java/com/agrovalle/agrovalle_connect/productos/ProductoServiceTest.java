package com.agrovalle.agrovalle_connect.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import com.agrovalle.agrovalle_connect.agricultores.AgricultorRepository;
import com.agrovalle.agrovalle_connect.excepciones.RecursoNoEncontradoException;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

  @Mock
  private ProductoRepository productoRepositorio;

  @Mock
  private AgricultorRepository agricultorRepositorio;

  @Mock
  private CategoriaRepository categoriaRepositorio;

  @Spy
  private ProductoMapper mapper = new ProductoMapper();

  @InjectMocks
  private ProductoService servicio;

  private ProductoRequest crearRequest() {
    return new ProductoRequest(1L, 2L, "Tomate chonto", "Cosecha fresca", new BigDecimal("2500.00"), 40, "kg",
        "https://fotos.com/tomate.jpg");
  }

  @Test
  void publicarGuardaProductoConFotoYDevuelveRespuesta() {
    Agricultor agricultor = Agricultor.builder().id(1L).build();
    Categoria categoria = Categoria.builder().id(2L).nombre("Verduras").build();
    when(agricultorRepositorio.findById(1L)).thenReturn(Optional.of(agricultor));
    when(categoriaRepositorio.findById(2L)).thenReturn(Optional.of(categoria));
    when(productoRepositorio.save(any(Producto.class))).thenAnswer(inv -> {
      Producto p = inv.getArgument(0);
      p.setId(10L);
      return p;
    });

    ProductoResponse respuesta = servicio.publicar(crearRequest());

    assertEquals(10L, respuesta.id());
    assertEquals(1L, respuesta.agricultorId());
    assertEquals(2L, respuesta.categoriaId());
    assertEquals("https://fotos.com/tomate.jpg", respuesta.fotoUrl());
    assertEquals(new BigDecimal("2500.00"), respuesta.precio());
  }

  @Test
  void publicarFallaSiElAgricultorNoExiste() {
    when(agricultorRepositorio.findById(1L)).thenReturn(Optional.empty());

    assertThrows(RecursoNoEncontradoException.class, () -> servicio.publicar(crearRequest()));

    verify(productoRepositorio, never()).save(any());
  }

  @Test
  void publicarFallaSiLaCategoriaNoExiste() {
    when(agricultorRepositorio.findById(1L)).thenReturn(Optional.of(Agricultor.builder().id(1L).build()));
    when(categoriaRepositorio.findById(2L)).thenReturn(Optional.empty());

    assertThrows(RecursoNoEncontradoException.class, () -> servicio.publicar(crearRequest()));

    verify(productoRepositorio, never()).save(any());
  }
}

package com.agrovalle.agrovalle_connect.productos;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.excepciones.GlobalExceptionHandler;
import com.agrovalle.agrovalle_connect.excepciones.RecursoNoEncontradoException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ProductoControllerTest {

  private static final String JSON_VALIDO = "{\"agricultorId\":1,\"categoriaId\":2,\"nombre\":\"Tomate chonto\","
      + "\"descripcion\":\"Cosecha fresca\",\"precio\":2500.00,\"cantidadDisponible\":40,\"unidad\":\"kg\","
      + "\"fotoUrl\":\"https://fotos.com/tomate.jpg\"}";

  @Mock
  private ProductoService servicio;

  @InjectMocks
  private ProductoController controlador;

  private MockMvc mockMvc;

  @BeforeEach
  void preparar() {
    mockMvc = MockMvcBuilders.standaloneSetup(controlador)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void postPublicaProductoYDevuelve201() throws Exception {
    when(servicio.publicar(any(ProductoRequest.class))).thenReturn(new ProductoResponse(10L, 1L, 2L,
        "Tomate chonto", "Cosecha fresca", new BigDecimal("2500.00"), 40, "kg", "https://fotos.com/tomate.jpg",
        LocalDateTime.of(2026, 11, 1, 10, 0)));

    mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(10))
        .andExpect(jsonPath("$.fotoUrl").value("https://fotos.com/tomate.jpg"));
  }

  @Test
  void postConPrecioCeroDevuelve400() throws Exception {
    String json = JSON_VALIDO.replace("2500.00", "0");

    mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(servicio);
  }

  @Test
  void postConAgricultorInexistenteDevuelve404() throws Exception {
    when(servicio.publicar(any(ProductoRequest.class)))
        .thenThrow(new RecursoNoEncontradoException("No existe el agricultor con id 1"));

    mockMvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
        .andExpect(status().isNotFound());
  }
}

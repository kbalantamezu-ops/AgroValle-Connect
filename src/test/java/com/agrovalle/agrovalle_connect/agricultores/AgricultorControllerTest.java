package com.agrovalle.agrovalle_connect.agricultores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.excepciones.GlobalExceptionHandler;
import com.agrovalle.agrovalle_connect.excepciones.ReglaNegocioException;
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
class AgricultorControllerTest {

  private static final String JSON_VALIDO = "{\"nombre\":\"Juan Pérez\",\"ubicacionValle\":\"Palmira\","
      + "\"cedula\":\"1112223334\",\"email\":\"juan@correo.com\",\"password\":\"clave1234\"}";

  @Mock
  private AgricultorService servicio;

  @InjectMocks
  private AgricultorController controlador;

  private MockMvc mockMvc;

  @BeforeEach
  void preparar() {
    mockMvc = MockMvcBuilders.standaloneSetup(controlador)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void postRegistraAgricultorYDevuelve201() throws Exception {
    when(servicio.registrar(any(AgricultorRequest.class)))
        .thenReturn(new AgricultorResponse(1L, "Juan Pérez", "Palmira", "1112223334", "juan@correo.com"));

    mockMvc.perform(post("/api/agricultores").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.email").value("juan@correo.com"));
  }

  @Test
  void postConCorreoInvalidoDevuelve400() throws Exception {
    String json = JSON_VALIDO.replace("juan@correo.com", "no-es-correo");

    mockMvc.perform(post("/api/agricultores").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.mensaje").value("Datos inválidos"));

    verifyNoInteractions(servicio);
  }

  @Test
  void postConDatosDuplicadosDevuelve409() throws Exception {
    when(servicio.registrar(any(AgricultorRequest.class)))
        .thenThrow(new ReglaNegocioException("Ya existe un agricultor con esa cédula"));

    mockMvc.perform(post("/api/agricultores").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.mensaje").value("Ya existe un agricultor con esa cédula"));
  }
}

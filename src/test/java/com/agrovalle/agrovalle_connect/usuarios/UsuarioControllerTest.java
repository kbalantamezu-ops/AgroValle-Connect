package com.agrovalle.agrovalle_connect.usuarios;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.comun.GlobalExceptionHandler;
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
class UsuarioControllerTest {

  private static final String JSON_VALIDO = "{\"nombre\":\"Ana Gómez\",\"email\":\"ana@correo.com\","
      + "\"password\":\"clave1234\",\"telefono\":\"3001234567\"}";

  @Mock
  private UsuarioService servicio;

  @InjectMocks
  private UsuarioController controlador;

  private MockMvc mockMvc;

  @BeforeEach
  void preparar() {
    mockMvc = MockMvcBuilders.standaloneSetup(controlador)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void postRegistraUsuarioYDevuelve201() throws Exception {
    when(servicio.registrar(any(UsuarioRequest.class)))
        .thenReturn(new UsuarioResponse(7L, "Ana Gómez", "ana@correo.com", "3001234567"));

    mockMvc.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(7));
  }

  @Test
  void postConContrasenaCortaDevuelve400() throws Exception {
    String json = JSON_VALIDO.replace("clave1234", "corta");

    mockMvc.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(servicio);
  }

  @Test
  void postConCorreoDuplicadoDevuelve409() throws Exception {
    when(servicio.registrar(any(UsuarioRequest.class)))
        .thenThrow(new ReglaNegocioException("Ya existe un usuario con ese correo"));

    mockMvc.perform(post("/api/v1/usuarios").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
        .andExpect(status().isConflict());
  }
}

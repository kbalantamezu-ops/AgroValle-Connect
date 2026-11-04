package com.agrovalle.agrovalle_connect.fletes;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.excepciones.GlobalExceptionHandler;
import com.agrovalle.agrovalle_connect.excepciones.RecursoNoEncontradoException;
import java.math.BigDecimal;
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
class FleteControllerTest {

  private static final String JSON_VALIDO = "{\"municipio\":\"Palmira\",\"pesoKg\":10}";

  @Mock
  private FleteService servicio;

  @InjectMocks
  private FleteController controlador;

  private MockMvc mockMvc;

  @BeforeEach
  void preparar() {
    mockMvc = MockMvcBuilders.standaloneSetup(controlador)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void postCalculaFleteYDevuelve200() throws Exception {
    when(servicio.calcular(any(FleteRequest.class)))
        .thenReturn(new FleteResponse("Palmira", "CERCANA", new BigDecimal("10"), new BigDecimal("8000.00")));

    mockMvc.perform(post("/api/fletes/calcular").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.zona").value("CERCANA"))
        .andExpect(jsonPath("$.costo").value(8000.00));
  }

  @Test
  void postConPesoInvalidoDevuelve400() throws Exception {
    String json = "{\"municipio\":\"Palmira\",\"pesoKg\":0}";

    mockMvc.perform(post("/api/fletes/calcular").contentType(MediaType.APPLICATION_JSON).content(json))
        .andExpect(status().isBadRequest());

    verifyNoInteractions(servicio);
  }

  @Test
  void postSinTarifaDevuelve404() throws Exception {
    when(servicio.calcular(any(FleteRequest.class)))
        .thenThrow(new RecursoNoEncontradoException("No hay tarifa de flete para el municipio Palmira"));

    mockMvc.perform(post("/api/fletes/calcular").contentType(MediaType.APPLICATION_JSON).content(JSON_VALIDO))
        .andExpect(status().isNotFound());
  }
}

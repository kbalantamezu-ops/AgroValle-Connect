package com.agrovalle.agrovalle_connect.agricultores;

<<<<<<< HEAD
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.BaseIntegracion;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AgricultorControllerTest extends BaseIntegracion {

    @Test
    void registrarRetorna201ConDatosDelAgricultor() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("Carlos Test", "77000001", "DAGUA")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Carlos Test"))
                .andExpect(jsonPath("$.municipio").value("DAGUA"))
                .andExpect(jsonPath("$.cedula").value("77000001"))
                .andExpect(jsonPath("$.fechaRegistro").exists());
    }

    @Test
    void registrarCedulaDuplicadaRetorna409() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("Primero", "77000005", "BUGA")))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo("Segundo", "77000005", "CALI")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("cédula")));
    }

    @Test
    void registrarDatosInvalidosRetorna400ConErroresDeCampo() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "", "cedula": "77000099", "municipio": "CALI"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.nombre").exists());
    }

    @Test
    void registrarMunicipioInvalidoRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Municipio Malo", "cedula": "77000098", "municipio": "BOGOTA"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarRetorna200() throws Exception {
        mockMvc.perform(get("/api/v1/agricultores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerAgricultorRegistradoRetorna200() throws Exception {
        long id = registrar("Para Consultar", "77000002", "TULUA");
        mockMvc.perform(get("/api/v1/agricultores/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Para Consultar"))
                .andExpect(jsonPath("$.municipio").value("TULUA"));
    }

    @Test
    void obtenerAgricultorInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/v1/agricultores/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("999999")));
    }

    @Test
    void actualizarPerfilRetorna200() throws Exception {
        long id = registrar("Para Editar", "77000003", "SEVILLA");
        mockMvc.perform(put("/api/v1/agricultores/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Nombre Editado", "cedula": "77000003", "municipio": "SEVILLA",
                                 "telefono": "300 111 2233", "email": "editado@finca.co",
                                 "finca": "Finca Editada"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre Editado"))
                .andExpect(jsonPath("$.finca").value("Finca Editada"))
                .andExpect(jsonPath("$.telefono").value("300 111 2233"));
    }

    private long registrar(String nombre, String cedula, String municipio) throws Exception {
        String respuesta = mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(nombre, cedula, municipio)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private String cuerpo(String nombre, String cedula, String municipio) {
        return """
                {"nombre": "%s", "cedula": "%s", "municipio": "%s", "telefono": "300 000 0000",
                 "email": "correo@finca.co", "finca": "Finca Test"}
                """.formatted(nombre, cedula, municipio);
    }
=======
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
>>>>>>> origin/develop
}

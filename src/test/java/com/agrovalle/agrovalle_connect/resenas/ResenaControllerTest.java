package com.agrovalle.agrovalle_connect.resenas;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.BaseIntegracion;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ResenaControllerTest extends BaseIntegracion {

    @Test
    void crearResenaRetorna201() throws Exception {
        long agricultorId = registrarAgricultor("77000301");
        mockMvc.perform(post("/api/v1/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(agricultorId, 5, "Excelente producto y entrega puntual", "Cliente Test")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.calificacion").value(5))
                .andExpect(jsonPath("$.agricultorId").value(agricultorId));
    }

    @Test
    void crearResenaConCalificacionInvalidaRetorna400() throws Exception {
        long agricultorId = registrarAgricultor("77000302");
        mockMvc.perform(post("/api/v1/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(agricultorId, 6, "Comentario", "Cliente Test")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.calificacion").exists());
    }

    @Test
    void crearResenaConAgricultorInexistenteRetorna404() throws Exception {
        mockMvc.perform(post("/api/v1/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(999999, 4, "Comentario", "Cliente Test")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("999999")));
    }

    @Test
    void listarResenasDeAgricultorRetorna200() throws Exception {
        long agricultorId = registrarAgricultor("77000303");
        mockMvc.perform(post("/api/v1/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo(agricultorId, 4, "Buena calidad", "Cliente Lista")))
                .andExpect(status().isCreated());
        mockMvc.perform(get("/api/v1/resenas").param("agricultorId", String.valueOf(agricultorId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.autorNombre == 'Cliente Lista')]").isNotEmpty());
    }

    private long registrarAgricultor(String cedula) throws Exception {
        String cuerpo = """
                {"nombre": "Agricultor Resenas", "cedula": "%s", "municipio": "CARTAGO"}
                """.formatted(cedula);
        String respuesta = mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private String cuerpo(long agricultorId, int calificacion, String comentario, String autor) {
        return """
                {"agricultorId": %d, "calificacion": %d, "comentario": "%s", "autorNombre": "%s"}
                """.formatted(agricultorId, calificacion, comentario, autor);
    }
}

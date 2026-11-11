package com.agrovalle.agrovalle_connect.agricultores;

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
    void panelRetornaResumenDelAgricultor() throws Exception {
        long id = registrar("Panel Test", "77000004", "PALMIRA");
        mockMvc.perform(get("/api/v1/agricultores/" + id + "/panel")
                        .with(conToken(tokenAgricultor(id))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.agricultor.id").value(id))
                .andExpect(jsonPath("$.agricultor.nombre").value("Panel Test"))
                .andExpect(jsonPath("$.productos").isArray())
                .andExpect(jsonPath("$.pedidos").isArray())
                .andExpect(jsonPath("$.pedidosRecibidos").isNumber())
                .andExpect(jsonPath("$.ventasTotales").exists());
    }

    @Test
    void panelRequiereAutenticacion() throws Exception {
        mockMvc.perform(get("/api/v1/agricultores/1/panel"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void panelRechazaAgricultorAjeno() throws Exception {
        long id = registrar("Panel Ajeno", "77000006", "CALI");
        long otro = registrar("Otro Panel", "77000007", "BUGA");
        mockMvc.perform(get("/api/v1/agricultores/" + id + "/panel")
                        .with(conToken(tokenAgricultor(otro))))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizarPerfilRetorna200() throws Exception {
        long id = registrar("Para Editar", "77000003", "SEVILLA");
        mockMvc.perform(put("/api/v1/agricultores/" + id)
                        .with(conToken(tokenAgricultor(id)))
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
}

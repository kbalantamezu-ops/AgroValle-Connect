package com.agrovalle.agrovalle_connect.auth;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.BaseIntegracion;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthControllerTest extends BaseIntegracion {

    @Test
    void registroCompradorYLoginRetornanToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/registro/comprador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana Compradora","email":"ana.auth@test.co",
                                 "password":"Clave1234*","telefono":"3001112233"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana.auth@test.co"))
                .andExpect(jsonPath("$.rol").value("COMPRADOR"));

        String login = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"ana.auth@test.co","password":"Clave1234*"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.usuario.email").value("ana.auth@test.co"))
                .andReturn().getResponse().getContentAsString();
        String token = objectMapper.readTree(login).get("token").asText();

        mockMvc.perform(get("/api/v1/auth/me").with(conToken(token)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana.auth@test.co"));
    }

    @Test
    void loginConCredencialesInvalidasRetorna401() throws Exception {
        mockMvc.perform(post("/api/v1/auth/registro/comprador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Ana","email":"bad.auth@test.co","password":"Clave1234*"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"bad.auth@test.co","password":"OtraClave1*"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(containsString("incorrectos")));
    }

    @Test
    void registroAgricultorCreaCuentaVinculada() throws Exception {
        mockMvc.perform(post("/api/v1/auth/registro/agricultor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Luis Finca","cedula":"99000001","municipio":"BUGA",
                                 "telefono":"3100000000","email":"luis.auth@test.co","finca":"La Prueba",
                                 "password":"Clave1234*"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("AGRICULTOR"))
                .andExpect(jsonPath("$.agricultorId").isNumber());
    }

    @Test
    void meSinTokenRetorna401() throws Exception {
        mockMvc.perform(get("/api/v1/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}

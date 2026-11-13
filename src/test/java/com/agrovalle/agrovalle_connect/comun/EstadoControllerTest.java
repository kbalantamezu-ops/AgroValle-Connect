package com.agrovalle.agrovalle_connect.comun;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.BaseIntegracion;
import org.junit.jupiter.api.Test;

class EstadoControllerTest extends BaseIntegracion {

    @Test
    void estadoRespondeOkConPerfilYBaseDeDatos() throws Exception {
        mockMvc.perform(get("/api/v1/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aplicacion").value("AgroValle Connect"))
                .andExpect(jsonPath("$.estado").value("OK"))
                .andExpect(jsonPath("$.perfil").value("test"))
                .andExpect(jsonPath("$.baseDatos").value("H2 Connected"));
    }
}

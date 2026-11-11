package com.agrovalle.agrovalle_connect.pagos;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.BaseIntegracion;
import org.junit.jupiter.api.Test;

class MedioPagoControllerTest extends BaseIntegracion {

    @Test
    void listarRetornaMediosRegistrados() throws Exception {
        medioPagoId("NEQUI");

        mockMvc.perform(get("/api/v1/medios-pago"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[*].nombre", hasItem("NEQUI")));
    }
}

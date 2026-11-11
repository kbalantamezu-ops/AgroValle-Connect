package com.agrovalle.agrovalle_connect.catalogo;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.BaseIntegracion;
import org.junit.jupiter.api.Test;

class CatalogoControllerTest extends BaseIntegracion {

    @Test
    void municipiosRetornaCatalogoDelValle() throws Exception {
        mockMvc.perform(get("/api/v1/catalogo/municipios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Municipio.values().length))
                .andExpect(jsonPath("$[*].nombre", hasItem("DAGUA")))
                .andExpect(jsonPath("$[*].etiqueta", hasItem("Tuluá")));
    }

    @Test
    void categoriasRetornaCatalogo() throws Exception {
        mockMvc.perform(get("/api/v1/catalogo/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(Categoria.values().length))
                .andExpect(jsonPath("$[*].nombre", hasItem("CAFE")))
                .andExpect(jsonPath("$[*].etiqueta", hasItem("Café")));
    }
}

package com.agrovalle.agrovalle_connect.productos;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.BaseIntegracion;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PrecioControllerTest extends BaseIntegracion {

    @Test
    void promedioRetornaValorCalculadoEnPesos() throws Exception {
        long agricultorId = registrarAgricultor("77000401");
        publicarProducto("Queso Campesino A", 100, "1000", agricultorId);
        publicarProducto("Queso Campesino B", 100, "2000", agricultorId);

        mockMvc.perform(get("/api/v1/precios/promedio").param("categoria", "LACTEOS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoria").value("LACTEOS"))
                .andExpect(jsonPath("$.promedio").value(1500.00))
                .andExpect(jsonPath("$.cantidadProductos").value(2))
                .andExpect(jsonPath("$.moneda").value("COP"));
    }

    @Test
    void promedioSinProductosRetorna404() throws Exception {
        mockMvc.perform(get("/api/v1/precios/promedio").param("categoria", "OTROS"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("Otros")));
    }

    @Test
    void promedioSinParametroRetorna400() throws Exception {
        mockMvc.perform(get("/api/v1/precios/promedio"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("categoria")));
    }

    @Test
    void promedioConCategoriaInvalidaRetorna400() throws Exception {
        mockMvc.perform(get("/api/v1/precios/promedio").param("categoria", "NO_EXISTE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("categoria")));
    }

    private long registrarAgricultor(String cedula) throws Exception {
        String cuerpo = """
                {"nombre": "Agricultor Precios", "cedula": "%s", "municipio": "ZARZAL"}
                """.formatted(cedula);
        String respuesta = mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private void publicarProducto(String nombre, double cantidad, String precio, long agricultorId)
            throws Exception {
        String cuerpo = """
                {"nombre": "%s", "categoria": "LACTEOS", "cantidadDisponible": %s, "precioUnitario": %s,
                 "unidad": "kg", "fechaCosecha": "%s", "agricultorId": %d}
                """.formatted(nombre, cantidad, precio, LocalDate.now().plusDays(4), agricultorId);
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated());
    }
}

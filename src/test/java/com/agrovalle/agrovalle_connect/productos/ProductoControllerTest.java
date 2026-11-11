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

class ProductoControllerTest extends BaseIntegracion {

    @Test
    void publicarRetorna201ConDatosDelProducto() throws Exception {
        long agricultorId = registrarAgricultor("77000101", "SEVILLA");
        mockMvc.perform(post("/api/v1/productos")
                        .with(conToken(tokenAgricultor(agricultorId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoProducto("Guayaba Test", "FRUTAS", 100, "1500", agricultorId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.nombre").value("Guayaba Test"))
                .andExpect(jsonPath("$.categoria").value("FRUTAS"))
                .andExpect(jsonPath("$.municipio").value("SEVILLA"))
                .andExpect(jsonPath("$.disponible").value(true))
                .andExpect(jsonPath("$.agricultor.id").value(agricultorId));
    }

    @Test
    void publicarSinAutenticacionRetorna401() throws Exception {
        long agricultorId = registrarAgricultor("77000109", "SEVILLA");
        mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoProducto("Sin Token", "FRUTAS", 10, "1000", agricultorId)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicarConFechaPasadaRetorna400() throws Exception {
        long agricultorId = registrarAgricultor("77000102", "SEVILLA");
        String cuerpo = """
                {"nombre": "Producto Fecha", "categoria": "FRUTAS", "cantidadDisponible": 10,
                 "precioUnitario": 1000, "unidad": "kg", "fechaCosecha": "2020-01-01",
                 "agricultorId": %d}
                """.formatted(agricultorId);
        mockMvc.perform(post("/api/v1/productos")
                        .with(conToken(tokenAgricultor(agricultorId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.fechaCosecha").exists());
    }

    @Test
    void publicarParaOtroAgricultorRetorna403() throws Exception {
        long agricultorId = registrarAgricultor("77000108", "SEVILLA");
        long otro = registrarAgricultor("77000110", "BUGA");
        mockMvc.perform(post("/api/v1/productos")
                        .with(conToken(tokenAgricultor(otro)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoProducto("Ajeno", "FRUTAS", 10, "1000", agricultorId)))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarConFiltrosRetornaResultados() throws Exception {
        long agricultorId = registrarAgricultor("77000103", "SEVILLA");
        publicar("Mangostino Filtro", "FRUTAS", 40, "3000", agricultorId);

        mockMvc.perform(get("/api/v1/productos").param("q", "Mangostino"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre == 'Mangostino Filtro')]").isNotEmpty());

        mockMvc.perform(get("/api/v1/productos").param("municipio", "SEVILLA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre == 'Mangostino Filtro')]").isNotEmpty());

        mockMvc.perform(get("/api/v1/productos").param("categoria", "FRUTAS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre == 'Mangostino Filtro')]").isNotEmpty());

        mockMvc.perform(get("/api/v1/productos").param("disponible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.nombre == 'Mangostino Filtro')]").isNotEmpty());

        mockMvc.perform(get("/api/v1/productos").param("municipio", "CALI"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void obtenerProductoRegistradoRetorna200() throws Exception {
        long agricultorId = registrarAgricultor("77000104", "SEVILLA");
        long productoId = publicar("Lulo Test", "FRUTAS", 60, "2800", agricultorId);
        mockMvc.perform(get("/api/v1/productos/" + productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Lulo Test"))
                .andExpect(jsonPath("$.agricultor.nombre").exists());
    }

    @Test
    void obtenerProductoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/v1/productos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("999999")));
    }

    private long registrarAgricultor(String cedula, String municipio) throws Exception {
        String cuerpo = """
                {"nombre": "Agricultor Productos", "cedula": "%s", "municipio": "%s"}
                """.formatted(cedula, municipio);
        String respuesta = mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private long publicar(String nombre, String categoria, double cantidad, String precio, long agricultorId)
            throws Exception {
        String respuesta = mockMvc.perform(post("/api/v1/productos")
                        .with(conToken(tokenAgricultor(agricultorId)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpoProducto(nombre, categoria, cantidad, precio, agricultorId)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private String cuerpoProducto(String nombre, String categoria, double cantidad, String precio,
            long agricultorId) {
        return """
                {"nombre": "%s", "categoria": "%s", "cantidadDisponible": %s, "precioUnitario": %s,
                 "unidad": "kg", "fechaCosecha": "%s", "agricultorId": %d}
                """.formatted(nombre, categoria, cantidad, precio,
                LocalDate.now().plusDays(5), agricultorId);
    }
}

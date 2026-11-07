package com.agrovalle.agrovalle_connect.pedidos;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.agrovalle.agrovalle_connect.BaseIntegracion;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class PedidoControllerTest extends BaseIntegracion {

    @Test
    void crearDescuentaStockYRetorna201() throws Exception {
        long agricultorId = registrarAgricultor("77000201");
        long productoId = publicarProducto("Yuca Test", "TUBERCULOS", 50, "2000", agricultorId);
        long medioPagoId = medioPagoId("EFECTIVO");
        String cuerpo = """
                {"compradorNombre": "Comprador Test", "compradorTelefono": "300 123 4567",
                 "medioPagoId": %d, "items": [{"productoId": %d, "cantidad": 10}]}
                """.formatted(medioPagoId, productoId);
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.total").value(20000.00))
                .andExpect(jsonPath("$.pago.medioPago").value("EFECTIVO"))
                .andExpect(jsonPath("$.pago.monto").value(20000.00))
                .andExpect(jsonPath("$.pago.estado").value("PAGADO"))
                .andExpect(jsonPath("$.pago.fechaPago").isNotEmpty())
                .andExpect(jsonPath("$.items[0].productoNombre").value("Yuca Test"))
                .andExpect(jsonPath("$.items[0].subtotal").value(20000.00));

        mockMvc.perform(get("/api/v1/productos/" + productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(40.0));
    }

    @Test
    void crearConStockInsuficienteRetorna409SinDescontar() throws Exception {
        long agricultorId = registrarAgricultor("77000202");
        long productoId = publicarProducto("Papa Test", "TUBERCULOS", 5, "3000", agricultorId);
        String cuerpo = """
                {"compradorNombre": "Comprador Test", "medioPagoId": %d,
                 "items": [{"productoId": %d, "cantidad": 50}]}
                """.formatted(medioPagoId("EFECTIVO"), productoId);
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("Stock insuficiente")));

        mockMvc.perform(get("/api/v1/productos/" + productoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadDisponible").value(5.0));
    }

    @Test
    void crearConProductoInexistenteRetorna404() throws Exception {
        String cuerpo = """
                {"compradorNombre": "Comprador Test", "medioPagoId": %d,
                 "items": [{"productoId": 999999, "cantidad": 2}]}
                """.formatted(medioPagoId("EFECTIVO"));
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("999999")));
    }

    @Test
    void crearConMedioPagoInexistenteRetorna400() throws Exception {
        long agricultorId = registrarAgricultor("77000206");
        long productoId = publicarProducto("Yuca Medio", "TUBERCULOS", 10, "2000", agricultorId);
        String cuerpo = """
                {"compradorNombre": "Comprador Test", "medioPagoId": 999999,
                 "items": [{"productoId": %d, "cantidad": 2}]}
                """.formatted(productoId);
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(containsString("Medio de pago")));
    }

    @Test
    void crearSinItemsRetorna400() throws Exception {
        mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"compradorNombre": "Comprador Test", "items": []}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.items").exists());
    }

    @Test
    void cambiarEstadoGeneraNotificacionYEstadoTerminalBloqueaCambios() throws Exception {
        long pedidoId = crearPedido("77000203", "Papa Avanza", 20);

        mockMvc.perform(patch("/api/v1/pedidos/" + pedidoId + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"ENVIADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADO"));

        mockMvc.perform(get("/api/v1/pedidos/" + pedidoId + "/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].mensaje", hasItem(containsString("cambió a ENVIADO"))));

        mockMvc.perform(patch("/api/v1/pedidos/" + pedidoId + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"ENTREGADO\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/api/v1/pedidos/" + pedidoId + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"CANCELADO\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("ENTREGADO")));
    }

    @Test
    void cambiarEstadoInvalidoRetorna400() throws Exception {
        long pedidoId = crearPedido("77000204", "Papa Estado", 20);
        mockMvc.perform(patch("/api/v1/pedidos/" + pedidoId + "/estado")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.estado").exists());
    }

    @Test
    void obtenerPedidoRegistradoRetorna200ConItems() throws Exception {
        long pedidoId = crearPedido("77000205", "Papa Consulta", 20);
        mockMvc.perform(get("/api/v1/pedidos/" + pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andExpect(jsonPath("$.pago.medioPago").value("EFECTIVO"))
                .andExpect(jsonPath("$.items[0].productoNombre").value("Papa Consulta"))
                .andExpect(jsonPath("$.items[0].municipio").value("YUMBO"));
    }

    @Test
    void listarPedidosRetorna200() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void obtenerPedidoInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/v1/pedidos/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString("999999")));
    }

    private long crearPedido(String cedula, String nombreProducto, double cantidad) throws Exception {
        long agricultorId = registrarAgricultor(cedula);
        long productoId = publicarProducto(nombreProducto, "TUBERCULOS", cantidad + 30, "2500", agricultorId);
        long medioPagoId = medioPagoId("EFECTIVO");
        String cuerpo = """
                {"compradorNombre": "Comprador %s", "medioPagoId": %d,
                 "items": [{"productoId": %d, "cantidad": %s}]}
                """.formatted(cedula, medioPagoId, productoId, cantidad);
        String respuesta = mockMvc.perform(post("/api/v1/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private long registrarAgricultor(String cedula) throws Exception {
        String cuerpo = """
                {"nombre": "Agricultor Pedidos", "cedula": "%s", "municipio": "YUMBO"}
                """.formatted(cedula);
        String respuesta = mockMvc.perform(post("/api/v1/agricultores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }

    private long publicarProducto(String nombre, String categoria, double cantidad, String precio,
            long agricultorId) throws Exception {
        String cuerpo = """
                {"nombre": "%s", "categoria": "%s", "cantidadDisponible": %s, "precioUnitario": %s,
                 "unidad": "kg", "fechaCosecha": "%s", "agricultorId": %d}
                """.formatted(nombre, categoria, cantidad, precio, LocalDate.now().plusDays(5), agricultorId);
        String respuesta = mockMvc.perform(post("/api/v1/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cuerpo))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(respuesta).get("id").asLong();
    }
}

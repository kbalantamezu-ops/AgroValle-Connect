package com.agrovalle.agrovalle_connect;

import com.agrovalle.agrovalle_connect.pagos.MedioPago;
import com.agrovalle.agrovalle_connect.pagos.MedioPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public abstract class BaseIntegracion {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MedioPagoRepository medioPagoRepository;

    protected long medioPagoId(String nombre) {
        return medioPagoRepository.findByNombre(nombre)
                .orElseGet(() -> medioPagoRepository.save(new MedioPago(nombre)))
                .getId();
    }
}

package com.agrovalle.agrovalle_connect.pagos;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/medios-pago")
public class MedioPagoController {

    private final MedioPagoRepository medioPagoRepository;

    public MedioPagoController(MedioPagoRepository medioPagoRepository) {
        this.medioPagoRepository = medioPagoRepository;
    }

    @GetMapping
    public List<MedioPagoResponse> listar() {
        return medioPagoRepository.findAll(Sort.by("id")).stream()
                .map(medio -> new MedioPagoResponse(medio.getId(), medio.getNombre()))
                .toList();
    }

    public record MedioPagoResponse(Long id, String nombre) {
    }
}

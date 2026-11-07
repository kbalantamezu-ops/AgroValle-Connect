package com.agrovalle.agrovalle_connect.resenas;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import com.agrovalle.agrovalle_connect.agricultores.AgricultorService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;

    private final AgricultorService agricultorService;

    public ResenaService(ResenaRepository resenaRepository, AgricultorService agricultorService) {
        this.resenaRepository = resenaRepository;
        this.agricultorService = agricultorService;
    }

    @Transactional
    public ResenaResponse crear(ResenaRequest request) {
        Agricultor agricultor = agricultorService.buscarEntidad(request.agricultorId());
        Resena resena = new Resena();
        resena.setAgricultor(agricultor);
        resena.setCalificacion(request.calificacion());
        resena.setComentario(request.comentario());
        resena.setAutorNombre(request.autorNombre());
        resena.setFechaCreacion(LocalDateTime.now());
        return toResponse(resenaRepository.save(resena));
    }

    @Transactional(readOnly = true)
    public List<ResenaResponse> listarPorAgricultor(Long agricultorId) {
        agricultorService.buscarEntidad(agricultorId);
        return resenaRepository.findByAgricultorIdOrderByFechaCreacionDesc(agricultorId).stream()
                .map(this::toResponse).toList();
    }

    private ResenaResponse toResponse(Resena resena) {
        return new ResenaResponse(resena.getId(), resena.getAgricultor().getId(), resena.getCalificacion(),
                resena.getComentario(), resena.getAutorNombre(), resena.getFechaCreacion());
    }
}

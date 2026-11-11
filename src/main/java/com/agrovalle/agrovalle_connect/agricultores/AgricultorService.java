package com.agrovalle.agrovalle_connect.agricultores;

import com.agrovalle.agrovalle_connect.comun.ApiException;
import com.agrovalle.agrovalle_connect.resenas.ResenaRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AgricultorService {

    private final AgricultorRepository agricultorRepository;

    private final ResenaRepository resenaRepository;

    public AgricultorService(AgricultorRepository agricultorRepository, ResenaRepository resenaRepository) {
        this.agricultorRepository = agricultorRepository;
        this.resenaRepository = resenaRepository;
    }

    @Transactional
    public AgricultorResponse registrar(AgricultorRequest request) {
        if (agricultorRepository.existsByCedula(request.cedula())) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Ya existe un agricultor registrado con la cédula " + request.cedula());
        }
        Agricultor agricultor = new Agricultor(request.nombre(), request.cedula(), request.municipio(),
                request.telefono(), request.email(), request.finca());
        return toResponse(agricultorRepository.save(agricultor));
    }

    @Transactional(readOnly = true)
    public List<AgricultorResponse> listar() {
        return agricultorRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AgricultorResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional
    public AgricultorResponse actualizar(Long id, AgricultorRequest request) {
        Agricultor agricultor = buscarEntidad(id);
        agricultor.setNombre(request.nombre());
        agricultor.setMunicipio(request.municipio());
        agricultor.setTelefono(request.telefono());
        agricultor.setEmail(request.email());
        agricultor.setFinca(request.finca());
        return toResponse(agricultor);
    }

    public Agricultor buscarEntidad(Long id) {
        return agricultorRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Agricultor no encontrado con id " + id));
    }

    private AgricultorResponse toResponse(Agricultor agricultor) {
        Double promedio = resenaRepository.promedioCalificacion(agricultor.getId()).orElse(null);
        return new AgricultorResponse(agricultor.getId(), agricultor.getNombre(), agricultor.getCedula(),
                agricultor.getMunicipio().name(), agricultor.getTelefono(), agricultor.getEmail(),
                agricultor.getFinca(), agricultor.getFechaRegistro(), promedio);
    }
}

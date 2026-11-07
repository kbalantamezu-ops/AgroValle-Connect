package com.agrovalle.agrovalle_connect.agricultores;

import org.springframework.stereotype.Component;

@Component
public class AgricultorMapper {

  public Agricultor toEntity(AgricultorRequest request, String passwordHash) {
    return Agricultor.builder()
        .nombre(request.nombre())
        .ubicacionValle(request.ubicacionValle())
        .cedula(request.cedula())
        .email(request.email())
        .passwordHash(passwordHash)
        .build();
  }

  public AgricultorResponse toResponse(Agricultor agricultor) {
    return new AgricultorResponse(agricultor.getId(), agricultor.getNombre(), agricultor.getUbicacionValle(),
        agricultor.getCedula(), agricultor.getEmail());
  }
}

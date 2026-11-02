package com.agrovalle.agrovalle_connect.fletes;

import com.agrovalle.agrovalle_connect.excepciones.RecursoNoEncontradoException;
import java.util.List;
import org.springframework.stereotype.Service;

/** HU-07: elige la estrategia que aplica al municipio y calcula el costo del flete. */
@Service
public class FleteService {

  private final List<EstrategiaFlete> estrategias;

  public FleteService(List<EstrategiaFlete> estrategias) {
    this.estrategias = estrategias;
  }

  public FleteResponse calcular(FleteRequest request) {
    EstrategiaFlete estrategia = estrategias.stream()
        .filter(e -> e.aplicaA(request.municipio()))
        .findFirst()
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "No hay tarifa de flete para el municipio " + request.municipio()));
    return new FleteResponse(request.municipio(), estrategia.zona(), request.pesoKg(),
        estrategia.calcular(request.pesoKg()));
  }
}

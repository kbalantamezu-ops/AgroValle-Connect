package com.agrovalle.agrovalle_connect.fletes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/fletes")
@RequiredArgsConstructor
public class FleteController {

  private final FleteService servicio;

  @PostMapping("/calcular")
  public FleteResponse calcular(@Valid @RequestBody FleteRequest request) {
    return servicio.calcular(request);
  }
}

package com.agrovalle.agrovalle_connect.agricultores;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/agricultores")
@RequiredArgsConstructor
public class AgricultorController {

  private final AgricultorService servicio;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public AgricultorResponse registrar(@Valid @RequestBody AgricultorRequest request) {
    return servicio.registrar(request);
  }
}

package com.agrovalle.agrovalle_connect.productos;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

  private final ProductoService servicio;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ProductoResponse publicar(@Valid @RequestBody ProductoRequest request) {
    return servicio.publicar(request);
  }
}

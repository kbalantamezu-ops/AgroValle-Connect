package com.agrovalle.agrovalle_connect.agricultores;

import jakarta.validation.Valid;
<<<<<<< HEAD
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agricultores")
public class AgricultorController {

    private final AgricultorService agricultorService;

    public AgricultorController(AgricultorService agricultorService) {
        this.agricultorService = agricultorService;
    }

    @PostMapping
    public ResponseEntity<AgricultorResponse> registrar(@Valid @RequestBody AgricultorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agricultorService.registrar(request));
    }

    @GetMapping
    public List<AgricultorResponse> listar() {
        return agricultorService.listar();
    }

    @GetMapping("/{id}")
    public AgricultorResponse obtener(@PathVariable Long id) {
        return agricultorService.obtener(id);
    }

    @PutMapping("/{id}")
    public AgricultorResponse actualizar(@PathVariable Long id, @Valid @RequestBody AgricultorRequest request) {
        return agricultorService.actualizar(id, request);
    }
=======
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
>>>>>>> origin/develop
}

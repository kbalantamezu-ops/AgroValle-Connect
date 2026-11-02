package com.agrovalle.agrovalle_connect.excepciones;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Traduce las excepciones del sistema a respuestas HTTP. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorRespuesta> manejarValidacion(MethodArgumentNotValidException ex) {
    List<String> detalles = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .toList();
    return construir(HttpStatus.BAD_REQUEST, "Datos inválidos", detalles);
  }

  @ExceptionHandler(RecursoNoEncontradoException.class)
  public ResponseEntity<ErrorRespuesta> manejarNoEncontrado(RecursoNoEncontradoException ex) {
    return construir(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
  }

  @ExceptionHandler(ReglaNegocioException.class)
  public ResponseEntity<ErrorRespuesta> manejarReglaNegocio(ReglaNegocioException ex) {
    return construir(HttpStatus.CONFLICT, ex.getMessage(), List.of());
  }

  private ResponseEntity<ErrorRespuesta> construir(HttpStatus estado, String mensaje, List<String> detalles) {
    return ResponseEntity.status(estado).body(new ErrorRespuesta(estado.value(), mensaje, detalles));
  }
}

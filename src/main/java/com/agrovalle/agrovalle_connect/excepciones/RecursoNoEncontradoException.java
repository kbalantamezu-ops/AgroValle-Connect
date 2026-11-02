package com.agrovalle.agrovalle_connect.excepciones;

/** Se lanza cuando un recurso referenciado no existe. */
public class RecursoNoEncontradoException extends RuntimeException {

  public RecursoNoEncontradoException(String mensaje) {
    super(mensaje);
  }
}

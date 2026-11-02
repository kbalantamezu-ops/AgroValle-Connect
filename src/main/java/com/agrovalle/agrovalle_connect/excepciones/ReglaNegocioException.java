package com.agrovalle.agrovalle_connect.excepciones;

/** Se lanza cuando se viola una regla de negocio (por ejemplo, datos duplicados). */
public class ReglaNegocioException extends RuntimeException {

  public ReglaNegocioException(String mensaje) {
    super(mensaje);
  }
}

package com.agrovalle.agrovalle_connect.comun;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> manejarApiException(ApiException ex, HttpServletRequest request) {
        return construir(ex.getStatus(), ex.getMessage(), request, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> manejarValidacion(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(fe -> errores.putIfAbsent(fe.getField(), fe.getDefaultMessage()));
        return construir(HttpStatus.BAD_REQUEST, "Datos de la solicitud inválidos", request, errores);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> manejarCuerpoIlegible(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST, "Cuerpo de la solicitud inválido", request, null);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> manejarParametroFaltante(MissingServletRequestParameterException ex,
            HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST,
                "Falta el parámetro requerido '" + ex.getParameterName() + "'", request, null);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> manejarTipoInvalido(MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST,
                "Valor inválido para el parámetro '" + ex.getName() + "'", request, null);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiError> manejarRecursoNoEncontrado(NoResourceFoundException ex,
            HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "Recurso no encontrado", request, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> manejarErrorInesperado(Exception ex, HttpServletRequest request) {
        return construir(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor", request, null);
    }

    private ResponseEntity<ApiError> construir(HttpStatus status, String mensaje, HttpServletRequest request,
            Map<String, String> errores) {
        ApiError body = new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                errores);
        return ResponseEntity.status(status).body(body);
    }
}

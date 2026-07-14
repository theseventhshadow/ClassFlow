package com.ohiggins.classflow.bff.exception;

import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Convierte errores del BFF en respuestas uniformes para el frontend.
 *
 * Reporta explicitamente a Sentry/GlitchTip: en WebFlux, un @RestControllerAdvice que
 * resuelve la excepcion evita que el filtro reactivo de Sentry la vea (limitacion conocida
 * de Spring WebFlux, distinto al comportamiento en Spring MVC).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Convierte errores de validacion en un mapa campo-error.
     *
     * @param ex excepcion capturada.
     * @return respuesta HTTP 400 con los errores por campo.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Convierte excepciones de negocio o de flujo en un error estandar.
     *
     * @param ex excepcion capturada.
     * @param exchange contexto de la peticion en curso.
     * @return respuesta HTTP 400 con el detalle del error.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, ServerWebExchange exchange) {
        Sentry.captureException(ex);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .path(exchange.getRequest().getURI().getPath())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Convierte excepciones inesperadas en un error estandar.
     *
     * @param ex excepcion capturada.
     * @param exchange contexto de la peticion en curso.
     * @return respuesta HTTP 500 con el detalle del error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, ServerWebExchange exchange) {
        Sentry.captureException(ex);
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred: " + ex.getMessage())
                .path(exchange.getRequest().getURI().getPath())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

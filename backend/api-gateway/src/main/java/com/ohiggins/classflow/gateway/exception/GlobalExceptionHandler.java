package com.ohiggins.classflow.gateway.exception;

import io.sentry.Sentry;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import java.time.LocalDateTime;

/**
 * Intercepta excepciones no controladas y responde con un formato uniforme.
 *
 * Reporta explicitamente a Sentry/GlitchTip: en WebFlux, un @RestControllerAdvice que
 * resuelve la excepcion evita que el filtro reactivo de Sentry la vea (limitacion conocida
 * de Spring WebFlux, distinto al comportamiento en Spring MVC).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja errores de negocio o validacion representados como RuntimeException.
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
     * Maneja excepciones no previstas y devuelve un error generico.
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

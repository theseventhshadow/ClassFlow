package com.ohiggins.classflow.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Respuesta generica de confirmacion para operaciones sin cuerpo de datos.
 */
@Data
@AllArgsConstructor
public class MessageResponseDTO {
    private String message;
}

package com.ohiggins.classflow.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Datos de entrada para enviar un correo electronico.
 */
@Data
public class EmailRequestDTO {
    @NotBlank @Email
    private String to;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;
}
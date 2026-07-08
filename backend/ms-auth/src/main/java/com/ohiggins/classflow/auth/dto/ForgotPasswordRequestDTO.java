package com.ohiggins.classflow.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Datos de entrada para solicitar el restablecimiento de contrasena.
 */
@Data
public class ForgotPasswordRequestDTO {
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    private String email;
}

package com.ohiggins.classflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Datos de entrada para cambiar la contrasena.
 */
@Data
public class ChangePasswordRequestDTO {
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    private String newPassword;
}

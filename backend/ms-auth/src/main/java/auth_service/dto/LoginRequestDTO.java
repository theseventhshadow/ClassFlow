package auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Credenciales para iniciar sesión")
public class LoginRequestDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    @Schema(description = "Correo electrónico del usuario", example = "admin@example.com")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Schema(description = "Contraseña del usuario", example = "password")
    private String password;
}

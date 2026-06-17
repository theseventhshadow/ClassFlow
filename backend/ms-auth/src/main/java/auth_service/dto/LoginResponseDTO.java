package auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT")
public class LoginResponseDTO {

    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Email del usuario autenticado", example = "admin@example.com")
    private String email;

    @Schema(description = "Rol del usuario", example = "ADMINISTRATOR")
    private String role;

    @Schema(description = "Nombre completo del usuario", example = "Administrador Root")
    private String fullName;

    public LoginResponseDTO(String token, String email, String role, String fullName) {
        this.token = token;
        this.email = email;
        this.role = role;
        this.fullName = fullName;
    }
}

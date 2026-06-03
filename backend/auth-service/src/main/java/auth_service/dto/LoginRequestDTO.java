package auth_service.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDTO {
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
package auth_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    @NotBlank(message = "ID number is required")
    @Pattern(regexp = "^\\d{1,2}\\.\\d{3}\\.\\d{3}-[\\dkK]$", message = "Invalid ID number format")
    private String idNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must have at least 6 characters")
    private String password;

    @NotBlank(message = "Role is required")
    @Pattern(regexp = "ADMINISTRATOR|TEACHER|GUARDIAN|STUDENT", message = "Invalid role")
    private String role;

    private String course;
    private Long guardianId;
}
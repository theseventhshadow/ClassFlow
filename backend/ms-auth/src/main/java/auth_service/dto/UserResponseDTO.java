package auth_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String idNumber;
    private String email;
    private String role;
    private String course;
    private Boolean active;
}

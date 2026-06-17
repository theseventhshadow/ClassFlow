package assistance_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnotationRequestDTO {
    @NotNull
    private Long studentId;
    
    @NotNull
    private Long teacherId;
    
    @NotBlank
    private String type;  // "POSITIVE" or "NEGATIVE"
    
    @NotBlank
    private String description;
}

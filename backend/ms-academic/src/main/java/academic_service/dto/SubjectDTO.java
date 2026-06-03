package academic_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectDTO {
    private Long id;
    private String name;
    private String description;
    private Long courseId;
    private Boolean active;
}
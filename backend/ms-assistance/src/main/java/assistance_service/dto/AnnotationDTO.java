package assistance_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AnnotationDTO {
    private Long id;
    private Long studentId;
    private Long teacherId;
    private String type;
    private String description;
    private LocalDateTime date;
    private Boolean active;
}

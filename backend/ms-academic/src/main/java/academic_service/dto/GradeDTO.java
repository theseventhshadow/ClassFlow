package academic_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeDTO {
    private Long id;
    private Long studentId;
    private Double score;
    private String observations;
    private Long evaluationId;
}
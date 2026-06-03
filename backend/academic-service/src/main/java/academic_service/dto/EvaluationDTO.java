package academic_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class EvaluationDTO {
    private Long id;
    private String name;
    private String description;
    private Double maxScore;
    private Double percentage;
    private LocalDate date;
    private Long subjectId;
}
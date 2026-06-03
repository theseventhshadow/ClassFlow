package assistance_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class AttendanceDTO {
    private Long id;
    private Long studentId;
    private Long courseId;
    private LocalDate date;
    private Boolean present;
    private String justification;
}

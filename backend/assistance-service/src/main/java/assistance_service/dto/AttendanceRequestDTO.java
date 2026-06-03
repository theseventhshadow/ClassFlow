package assistance_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class AttendanceRequestDTO {
    @NotNull
    private Long studentId;
    
    @NotNull
    private Long courseId;
    
    @NotNull
    private LocalDate date;
    
    @NotNull
    private Boolean present;
    
    private String justification;
}

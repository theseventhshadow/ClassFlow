package bff_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class RecentActivityDTO {
    private Long id;
    private String type;                // success, warning, info, error
    private String text;
    private LocalDateTime timestamp;
}
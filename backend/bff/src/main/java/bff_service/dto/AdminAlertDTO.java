package bff_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminAlertDTO {
    private Long id;
    private String text;
    private String severity;            // low, medium, high
}
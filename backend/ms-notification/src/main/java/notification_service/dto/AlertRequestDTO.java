package notification_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import notification_service.enums.NotificationType;

@Data
public class AlertRequestDTO {
    @NotNull
    private Long userId;

    @NotNull
    private NotificationType type;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    private String userEmail; // opcional: para enviar también por email
}
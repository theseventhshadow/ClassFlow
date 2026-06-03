package notification_service.dto;

import lombok.Data;
import notification_service.enums.NotificationType;
import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Long id;
    private Long userId;
    private NotificationType type;
    private String subject;
    private String content;
    private Boolean sent;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
    private String errorMessage;
}
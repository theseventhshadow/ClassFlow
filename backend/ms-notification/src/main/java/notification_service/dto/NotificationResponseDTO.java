package notification_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import notification_service.enums.NotificationType;
import java.time.LocalDateTime;

@Data
@Schema(description = "Respuesta de una notificación del sistema")
public class NotificationResponseDTO {

    @Schema(description = "ID de la notificación", example = "1")
    private Long id;

    @Schema(description = "ID del usuario destinatario", example = "3")
    private Long userId;

    @Schema(description = "Tipo de notificación", example = "ABSENCE")
    private NotificationType type;

    @Schema(description = "Asunto de la notificación", example = "Inasistencia registrada")
    private String subject;

    @Schema(description = "Contenido de la notificación")
    private String content;

    @Schema(description = "Indica si fue enviada", example = "true")
    private Boolean sent;

    @Schema(description = "Fecha y hora de envío")
    private LocalDateTime sentAt;

    @Schema(description = "Fecha y hora de creación")
    private LocalDateTime createdAt;

    @Schema(description = "Mensaje de error si el envío falló")
    private String errorMessage;
}

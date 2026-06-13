package message_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Mensaje privado entre usuarios")
public class MessageDTO {

    @Schema(description = "ID del mensaje", example = "1")
    private Long id;

    @Schema(description = "ID del usuario que envía el mensaje", example = "1")
    private Long senderId;

    @Schema(description = "ID del usuario que recibe el mensaje", example = "2")
    private Long receiverId;

    @Schema(description = "Asunto del mensaje", example = "Reunión de apoderados")
    private String subject;

    @Schema(description = "Cuerpo del mensaje", example = "Estimado apoderado, le informamos que...")
    private String body;

    @Schema(description = "Indica si el mensaje fue leído", example = "false")
    private Boolean read;

    @Schema(description = "Fecha y hora de envío")
    private LocalDateTime sentAt;
}

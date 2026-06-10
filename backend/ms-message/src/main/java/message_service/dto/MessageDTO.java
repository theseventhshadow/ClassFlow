package message_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class MessageDTO {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String subject;
    private String body;
    private Boolean read;
    private LocalDateTime sentAt;
}

package message_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class AnnouncementDTO {
    private Long id;
    private String title;
    private String content;
    private Long courseId;
    private Long senderId;
    private LocalDateTime publishedAt;
    private Boolean active;
}

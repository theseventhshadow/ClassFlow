package message_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnnouncementRequestDTO {
    @NotBlank
    private String title;
    
    @NotBlank
    private String content;
    
    private Long courseId;  // null = all courses
    
    @NotNull
    private Long senderId;
}

package assistance_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Anotación conductual de un estudiante")
public class AnnotationDTO {

    @Schema(description = "ID de la anotación", example = "1")
    private Long id;

    @Schema(description = "ID del estudiante", example = "3")
    private Long studentId;

    @Schema(description = "ID del docente que registró la anotación", example = "2")
    private Long teacherId;

    @Schema(description = "Tipo de anotación", example = "POSITIVE", allowableValues = {"POSITIVE", "NEGATIVE"})
    private String type;

    @Schema(description = "Descripción de la anotación", example = "Excelente participación en clases")
    private String description;

    @Schema(description = "Fecha y hora de la anotación")
    private LocalDateTime date;

    @Schema(description = "Indica si la anotación está activa", example = "true")
    private Boolean active;
}

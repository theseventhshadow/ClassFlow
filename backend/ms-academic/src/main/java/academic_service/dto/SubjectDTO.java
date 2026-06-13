package academic_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Datos de una asignatura")
public class SubjectDTO {

    @Schema(description = "ID de la asignatura", example = "1")
    private Long id;

    @Schema(description = "Nombre de la asignatura", example = "Matemáticas")
    private String name;

    @Schema(description = "Descripción de la asignatura")
    private String description;

    @Schema(description = "ID del curso al que pertenece", example = "1")
    private Long courseId;

    @Schema(description = "Indica si la asignatura está activa", example = "true")
    private Boolean active;
}

package academic_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Datos de un curso académico")
public class CourseDTO {

    @Schema(description = "ID del curso", example = "1")
    private Long id;

    @Schema(description = "Nombre del curso", example = "1° Medio A")
    private String name;

    @Schema(description = "Descripción del curso", example = "Primer año de enseñanza media, sección A")
    private String description;

    @Schema(description = "Año académico", example = "2025")
    private Integer academicYear;

    @Schema(description = "Indica si el curso está activo", example = "true")
    private Boolean active;
}

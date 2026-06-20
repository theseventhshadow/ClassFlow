package com.ohiggins.classflow.assistance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
@Schema(description = "Registro de asistencia de un estudiante")
public class AttendanceDTO {

    @Schema(description = "ID del registro", example = "1")
    private Long id;

    @Schema(description = "ID del estudiante", example = "3")
    private Long studentId;

    @Schema(description = "ID del curso", example = "1")
    private Long courseId;

    @Schema(description = "Fecha de la asistencia", example = "2025-06-13")
    private LocalDate date;

    @Schema(description = "Indica si el estudiante estuvo presente", example = "true")
    private Boolean present;

    @Schema(description = "Justificación en caso de ausencia", example = "Certificado médico")
    private String justification;
}

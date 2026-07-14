package com.ohiggins.classflow.assistance.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

/**
 * Datos de entrada para registrar o actualizar asistencia.
 */
@Data
public class AttendanceRequestDTO {
    @NotNull
    private Long studentId;
    
    @NotNull
    private Long courseId;
    
    @NotNull
    private LocalDate date;
    
    @NotNull
    private Boolean present;
    
    private String justification;
}

package com.ohiggins.classflow.assistance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Datos de entrada para crear una anotacion conductual.
 */
@Data
public class AnnotationRequestDTO {
    @NotNull
    private Long studentId;
    
    @NotNull
    private Long teacherId;
    
    @NotBlank
    private String type;  // "POSITIVE" or "NEGATIVE"
    
    @NotBlank
    private String description;
}

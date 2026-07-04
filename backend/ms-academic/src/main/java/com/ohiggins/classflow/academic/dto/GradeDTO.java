package com.ohiggins.classflow.academic.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Representa una calificacion de estudiante.
 */
@Data
@Builder
public class GradeDTO {
    private Long id;
    private Long studentId;
    private Double score;
    private String observations;
    private Long evaluationId;
}
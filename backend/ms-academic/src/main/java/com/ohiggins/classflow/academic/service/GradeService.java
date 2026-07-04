package com.ohiggins.classflow.academic.service;

import com.ohiggins.classflow.academic.dto.GradeDTO;
import com.ohiggins.classflow.academic.entity.Evaluation;
import com.ohiggins.classflow.academic.entity.Grade;
import com.ohiggins.classflow.academic.repository.EvaluationRepository;
import com.ohiggins.classflow.academic.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene la logica de negocio para la gestion de calificaciones.
 */
@Service
@RequiredArgsConstructor
public class GradeService {
    
    private final GradeRepository gradeRepository;
    private final EvaluationRepository evaluationRepository;
    
    /**
     * Obtiene todas las calificaciones con sus relaciones.
     *
     * @return lista de calificaciones convertidas a DTO.
     */
    public List<GradeDTO> findAll() {
        return gradeRepository.findAllWithDetails().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las calificaciones de un estudiante.
     *
     * @param studentId identificador del estudiante.
     * @return lista de calificaciones convertidas a DTO.
     */
    public List<GradeDTO> findByStudentId(Long studentId) {
        return gradeRepository.findByStudentIdWithDetails(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las calificaciones de una evaluacion.
     *
     * @param evaluationId identificador de la evaluacion.
     * @return lista de calificaciones convertidas a DTO.
     */
    public List<GradeDTO> findByEvaluationId(Long evaluationId) {
        return gradeRepository.findByEvaluationId(evaluationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca una calificacion por su identificador.
     *
     * @param id identificador de la calificacion.
     * @return calificacion encontrada convertida a DTO.
     */
    public GradeDTO findById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        return convertToDTO(grade);
    }
    
    /**
     * Registra una nueva calificacion para una evaluacion.
     *
     * @param dto datos de la calificacion a crear.
     * @return calificacion guardada convertida a DTO.
     */
    public GradeDTO create(GradeDTO dto) {
        Evaluation evaluation = evaluationRepository.findById(dto.getEvaluationId())
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        
        // Validar que la nota no supere la máxima
        if (dto.getScore() > evaluation.getMaxScore()) {
            throw new RuntimeException("Score cannot exceed maximum score of " + evaluation.getMaxScore());
        }
        
        Grade grade = new Grade();
        grade.setStudentId(dto.getStudentId());
        grade.setScore(dto.getScore());
        grade.setObservations(dto.getObservations());
        grade.setEvaluation(evaluation);
        
        Grade saved = gradeRepository.save(grade);
        return convertToDTO(saved);
    }
    
    /**
     * Actualiza una calificacion existente.
     *
     * @param id identificador de la calificacion.
     * @param dto datos actualizados de la calificacion.
     * @return calificacion actualizada convertida a DTO.
     */
    public GradeDTO update(Long id, GradeDTO dto) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        
        grade.setScore(dto.getScore());
        grade.setObservations(dto.getObservations());
        
        Grade updated = gradeRepository.save(grade);
        return convertToDTO(updated);
    }
    
    /**
     * Elimina una calificacion por su identificador.
     *
     * @param id identificador de la calificacion.
     */
    public void delete(Long id) {
        gradeRepository.deleteById(id);
    }
    
    private GradeDTO convertToDTO(Grade grade) {
        return GradeDTO.builder()
                .id(grade.getId())
                .studentId(grade.getStudentId())
                .score(grade.getScore())
                .observations(grade.getObservations())
                .evaluationId(grade.getEvaluation() != null ? grade.getEvaluation().getId() : null)
                .build();
    }
}
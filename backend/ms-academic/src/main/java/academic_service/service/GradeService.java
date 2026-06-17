package academic_service.service;

import academic_service.dto.GradeDTO;
import academic_service.entity.Evaluation;
import academic_service.entity.Grade;
import academic_service.repository.EvaluationRepository;
import academic_service.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeService {
    
    private final GradeRepository gradeRepository;
    private final EvaluationRepository evaluationRepository;
    
    public List<GradeDTO> findAll() {
        return gradeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<GradeDTO> findByStudentId(Long studentId) {
        return gradeRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<GradeDTO> findByEvaluationId(Long evaluationId) {
        return gradeRepository.findByEvaluationId(evaluationId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public GradeDTO findById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        return convertToDTO(grade);
    }
    
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
    
    public GradeDTO update(Long id, GradeDTO dto) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        
        grade.setScore(dto.getScore());
        grade.setObservations(dto.getObservations());
        
        Grade updated = gradeRepository.save(grade);
        return convertToDTO(updated);
    }
    
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
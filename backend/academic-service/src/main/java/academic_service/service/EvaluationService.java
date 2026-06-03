package academic_service.service;

import academic_service.dto.EvaluationDTO;
import academic_service.entity.Evaluation;
import academic_service.entity.Subject;
import academic_service.repository.EvaluationRepository;
import academic_service.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    
    private final EvaluationRepository evaluationRepository;
    private final SubjectRepository subjectRepository;
    
    public List<EvaluationDTO> findAll() {
        return evaluationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<EvaluationDTO> findBySubjectId(Long subjectId) {
        return evaluationRepository.findBySubjectId(subjectId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public EvaluationDTO findById(Long id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        return convertToDTO(evaluation);
    }
    
    public EvaluationDTO create(EvaluationDTO dto) {
        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        
        Evaluation evaluation = new Evaluation();
        evaluation.setName(dto.getName());
        evaluation.setDescription(dto.getDescription());
        evaluation.setMaxScore(dto.getMaxScore());
        evaluation.setPercentage(dto.getPercentage());
        evaluation.setDate(dto.getDate());
        evaluation.setSubject(subject);
        
        Evaluation saved = evaluationRepository.save(evaluation);
        return convertToDTO(saved);
    }
    
    public EvaluationDTO update(Long id, EvaluationDTO dto) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        
        evaluation.setName(dto.getName());
        evaluation.setDescription(dto.getDescription());
        evaluation.setMaxScore(dto.getMaxScore());
        evaluation.setPercentage(dto.getPercentage());
        evaluation.setDate(dto.getDate());
        
        if (dto.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            evaluation.setSubject(subject);
        }
        
        Evaluation updated = evaluationRepository.save(evaluation);
        return convertToDTO(updated);
    }
    
    public void delete(Long id) {
        evaluationRepository.deleteById(id);
    }
    
    private EvaluationDTO convertToDTO(Evaluation evaluation) {
        return EvaluationDTO.builder()
                .id(evaluation.getId())
                .name(evaluation.getName())
                .description(evaluation.getDescription())
                .maxScore(evaluation.getMaxScore())
                .percentage(evaluation.getPercentage())
                .date(evaluation.getDate())
                .subjectId(evaluation.getSubject() != null ? evaluation.getSubject().getId() : null)
                .build();
    }
}
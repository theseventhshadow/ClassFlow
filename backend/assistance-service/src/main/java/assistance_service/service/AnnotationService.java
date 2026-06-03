package assistance_service.service;

import assistance_service.dto.AnnotationDTO;
import assistance_service.dto.AnnotationRequestDTO;
import assistance_service.entity.Annotation;
import assistance_service.repository.AnnotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnotationService {
    
    private final AnnotationRepository annotationRepository;
    
    public List<AnnotationDTO> findAll() {
        return annotationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AnnotationDTO> findByStudentId(Long studentId) {
        return annotationRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AnnotationDTO> findByStudentIdAndType(Long studentId, String type) {
        return annotationRepository.findByStudentIdAndType(studentId, type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public AnnotationDTO create(AnnotationRequestDTO request) {
        if (!request.getType().equals("POSITIVE") && !request.getType().equals("NEGATIVE")) {
            throw new RuntimeException("Invalid annotation type. Use 'POSITIVE' or 'NEGATIVE'");
        }
        
        Annotation annotation = new Annotation();
        annotation.setStudentId(request.getStudentId());
        annotation.setTeacherId(request.getTeacherId());
        annotation.setType(request.getType());
        annotation.setDescription(request.getDescription());
        annotation.setDate(LocalDateTime.now());
        annotation.setActive(true);
        
        Annotation saved = annotationRepository.save(annotation);
        return convertToDTO(saved);
    }
    
    public void delete(Long id) {
        Annotation annotation = annotationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Annotation not found"));
        annotation.setActive(false);
        annotationRepository.save(annotation);
    }
    
    private AnnotationDTO convertToDTO(Annotation annotation) {
        return AnnotationDTO.builder()
                .id(annotation.getId())
                .studentId(annotation.getStudentId())
                .teacherId(annotation.getTeacherId())
                .type(annotation.getType())
                .description(annotation.getDescription())
                .date(annotation.getDate())
                .active(annotation.getActive())
                .build();
    }
}

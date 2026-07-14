package com.ohiggins.classflow.assistance.service;

import com.ohiggins.classflow.assistance.dto.AnnotationDTO;
import com.ohiggins.classflow.assistance.dto.AnnotationRequestDTO;
import com.ohiggins.classflow.assistance.entity.Annotation;
import com.ohiggins.classflow.assistance.repository.AnnotationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene la logica de negocio para la gestion de anotaciones.
 */
@Service
@RequiredArgsConstructor
public class AnnotationService {
    
    private final AnnotationRepository annotationRepository;
    
    /**
     * Obtiene todas las anotaciones.
     *
     * @return lista de anotaciones convertidas a DTO.
     */
    public List<AnnotationDTO> findAll() {
        return annotationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las anotaciones de un estudiante.
     *
     * @param studentId identificador del estudiante.
     * @return lista de anotaciones convertidas a DTO.
     */
    public List<AnnotationDTO> findByStudentId(Long studentId) {
        return annotationRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las anotaciones de un estudiante filtradas por tipo.
     *
     * @param studentId identificador del estudiante.
     * @param type tipo de anotacion.
     * @return lista de anotaciones convertidas a DTO.
     */
    public List<AnnotationDTO> findByStudentIdAndType(Long studentId, String type) {
        return annotationRepository.findByStudentIdAndType(studentId, type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea una nueva anotacion validando su tipo.
     *
     * @param request datos de la anotacion a crear.
     * @return anotacion guardada convertida a DTO.
     */
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
    
    /**
     * Desactiva una anotacion existente.
     *
     * @param id identificador de la anotacion.
     */
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

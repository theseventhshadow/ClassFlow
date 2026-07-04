package com.ohiggins.classflow.academic.service;

import com.ohiggins.classflow.academic.dto.SubjectDTO;
import com.ohiggins.classflow.academic.entity.Course;
import com.ohiggins.classflow.academic.entity.Subject;
import com.ohiggins.classflow.academic.repository.CourseRepository;
import com.ohiggins.classflow.academic.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene la logica de negocio para la gestion de asignaturas.
 */
@Service
@RequiredArgsConstructor
public class SubjectService {
    
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    
    /**
     * Obtiene todas las asignaturas disponibles.
     *
     * @return lista de asignaturas convertidas a DTO.
     */
    public List<SubjectDTO> findAll() {
        return subjectRepository.findAllWithCourse().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene las asignaturas pertenecientes a un curso.
     *
     * @param courseId identificador del curso.
     * @return lista de asignaturas convertidas a DTO.
     */
    public List<SubjectDTO> findByCourseId(Long courseId) {
        return subjectRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca una asignatura por su identificador.
     *
     * @param id identificador de la asignatura.
     * @return asignatura encontrada convertida a DTO.
     */
    public SubjectDTO findById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        return convertToDTO(subject);
    }
    
    /**
     * Crea una nueva asignatura asociada a un curso.
     *
     * @param dto datos de la asignatura a crear.
     * @return asignatura guardada convertida a DTO.
     */
    public SubjectDTO create(SubjectDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        Subject subject = new Subject();
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        subject.setCourse(course);
        subject.setActive(true);
        
        Subject saved = subjectRepository.save(subject);
        return convertToDTO(saved);
    }
    
    /**
     * Actualiza los datos de una asignatura existente.
     *
     * @param id identificador de la asignatura.
     * @param dto datos actualizados de la asignatura.
     * @return asignatura actualizada convertida a DTO.
     */
    public SubjectDTO update(Long id, SubjectDTO dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        
        subject.setName(dto.getName());
        subject.setDescription(dto.getDescription());
        
        if (dto.getCourseId() != null) {
            Course course = courseRepository.findById(dto.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));
            subject.setCourse(course);
        }
        
        Subject updated = subjectRepository.save(subject);
        return convertToDTO(updated);
    }
    
    /**
     * Desactiva una asignatura existente.
     *
     * @param id identificador de la asignatura.
     */
    public void delete(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        subject.setActive(false);
        subjectRepository.save(subject);
    }
    
    private SubjectDTO convertToDTO(Subject subject) {
        return SubjectDTO.builder()
                .id(subject.getId())
                .name(subject.getName())
                .description(subject.getDescription())
                .courseId(subject.getCourse() != null ? subject.getCourse().getId() : null)
                .active(subject.getActive())
                .build();
    }
}
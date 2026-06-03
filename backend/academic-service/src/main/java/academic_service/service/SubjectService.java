package academic_service.service;

import academic_service.dto.SubjectDTO;
import academic_service.entity.Course;
import academic_service.entity.Subject;
import academic_service.repository.CourseRepository;
import academic_service.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectService {
    
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    
    public List<SubjectDTO> findAll() {
        return subjectRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SubjectDTO> findByCourseId(Long courseId) {
        return subjectRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public SubjectDTO findById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found"));
        return convertToDTO(subject);
    }
    
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
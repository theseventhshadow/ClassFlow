package academic_service.service;

import academic_service.dto.CourseDTO;
import academic_service.entity.Course;
import academic_service.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    
    private final CourseRepository courseRepository;
    
    public List<CourseDTO> findAll() {
        return courseRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public CourseDTO findById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        return convertToDTO(course);
    }
    
    public CourseDTO create(CourseDTO dto) {
        if (courseRepository.findByName(dto.getName()).isPresent()) {
            throw new RuntimeException("Course already exists");
        }
        
        Course course = new Course();
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setAcademicYear(dto.getAcademicYear());  // ← Cambiado
        course.setActive(true);
        
        Course saved = courseRepository.save(course);
        return convertToDTO(saved);
    }
    
    public CourseDTO update(Long id, CourseDTO dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        
        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setAcademicYear(dto.getAcademicYear());  // ← Cambiado
        
        Course updated = courseRepository.save(course);
        return convertToDTO(updated);
    }
    
    public void delete(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        course.setActive(false);
        courseRepository.save(course);
    }
    
    private CourseDTO convertToDTO(Course course) {
        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .academicYear(course.getAcademicYear())  // ← Cambiado
                .active(course.getActive())
                .build();
    }
}
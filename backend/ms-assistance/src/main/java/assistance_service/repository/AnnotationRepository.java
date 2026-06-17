package assistance_service.repository;

import assistance_service.entity.Annotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnnotationRepository extends JpaRepository<Annotation, Long> {
    List<Annotation> findByStudentId(Long studentId);
    List<Annotation> findByStudentIdAndType(Long studentId, String type);
    List<Annotation> findByTeacherId(Long teacherId);
}

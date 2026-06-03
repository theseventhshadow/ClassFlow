package academic_service.repository;

import academic_service.entity.Subject;
import academic_service.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByCourse(Course course);
    List<Subject> findByCourseId(Long courseId);
}
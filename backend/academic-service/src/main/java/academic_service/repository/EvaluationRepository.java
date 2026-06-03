package academic_service.repository;

import academic_service.entity.Evaluation;
import academic_service.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findBySubject(Subject subject);
    List<Evaluation> findBySubjectId(Long subjectId);
}
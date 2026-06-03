package academic_service.repository;

import academic_service.entity.Grade;
import academic_service.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    List<Grade> findByEvaluation(Evaluation evaluation);
    List<Grade> findByEvaluationId(Long evaluationId);
}
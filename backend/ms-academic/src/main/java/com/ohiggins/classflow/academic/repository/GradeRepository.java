package com.ohiggins.classflow.academic.repository;

import com.ohiggins.classflow.academic.entity.Grade;
import com.ohiggins.classflow.academic.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    @Query("SELECT g FROM Grade g JOIN FETCH g.evaluation e JOIN FETCH e.subject s JOIN FETCH s.course")
    List<Grade> findAllWithDetails();

    @Query("SELECT g FROM Grade g JOIN FETCH g.evaluation e JOIN FETCH e.subject s JOIN FETCH s.course WHERE g.studentId = :studentId")
    List<Grade> findByStudentIdWithDetails(Long studentId);

    List<Grade> findByEvaluation(Evaluation evaluation);
    List<Grade> findByEvaluationId(Long evaluationId);
}
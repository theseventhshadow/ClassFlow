package com.ohiggins.classflow.academic.repository;

import com.ohiggins.classflow.academic.entity.Evaluation;
import com.ohiggins.classflow.academic.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findBySubject(Subject subject);
    List<Evaluation> findBySubjectId(Long subjectId);
}
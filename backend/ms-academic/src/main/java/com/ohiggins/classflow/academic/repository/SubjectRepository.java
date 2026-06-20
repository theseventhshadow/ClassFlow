package com.ohiggins.classflow.academic.repository;

import com.ohiggins.classflow.academic.entity.Subject;
import com.ohiggins.classflow.academic.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    @Query("SELECT s FROM Subject s JOIN FETCH s.course")
    List<Subject> findAllWithCourse();

    List<Subject> findByCourse(Course course);
    List<Subject> findByCourseId(Long courseId);
}
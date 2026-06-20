package com.ohiggins.classflow.assistance.repository;

import com.ohiggins.classflow.assistance.entity.Annotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("AnnotationRepository Tests")
class AnnotationRepositoryTest {

    @Autowired
    private AnnotationRepository annotationRepository;

    private Annotation annotation;

    @BeforeEach
    void setUp() {
        annotation = new Annotation();
        annotation.setStudentId(101L);
        annotation.setTeacherId(501L);
        annotation.setType("POSITIVE");
        annotation.setDescription("Great improvement");
        annotation.setDate(LocalDateTime.now());
        annotation.setActive(true);
    }

    @Test
    @DisplayName("Should save and retrieve annotation")
    void testSaveAndFindAnnotation() {
        Annotation saved = annotationRepository.save(annotation);

        assertThat(saved)
                .isNotNull()
                .extracting(Annotation::getStudentId, Annotation::getType, Annotation::getActive)
                .containsExactly(101L, "POSITIVE", true);
    }

    @Test
    @DisplayName("Should find annotation by student id")
    void testFindByStudentId() {
        annotationRepository.save(annotation);

        assertThat(annotationRepository.findByStudentId(101L))
                .hasSize(1)
                .extracting(Annotation::getTeacherId)
                .containsExactly(501L);
    }

    @Test
    @DisplayName("Should find annotation by student id and type")
    void testFindByStudentIdAndType() {
        annotationRepository.save(annotation);

        assertThat(annotationRepository.findByStudentIdAndType(101L, "POSITIVE"))
                .hasSize(1)
                .extracting(Annotation::getDescription)
                .containsExactly("Great improvement");
    }

    @Test
    @DisplayName("Should find annotation by teacher id")
    void testFindByTeacherId() {
        annotationRepository.save(annotation);

        assertThat(annotationRepository.findByTeacherId(501L))
                .hasSize(1)
                .extracting(Annotation::getStudentId)
                .containsExactly(101L);
    }
}
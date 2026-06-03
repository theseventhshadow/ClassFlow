package academic_service.repository;

import academic_service.entity.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("CourseRepository Tests")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setName("Matemáticas");
        course.setDescription("Curso de Matemáticas");
        course.setAcademicYear(2024);
        course.setActive(true);
    }

    @Test
    @DisplayName("Should save and retrieve course")
    void testSaveAndFindCourse() {
        // When
        Course saved = courseRepository.save(course);

        // Then
        assertThat(saved)
                .isNotNull()
                .extracting(Course::getName, Course::getActive)
                .containsExactly("Matemáticas", true);
    }

    @Test
    @DisplayName("Should find course by name")
    void testFindByName() {
        // Given
        courseRepository.save(course);

        // When
        Optional<Course> result = courseRepository.findByName("Matemáticas");

        // Then
        assertThat(result)
                .isPresent()
                .get()
                .extracting(Course::getName)
                .isEqualTo("Matemáticas");
    }

    @Test
    @DisplayName("Should not find non-existent course by name")
    void testFindByNameNotFound() {
        // When
        Optional<Course> result = courseRepository.findByName("NoExiste");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find all courses")
    void testFindAll() {
        // Given
        Course course2 = new Course();
        course2.setName("Física");
        course2.setDescription("Curso de Física");
        course2.setAcademicYear(2024);
        course2.setActive(true);

        courseRepository.save(course);
        courseRepository.save(course2);

        // When
        var allCourses = courseRepository.findAll();

        // Then
        assertThat(allCourses)
                .hasSize(2)
                .extracting(Course::getName)
                .containsExactlyInAnyOrder("Matemáticas", "Física");
    }

    @Test
    @DisplayName("Should update course")
    void testUpdateCourse() {
        // Given
        Course saved = courseRepository.save(course);
        Long id = saved.getId();

        // When
        saved.setDescription("Descripción Actualizada");
        Course updated = courseRepository.save(saved);

        // Then
        assertThat(updated)
                .extracting(Course::getDescription)
                .isEqualTo("Descripción Actualizada");
    }

    @Test
    @DisplayName("Should delete course")
    void testDeleteCourse() {
        // Given
        Course saved = courseRepository.save(course);
        Long id = saved.getId();

        // When
        courseRepository.deleteById(id);

        // Then
        assertThat(courseRepository.findById(id)).isEmpty();
    }
}

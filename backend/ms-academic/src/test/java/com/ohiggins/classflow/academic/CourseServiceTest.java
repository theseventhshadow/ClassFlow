package com.ohiggins.classflow.academic.service;

import com.ohiggins.classflow.academic.dto.CourseDTO;
import com.ohiggins.classflow.academic.entity.Course;
import com.ohiggins.classflow.academic.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private Course course;
    private CourseDTO courseDTO;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setName("Matemáticas");
        course.setDescription("Curso de Matemáticas");
        course.setAcademicYear(2024);
        course.setActive(true);

        courseDTO = CourseDTO.builder()
                .id(1L)
                .name("Matemáticas")
                .description("Curso de Matemáticas")
                .academicYear(2024)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should find all courses")
    void testFindAll() {
        // Given
        Course course2 = new Course();
        course2.setId(2L);
        course2.setName("Física");
        course2.setDescription("Curso de Física");
        course2.setAcademicYear(2024);
        course2.setActive(true);

        when(courseRepository.findAll()).thenReturn(Arrays.asList(course, course2));

        // When
        List<CourseDTO> result = courseService.findAll();

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(CourseDTO::getName)
                .containsExactly("Matemáticas", "Física");

        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find course by id")
    void testFindById() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        // When
        CourseDTO result = courseService.findById(1L);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(CourseDTO::getName)
                .isEqualTo("Matemáticas");

        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when course not found by id")
    void testFindByIdNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> courseService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found");

        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create course successfully")
    void testCreate() {
        // Given
        CourseDTO newCourseDTO = CourseDTO.builder()
                .name("Historia")
                .description("Curso de Historia")
                .academicYear(2024)
                .build();

        Course newCourse = new Course();
        newCourse.setId(3L);
        newCourse.setName("Historia");
        newCourse.setDescription("Curso de Historia");
        newCourse.setAcademicYear(2024);
        newCourse.setActive(true);

        when(courseRepository.findByName("Historia")).thenReturn(Optional.empty());
        when(courseRepository.save(any(Course.class))).thenReturn(newCourse);

        // When
        CourseDTO result = courseService.create(newCourseDTO);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(CourseDTO::getName, CourseDTO::getActive)
                .containsExactly("Historia", true);

        verify(courseRepository, times(1)).findByName("Historia");
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when creating duplicate course")
    void testCreateDuplicate() {
        // Given
        CourseDTO duplicateCourseDTO = CourseDTO.builder()
                .name("Matemáticas")
                .description("Curso de Matemáticas")
                .academicYear(2024)
                .build();

        when(courseRepository.findByName("Matemáticas")).thenReturn(Optional.of(course));

        // When & Then
        assertThatThrownBy(() -> courseService.create(duplicateCourseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course already exists");

        verify(courseRepository, times(1)).findByName("Matemáticas");
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update course successfully")
    void testUpdate() {
        // Given
        CourseDTO updateDTO = CourseDTO.builder()
                .name("Matemáticas Avanzadas")
                .description("Curso de Matemáticas Nivel Avanzado")
                .academicYear(2025)
                .build();

        Course updatedCourse = new Course();
        updatedCourse.setId(1L);
        updatedCourse.setName("Matemáticas Avanzadas");
        updatedCourse.setDescription("Curso de Matemáticas Nivel Avanzado");
        updatedCourse.setAcademicYear(2025);
        updatedCourse.setActive(true);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(updatedCourse);

        // When
        CourseDTO result = courseService.update(1L, updateDTO);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(CourseDTO::getName)
                .isEqualTo("Matemáticas Avanzadas");

        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when updating non-existent course")
    void testUpdateNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> courseService.update(999L, courseDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found");

        verify(courseRepository, times(1)).findById(999L);
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete course by setting active to false")
    void testDelete() {
        // Given
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        // When
        courseService.delete(1L);

        // Then
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when deleting non-existent course")
    void testDeleteNotFound() {
        // Given
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> courseService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found");

        verify(courseRepository, times(1)).findById(999L);
        verify(courseRepository, never()).save(any());
    }
}

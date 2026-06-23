package com.ohiggins.classflow.academic.service;

import com.ohiggins.classflow.academic.dto.SubjectDTO;
import com.ohiggins.classflow.academic.entity.Course;
import com.ohiggins.classflow.academic.entity.Subject;
import com.ohiggins.classflow.academic.repository.CourseRepository;
import com.ohiggins.classflow.academic.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SubjectService Tests")
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private SubjectService subjectService;

    private Subject subject;
    private SubjectDTO subjectDTO;
    private Course course;

    @BeforeEach
    void setUp() {
        course = new Course();
        course.setId(1L);
        course.setName("1° Medio");
        course.setActive(true);

        subject = new Subject();
        subject.setId(1L);
        subject.setName("Matemáticas");
        subject.setDescription("Asignatura de Matemáticas");
        subject.setCourse(course);
        subject.setActive(true);

        subjectDTO = SubjectDTO.builder()
                .id(1L)
                .name("Matemáticas")
                .description("Asignatura de Matemáticas")
                .courseId(1L)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should find all subjects")
    void testFindAll() {
        Subject subject2 = new Subject();
        subject2.setId(2L);
        subject2.setName("Física");
        subject2.setDescription("Asignatura de Física");
        subject2.setCourse(course);
        subject2.setActive(true);

        when(subjectRepository.findAllWithCourse()).thenReturn(Arrays.asList(subject, subject2));

        List<SubjectDTO> result = subjectService.findAll();

        assertThat(result)
                .hasSize(2)
                .extracting(SubjectDTO::getName)
                .containsExactly("Matemáticas", "Física");

        verify(subjectRepository, times(1)).findAllWithCourse();
    }

    @Test
    @DisplayName("Should find subjects by course id")
    void testFindByCourseId() {
        when(subjectRepository.findByCourseId(1L)).thenReturn(Arrays.asList(subject));

        List<SubjectDTO> result = subjectService.findByCourseId(1L);

        assertThat(result)
                .hasSize(1)
                .extracting(SubjectDTO::getName)
                .containsExactly("Matemáticas");

        verify(subjectRepository, times(1)).findByCourseId(1L);
    }

    @Test
    @DisplayName("Should find subject by id")
    void testFindById() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        SubjectDTO result = subjectService.findById(1L);

        assertThat(result)
                .isNotNull()
                .extracting(SubjectDTO::getName)
                .isEqualTo("Matemáticas");

        verify(subjectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when subject not found")
    void testFindByIdNotFound() {
        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subject not found");

        verify(subjectRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create subject successfully")
    void testCreate() {
        SubjectDTO newSubjectDTO = SubjectDTO.builder()
                .name("Historia")
                .description("Asignatura de Historia")
                .courseId(1L)
                .build();

        Subject newSubject = new Subject();
        newSubject.setId(3L);
        newSubject.setName("Historia");
        newSubject.setDescription("Asignatura de Historia");
        newSubject.setCourse(course);
        newSubject.setActive(true);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(subjectRepository.save(any(Subject.class))).thenReturn(newSubject);

        SubjectDTO result = subjectService.create(newSubjectDTO);

        assertThat(result)
                .isNotNull()
                .extracting(SubjectDTO::getName, SubjectDTO::getActive)
                .containsExactly("Historia", true);

        verify(courseRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when course not found on create")
    void testCreateCourseNotFound() {
        SubjectDTO newSubjectDTO = SubjectDTO.builder()
                .name("Historia")
                .courseId(999L)
                .build();

        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.create(newSubjectDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Course not found");

        verify(courseRepository, times(1)).findById(999L);
        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update subject successfully")
    void testUpdate() {
        SubjectDTO updateDTO = SubjectDTO.builder()
                .name("Matemáticas Avanzadas")
                .description("Nivel avanzado")
                .courseId(1L)
                .build();

        Subject updatedSubject = new Subject();
        updatedSubject.setId(1L);
        updatedSubject.setName("Matemáticas Avanzadas");
        updatedSubject.setDescription("Nivel avanzado");
        updatedSubject.setCourse(course);
        updatedSubject.setActive(true);

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(subjectRepository.save(any(Subject.class))).thenReturn(updatedSubject);

        SubjectDTO result = subjectService.update(1L, updateDTO);

        assertThat(result)
                .isNotNull()
                .extracting(SubjectDTO::getName)
                .isEqualTo("Matemáticas Avanzadas");

        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when subject not found on update")
    void testUpdateNotFound() {
        SubjectDTO updateDTO = SubjectDTO.builder()
                .name("New Name")
                .build();

        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.update(999L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subject not found");

        verify(subjectRepository, times(1)).findById(999L);
        verify(subjectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete subject (soft delete)")
    void testDelete() {
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(any(Subject.class))).thenReturn(subject);

        subjectService.delete(1L);

        assertThat(subject.getActive()).isFalse();
        verify(subjectRepository, times(1)).findById(1L);
        verify(subjectRepository, times(1)).save(any(Subject.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when subject not found on delete")
    void testDeleteNotFound() {
        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subjectService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subject not found");

        verify(subjectRepository, times(1)).findById(999L);
        verify(subjectRepository, never()).save(any());
    }
}

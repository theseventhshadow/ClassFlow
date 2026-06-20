package com.ohiggins.classflow.academic.service;

import com.ohiggins.classflow.academic.dto.GradeDTO;
import com.ohiggins.classflow.academic.entity.Evaluation;
import com.ohiggins.classflow.academic.entity.Grade;
import com.ohiggins.classflow.academic.repository.EvaluationRepository;
import com.ohiggins.classflow.academic.repository.GradeRepository;
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
@DisplayName("GradeService Tests")
class GradeServiceTest {

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private GradeService gradeService;

    private Grade grade;
    private GradeDTO gradeDTO;
    private Evaluation evaluation;

    @BeforeEach
    void setUp() {
        evaluation = new Evaluation();
        evaluation.setId(1L);
        evaluation.setName("Test 1");
        evaluation.setMaxScore(100.0);

        grade = new Grade();
        grade.setId(1L);
        grade.setStudentId(101L);
        grade.setScore(85.5);
        grade.setObservations("Good performance");
        grade.setEvaluation(evaluation);

        gradeDTO = GradeDTO.builder()
                .id(1L)
                .studentId(101L)
                .evaluationId(1L)
                .score(85.5)
                .observations("Good performance")
                .build();
    }

    @Test
    @DisplayName("Should find all grades")
    void testFindAll() {
        // Given
        Grade grade2 = new Grade();
        grade2.setId(2L);
        grade2.setStudentId(102L);
        grade2.setScore(92.0);
        grade2.setEvaluation(evaluation);

        when(gradeRepository.findAllWithDetails()).thenReturn(Arrays.asList(grade, grade2));

        // When
        List<GradeDTO> result = gradeService.findAll();

        // Then
        assertThat(result)
                .hasSize(2)
                .extracting(GradeDTO::getStudentId)
                .containsExactly(101L, 102L);

        verify(gradeRepository, times(1)).findAllWithDetails();
    }

    @Test
    @DisplayName("Should find grades by student id")
    void testFindByStudentId() {
        // Given
        when(gradeRepository.findByStudentIdWithDetails(101L)).thenReturn(Arrays.asList(grade));

        // When
        List<GradeDTO> result = gradeService.findByStudentId(101L);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(GradeDTO::getStudentId)
                .containsExactly(101L);

        verify(gradeRepository, times(1)).findByStudentIdWithDetails(101L);
    }

    @Test
    @DisplayName("Should find grades by evaluation id")
    void testFindByEvaluationId() {
        // Given
        when(gradeRepository.findByEvaluationId(1L)).thenReturn(Arrays.asList(grade));

        // When
        List<GradeDTO> result = gradeService.findByEvaluationId(1L);

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting(GradeDTO::getEvaluationId)
                .containsExactly(1L);

        verify(gradeRepository, times(1)).findByEvaluationId(1L);
    }

    @Test
    @DisplayName("Should find grade by id")
    void testFindById() {
        // Given
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(grade));

        // When
        GradeDTO result = gradeService.findById(1L);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(GradeDTO::getScore)
                .isEqualTo(85.5);

        verify(gradeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create grade successfully")
    void testCreate() {
        // Given
        GradeDTO newGradeDTO = GradeDTO.builder()
                .studentId(103L)
                .evaluationId(1L)
                .score(90.0)
                .observations("Excellent")
                .build();

        Grade newGrade = new Grade();
        newGrade.setId(3L);
        newGrade.setStudentId(103L);
        newGrade.setScore(90.0);
        newGrade.setObservations("Excellent");
        newGrade.setEvaluation(evaluation);

        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));
        when(gradeRepository.save(any())).thenReturn(newGrade);

        // When
        GradeDTO result = gradeService.create(newGradeDTO);

        // Then
        assertThat(result)
                .isNotNull()
                .extracting(GradeDTO::getScore)
                .isEqualTo(90.0);

        verify(evaluationRepository, times(1)).findById(1L);
        verify(gradeRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw exception when score exceeds maximum")
    void testCreateScoreExceedsMax() {
        // Given
        GradeDTO invalidGradeDTO = GradeDTO.builder()
                .studentId(103L)
                .evaluationId(1L)
                .score(150.0) // Exceeds max 100.0
                .build();

        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));

        // When & Then
        assertThatThrownBy(() -> gradeService.create(invalidGradeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Score cannot exceed maximum score of 100.0");

        verify(evaluationRepository, times(1)).findById(1L);
        verify(gradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when evaluation not found")
    void testCreateEvaluationNotFound() {
        // Given
        GradeDTO newGradeDTO = GradeDTO.builder()
                .studentId(103L)
                .evaluationId(999L)
                .score(90.0)
                .build();

        when(evaluationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> gradeService.create(newGradeDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Evaluation not found");

        verify(evaluationRepository, times(1)).findById(999L);
        verify(gradeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete grade successfully")
    void testDelete() {
        // Given
        doNothing().when(gradeRepository).deleteById(1L);

        // When
        gradeService.delete(1L);

        // Then
        verify(gradeRepository, times(1)).deleteById(1L);
    }
}

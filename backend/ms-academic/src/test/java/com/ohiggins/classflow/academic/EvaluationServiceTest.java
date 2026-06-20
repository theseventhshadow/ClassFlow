package com.ohiggins.classflow.academic.service;

import com.ohiggins.classflow.academic.dto.EvaluationDTO;
import com.ohiggins.classflow.academic.entity.Evaluation;
import com.ohiggins.classflow.academic.entity.Subject;
import com.ohiggins.classflow.academic.repository.EvaluationRepository;
import com.ohiggins.classflow.academic.repository.SubjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluationService Tests")
class EvaluationServiceTest {

    @Mock
    private EvaluationRepository evaluationRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @InjectMocks
    private EvaluationService evaluationService;

    private Evaluation evaluation;
    private EvaluationDTO evaluationDTO;
    private Subject subject;

    @BeforeEach
    void setUp() {
        subject = new Subject();
        subject.setId(1L);
        subject.setName("Matemáticas");
        subject.setActive(true);

        evaluation = new Evaluation();
        evaluation.setId(1L);
        evaluation.setName("Prueba 1");
        evaluation.setDescription("Primera evaluación");
        evaluation.setMaxScore(100.0);
        evaluation.setPercentage(30.0);
        evaluation.setDate(LocalDate.of(2026, 4, 15));
        evaluation.setSubject(subject);

        evaluationDTO = EvaluationDTO.builder()
                .id(1L)
                .name("Prueba 1")
                .description("Primera evaluación")
                .maxScore(100.0)
                .percentage(30.0)
                .date(LocalDate.of(2026, 4, 15))
                .subjectId(1L)
                .build();
    }

    @Test
    @DisplayName("Should find all evaluations")
    void testFindAll() {
        Evaluation eval2 = new Evaluation();
        eval2.setId(2L);
        eval2.setName("Prueba 2");
        eval2.setDescription("Segunda evaluación");
        eval2.setMaxScore(50.0);
        eval2.setPercentage(20.0);
        eval2.setDate(LocalDate.of(2026, 5, 1));
        eval2.setSubject(subject);

        when(evaluationRepository.findAllWithDetails()).thenReturn(Arrays.asList(evaluation, eval2));

        List<EvaluationDTO> result = evaluationService.findAll();

        assertThat(result)
                .hasSize(2)
                .extracting(EvaluationDTO::getName)
                .containsExactly("Prueba 1", "Prueba 2");

        verify(evaluationRepository, times(1)).findAllWithDetails();
    }

    @Test
    @DisplayName("Should find evaluations by subject id")
    void testFindBySubjectId() {
        when(evaluationRepository.findBySubjectId(1L)).thenReturn(Arrays.asList(evaluation));

        List<EvaluationDTO> result = evaluationService.findBySubjectId(1L);

        assertThat(result)
                .hasSize(1)
                .extracting(EvaluationDTO::getName)
                .containsExactly("Prueba 1");

        verify(evaluationRepository, times(1)).findBySubjectId(1L);
    }

    @Test
    @DisplayName("Should find evaluation by id")
    void testFindById() {
        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));

        EvaluationDTO result = evaluationService.findById(1L);

        assertThat(result)
                .isNotNull()
                .extracting(EvaluationDTO::getName)
                .isEqualTo("Prueba 1");

        verify(evaluationRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw RuntimeException when evaluation not found")
    void testFindByIdNotFound() {
        when(evaluationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Evaluation not found");

        verify(evaluationRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create evaluation successfully")
    void testCreate() {
        EvaluationDTO newEvalDTO = EvaluationDTO.builder()
                .name("Prueba 3")
                .description("Tercera evaluación")
                .maxScore(70.0)
                .percentage(25.0)
                .date(LocalDate.of(2026, 6, 1))
                .subjectId(1L)
                .build();

        Evaluation newEval = new Evaluation();
        newEval.setId(3L);
        newEval.setName("Prueba 3");
        newEval.setDescription("Tercera evaluación");
        newEval.setMaxScore(70.0);
        newEval.setPercentage(25.0);
        newEval.setDate(LocalDate.of(2026, 6, 1));
        newEval.setSubject(subject);

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(newEval);

        EvaluationDTO result = evaluationService.create(newEvalDTO);

        assertThat(result)
                .isNotNull()
                .extracting(EvaluationDTO::getName, EvaluationDTO::getMaxScore)
                .containsExactly("Prueba 3", 70.0);

        verify(subjectRepository, times(1)).findById(1L);
        verify(evaluationRepository, times(1)).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when subject not found on create")
    void testCreateSubjectNotFound() {
        EvaluationDTO newEvalDTO = EvaluationDTO.builder()
                .name("Prueba 3")
                .subjectId(999L)
                .build();

        when(subjectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.create(newEvalDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subject not found");

        verify(subjectRepository, times(1)).findById(999L);
        verify(evaluationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update evaluation successfully")
    void testUpdate() {
        EvaluationDTO updateDTO = EvaluationDTO.builder()
                .name("Prueba 1 Actualizada")
                .description("Descripción actualizada")
                .maxScore(90.0)
                .percentage(35.0)
                .date(LocalDate.of(2026, 4, 20))
                .subjectId(1L)
                .build();

        Evaluation updatedEval = new Evaluation();
        updatedEval.setId(1L);
        updatedEval.setName("Prueba 1 Actualizada");
        updatedEval.setDescription("Descripción actualizada");
        updatedEval.setMaxScore(90.0);
        updatedEval.setPercentage(35.0);
        updatedEval.setDate(LocalDate.of(2026, 4, 20));
        updatedEval.setSubject(subject);

        when(evaluationRepository.findById(1L)).thenReturn(Optional.of(evaluation));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(evaluationRepository.save(any(Evaluation.class))).thenReturn(updatedEval);

        EvaluationDTO result = evaluationService.update(1L, updateDTO);

        assertThat(result)
                .isNotNull()
                .extracting(EvaluationDTO::getName)
                .isEqualTo("Prueba 1 Actualizada");

        verify(evaluationRepository, times(1)).findById(1L);
        verify(evaluationRepository, times(1)).save(any(Evaluation.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when evaluation not found on update")
    void testUpdateNotFound() {
        EvaluationDTO updateDTO = EvaluationDTO.builder()
                .name("New Name")
                .build();

        when(evaluationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.update(999L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Evaluation not found");

        verify(evaluationRepository, times(1)).findById(999L);
        verify(evaluationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete evaluation successfully")
    void testDelete() {
        doNothing().when(evaluationRepository).deleteById(1L);

        evaluationService.delete(1L);

        verify(evaluationRepository, times(1)).deleteById(1L);
    }
}

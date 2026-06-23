package com.ohiggins.classflow.academic.controller;

import com.ohiggins.classflow.academic.dto.EvaluationDTO;
import com.ohiggins.classflow.academic.service.EvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationController.class)
@DisplayName("EvaluationController Tests")
class EvaluationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EvaluationService evaluationService;

    private EvaluationDTO evaluationDTO;

    @BeforeEach
    void setUp() {
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
    @DisplayName("Should get all evaluations")
    void testGetAll() throws Exception {
        EvaluationDTO eval2 = EvaluationDTO.builder()
                .id(2L)
                .name("Prueba 2")
                .description("Segunda evaluación")
                .maxScore(50.0)
                .percentage(20.0)
                .date(LocalDate.of(2026, 5, 1))
                .subjectId(1L)
                .build();

        when(evaluationService.findAll()).thenReturn(Arrays.asList(evaluationDTO, eval2));

        mockMvc.perform(get("/api/evaluations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo("Prueba 1")))
                .andExpect(jsonPath("$[1].name", equalTo("Prueba 2")));

        verify(evaluationService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get evaluations by subject id")
    void testGetBySubjectId() throws Exception {
        when(evaluationService.findBySubjectId(1L)).thenReturn(Arrays.asList(evaluationDTO));

        mockMvc.perform(get("/api/evaluations/subject/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Prueba 1")));

        verify(evaluationService, times(1)).findBySubjectId(1L);
    }

    @Test
    @DisplayName("Should get evaluation by id")
    void testGetById() throws Exception {
        when(evaluationService.findById(1L)).thenReturn(evaluationDTO);

        mockMvc.perform(get("/api/evaluations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Prueba 1")))
                .andExpect(jsonPath("$.maxScore", equalTo(100.0)));

        verify(evaluationService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return 400 when evaluation not found")
    void testGetByIdNotFound() throws Exception {
        when(evaluationService.findById(999L))
                .thenThrow(new RuntimeException("Evaluation not found"));

        mockMvc.perform(get("/api/evaluations/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(evaluationService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create evaluation successfully")
    void testCreate() throws Exception {
        EvaluationDTO newEvalDTO = EvaluationDTO.builder()
                .name("Prueba 3")
                .description("Tercera evaluación")
                .maxScore(70.0)
                .percentage(25.0)
                .date(LocalDate.of(2026, 6, 1))
                .subjectId(1L)
                .build();

        EvaluationDTO createdDTO = EvaluationDTO.builder()
                .id(3L)
                .name("Prueba 3")
                .description("Tercera evaluación")
                .maxScore(70.0)
                .percentage(25.0)
                .date(LocalDate.of(2026, 6, 1))
                .subjectId(1L)
                .build();

        when(evaluationService.create(any(EvaluationDTO.class))).thenReturn(createdDTO);

        mockMvc.perform(post("/api/evaluations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEvalDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(3)))
                .andExpect(jsonPath("$.name", equalTo("Prueba 3")));

        verify(evaluationService, times(1)).create(any(EvaluationDTO.class));
    }

    @Test
    @DisplayName("Should update evaluation successfully")
    void testUpdate() throws Exception {
        EvaluationDTO updateDTO = EvaluationDTO.builder()
                .name("Prueba 1 Actualizada")
                .description("Descripción actualizada")
                .maxScore(90.0)
                .percentage(35.0)
                .date(LocalDate.of(2026, 4, 20))
                .subjectId(1L)
                .build();

        EvaluationDTO updatedDTO = EvaluationDTO.builder()
                .id(1L)
                .name("Prueba 1 Actualizada")
                .description("Descripción actualizada")
                .maxScore(90.0)
                .percentage(35.0)
                .date(LocalDate.of(2026, 4, 20))
                .subjectId(1L)
                .build();

        when(evaluationService.update(eq(1L), any(EvaluationDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/evaluations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Prueba 1 Actualizada")))
                .andExpect(jsonPath("$.maxScore", equalTo(90.0)));

        verify(evaluationService, times(1)).update(eq(1L), any(EvaluationDTO.class));
    }

    @Test
    @DisplayName("Should delete evaluation successfully")
    void testDelete() throws Exception {
        doNothing().when(evaluationService).delete(1L);

        mockMvc.perform(delete("/api/evaluations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(evaluationService, times(1)).delete(1L);
    }
}

package academic_service.controller;

import academic_service.dto.GradeDTO;
import academic_service.service.GradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GradeController.class)
@DisplayName("GradeController Integration Tests")
class GradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GradeService gradeService;

    private GradeDTO gradeDTO;

    @BeforeEach
    void setUp() {
        gradeDTO = GradeDTO.builder()
                .id(1L)
                .studentId(101L)
                .evaluationId(1L)
                .score(85.5)
                .observations("Good performance")
                .build();
    }

    @Test
    @DisplayName("Should get all grades")
    void testGetAll() throws Exception {
        // Given
        GradeDTO grade2 = GradeDTO.builder()
                .id(2L)
                .studentId(102L)
                .evaluationId(1L)
                .score(92.0)
                .build();

        when(gradeService.findAll()).thenReturn(Arrays.asList(gradeDTO, grade2));

        // When & Then
        mockMvc.perform(get("/api/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].score", equalTo(85.5)))
                .andExpect(jsonPath("$[1].score", equalTo(92.0)));

        verify(gradeService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get grades by student id")
    void testGetByStudentId() throws Exception {
        // Given
        when(gradeService.findByStudentId(101L)).thenReturn(Arrays.asList(gradeDTO));

        // When & Then
        mockMvc.perform(get("/api/grades/student/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].studentId", equalTo(101)));

        verify(gradeService, times(1)).findByStudentId(101L);
    }

    @Test
    @DisplayName("Should get grades by evaluation id")
    void testGetByEvaluationId() throws Exception {
        // Given
        when(gradeService.findByEvaluationId(1L)).thenReturn(Arrays.asList(gradeDTO));

        // When & Then
        mockMvc.perform(get("/api/grades/evaluation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].evaluationId", equalTo(1)));

        verify(gradeService, times(1)).findByEvaluationId(1L);
    }

    @Test
    @DisplayName("Should get grade by id")
    void testGetById() throws Exception {
        // Given
        when(gradeService.findById(1L)).thenReturn(gradeDTO);

        // When & Then
        mockMvc.perform(get("/api/grades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.studentId", equalTo(101)))
                .andExpect(jsonPath("$.score", equalTo(85.5)));

        verify(gradeService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should create grade successfully")
    void testCreate() throws Exception {
        // Given
        GradeDTO newGradeDTO = GradeDTO.builder()
                .studentId(103L)
                .evaluationId(1L)
                .score(90.0)
                .observations("Excellent")
                .build();

        GradeDTO createdDTO = GradeDTO.builder()
                .id(3L)
                .studentId(103L)
                .evaluationId(1L)
                .score(90.0)
                .observations("Excellent")
                .build();

        when(gradeService.create(any())).thenReturn(createdDTO);

        // When & Then
        mockMvc.perform(post("/api/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newGradeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(3)))
                .andExpect(jsonPath("$.score", equalTo(90.0)))
                .andExpect(jsonPath("$.observations", equalTo("Excellent")));

        verify(gradeService, times(1)).create(any());
    }

    @Test
    @DisplayName("Should update grade successfully")
    void testUpdate() throws Exception {
        // Given
        GradeDTO updateDTO = GradeDTO.builder()
                .studentId(101L)
                .evaluationId(1L)
                .score(95.0)
                .observations("Improved")
                .build();

        GradeDTO updatedDTO = GradeDTO.builder()
                .id(1L)
                .studentId(101L)
                .evaluationId(1L)
                .score(95.0)
                .observations("Improved")
                .build();

        when(gradeService.update(eq(1L), any())).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/grades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score", equalTo(95.0)))
                .andExpect(jsonPath("$.observations", equalTo("Improved")));

        verify(gradeService, times(1)).update(eq(1L), any());
    }

    @Test
    @DisplayName("Should delete grade successfully")
    void testDelete() throws Exception {
        // Given
        doNothing().when(gradeService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/grades/1"))
                .andExpect(status().isNoContent());

        verify(gradeService, times(1)).delete(1L);
    }
}

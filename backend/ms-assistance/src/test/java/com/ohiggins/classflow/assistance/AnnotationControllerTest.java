package com.ohiggins.classflow.assistance.controller;

import com.ohiggins.classflow.assistance.dto.AnnotationDTO;
import com.ohiggins.classflow.assistance.dto.AnnotationRequestDTO;
import com.ohiggins.classflow.assistance.service.AnnotationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnnotationController.class)
@DisplayName("AnnotationController Tests")
class AnnotationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AnnotationService annotationService;

    private AnnotationDTO annotationDTO;

    @BeforeEach
    void setUp() {
        annotationDTO = AnnotationDTO.builder()
                .id(1L)
                .studentId(101L)
                .teacherId(501L)
                .type("POSITIVE")
                .description("Great improvement")
                .date(LocalDateTime.of(2026, 5, 15, 10, 30))
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should get all annotations")
    void testGetAll() throws Exception {
        AnnotationDTO annotation2 = AnnotationDTO.builder()
                .id(2L)
                .studentId(102L)
                .teacherId(502L)
                .type("NEGATIVE")
                .description("Needs improvement")
                .date(LocalDateTime.of(2026, 5, 16, 11, 0))
                .active(true)
                .build();

        when(annotationService.findAll()).thenReturn(List.of(annotationDTO, annotation2));

        mockMvc.perform(get("/api/annotations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentId", equalTo(101)))
                .andExpect(jsonPath("$[1].studentId", equalTo(102)));

        verify(annotationService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get annotations by student id")
    void testGetByStudent() throws Exception {
        when(annotationService.findByStudentId(101L)).thenReturn(List.of(annotationDTO));

        mockMvc.perform(get("/api/annotations/student/101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type", equalTo("POSITIVE")));

        verify(annotationService, times(1)).findByStudentId(101L);
    }

    @Test
    @DisplayName("Should get annotations by student id and type")
    void testGetByStudentAndType() throws Exception {
        when(annotationService.findByStudentIdAndType(101L, "POSITIVE")).thenReturn(List.of(annotationDTO));

        mockMvc.perform(get("/api/annotations/student/101/type/POSITIVE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", equalTo("Great improvement")));

        verify(annotationService, times(1)).findByStudentIdAndType(101L, "POSITIVE");
    }

    @Test
    @DisplayName("Should create annotation successfully")
    void testCreate() throws Exception {
        AnnotationRequestDTO requestDTO = new AnnotationRequestDTO();
        requestDTO.setStudentId(101L);
        requestDTO.setTeacherId(501L);
        requestDTO.setType("POSITIVE");
        requestDTO.setDescription("Great improvement");

        when(annotationService.create(any(AnnotationRequestDTO.class))).thenReturn(annotationDTO);

        mockMvc.perform(post("/api/annotations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.active", equalTo(true)));

        verify(annotationService, times(1)).create(any(AnnotationRequestDTO.class));
    }

    @Test
    @DisplayName("Should delete annotation successfully")
    void testDelete() throws Exception {
        doNothing().when(annotationService).delete(1L);

        mockMvc.perform(delete("/api/annotations/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(annotationService, times(1)).delete(1L);
    }
}
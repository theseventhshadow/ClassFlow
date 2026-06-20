package com.ohiggins.classflow.academic.controller;

import com.ohiggins.classflow.academic.dto.SubjectDTO;
import com.ohiggins.classflow.academic.service.SubjectService;
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

@WebMvcTest(SubjectController.class)
@DisplayName("SubjectController Tests")
class SubjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubjectService subjectService;

    private SubjectDTO subjectDTO;

    @BeforeEach
    void setUp() {
        subjectDTO = SubjectDTO.builder()
                .id(1L)
                .name("Matemáticas")
                .description("Asignatura de Matemáticas")
                .courseId(1L)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should get all subjects")
    void testGetAll() throws Exception {
        SubjectDTO subject2 = SubjectDTO.builder()
                .id(2L)
                .name("Física")
                .description("Asignatura de Física")
                .courseId(1L)
                .active(true)
                .build();

        when(subjectService.findAll()).thenReturn(Arrays.asList(subjectDTO, subject2));

        mockMvc.perform(get("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo("Matemáticas")))
                .andExpect(jsonPath("$[1].name", equalTo("Física")));

        verify(subjectService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get subjects by course id")
    void testGetByCourseId() throws Exception {
        when(subjectService.findByCourseId(1L)).thenReturn(Arrays.asList(subjectDTO));

        mockMvc.perform(get("/api/subjects/course/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", equalTo("Matemáticas")));

        verify(subjectService, times(1)).findByCourseId(1L);
    }

    @Test
    @DisplayName("Should get subject by id")
    void testGetById() throws Exception {
        when(subjectService.findById(1L)).thenReturn(subjectDTO);

        mockMvc.perform(get("/api/subjects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Matemáticas")));

        verify(subjectService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return 400 when subject not found")
    void testGetByIdNotFound() throws Exception {
        when(subjectService.findById(999L))
                .thenThrow(new RuntimeException("Subject not found"));

        mockMvc.perform(get("/api/subjects/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(subjectService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create subject successfully")
    void testCreate() throws Exception {
        SubjectDTO newSubjectDTO = SubjectDTO.builder()
                .name("Historia")
                .description("Asignatura de Historia")
                .courseId(1L)
                .build();

        SubjectDTO createdDTO = SubjectDTO.builder()
                .id(3L)
                .name("Historia")
                .description("Asignatura de Historia")
                .courseId(1L)
                .active(true)
                .build();

        when(subjectService.create(any(SubjectDTO.class))).thenReturn(createdDTO);

        mockMvc.perform(post("/api/subjects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newSubjectDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(3)))
                .andExpect(jsonPath("$.name", equalTo("Historia")))
                .andExpect(jsonPath("$.active", equalTo(true)));

        verify(subjectService, times(1)).create(any(SubjectDTO.class));
    }

    @Test
    @DisplayName("Should update subject successfully")
    void testUpdate() throws Exception {
        SubjectDTO updateDTO = SubjectDTO.builder()
                .name("Matemáticas Avanzadas")
                .description("Nivel avanzado")
                .courseId(1L)
                .build();

        SubjectDTO updatedDTO = SubjectDTO.builder()
                .id(1L)
                .name("Matemáticas Avanzadas")
                .description("Nivel avanzado")
                .courseId(1L)
                .active(true)
                .build();

        when(subjectService.update(eq(1L), any(SubjectDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/subjects/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Matemáticas Avanzadas")));

        verify(subjectService, times(1)).update(eq(1L), any(SubjectDTO.class));
    }

    @Test
    @DisplayName("Should delete subject successfully")
    void testDelete() throws Exception {
        doNothing().when(subjectService).delete(1L);

        mockMvc.perform(delete("/api/subjects/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(subjectService, times(1)).delete(1L);
    }
}

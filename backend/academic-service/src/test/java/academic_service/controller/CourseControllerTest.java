package academic_service.controller;

import academic_service.dto.CourseDTO;
import academic_service.service.CourseService;
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

@WebMvcTest(CourseController.class)
@DisplayName("CourseController Tests")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    private CourseDTO courseDTO;

    @BeforeEach
    void setUp() {
        courseDTO = CourseDTO.builder()
                .id(1L)
                .name("Matemáticas")
                .description("Curso de Matemáticas")
                .academicYear(2024)
                .active(true)
                .build();
    }

    @Test
    @DisplayName("Should get all courses")
    void testGetAll() throws Exception {
        // Given
        CourseDTO course2 = CourseDTO.builder()
                .id(2L)
                .name("Física")
                .description("Curso de Física")
                .academicYear(2024)
                .active(true)
                .build();

        when(courseService.findAll()).thenReturn(Arrays.asList(courseDTO, course2));

        // When & Then
        mockMvc.perform(get("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo("Matemáticas")))
                .andExpect(jsonPath("$[1].name", equalTo("Física")));

        verify(courseService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get course by id")
    void testGetById() throws Exception {
        // Given
        when(courseService.findById(1L)).thenReturn(courseDTO);

        // When & Then
        mockMvc.perform(get("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.name", equalTo("Matemáticas")))
                .andExpect(jsonPath("$.description", equalTo("Curso de Matemáticas")));

        verify(courseService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return 400 when course not found")
    void testGetByIdNotFound() throws Exception {
        // Given
        when(courseService.findById(999L))
                .thenThrow(new RuntimeException("Course not found"));

        // When & Then
        mockMvc.perform(get("/api/courses/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(courseService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create course successfully")
    void testCreate() throws Exception {
        // Given
        CourseDTO newCourseDTO = CourseDTO.builder()
                .name("Historia")
                .description("Curso de Historia")
                .academicYear(2024)
                .build();

        CourseDTO createdDTO = CourseDTO.builder()
                .id(3L)
                .name("Historia")
                .description("Curso de Historia")
                .academicYear(2024)
                .active(true)
                .build();

        when(courseService.create(any(CourseDTO.class))).thenReturn(createdDTO);

        // When & Then
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCourseDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(3)))
                .andExpect(jsonPath("$.name", equalTo("Historia")))
                .andExpect(jsonPath("$.active", equalTo(true)));

        verify(courseService, times(1)).create(any(CourseDTO.class));
    }

    @Test
    @DisplayName("Should update course successfully")
    void testUpdate() throws Exception {
        // Given
        CourseDTO updateDTO = CourseDTO.builder()
                .name("Matemáticas Avanzadas")
                .description("Curso avanzado")
                .academicYear(2025)
                .build();

        CourseDTO updatedDTO = CourseDTO.builder()
                .id(1L)
                .name("Matemáticas Avanzadas")
                .description("Curso avanzado")
                .academicYear(2025)
                .active(true)
                .build();

        when(courseService.update(eq(1L), any(CourseDTO.class))).thenReturn(updatedDTO);

        // When & Then
        mockMvc.perform(put("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Matemáticas Avanzadas")))
                .andExpect(jsonPath("$.academicYear", equalTo(2025)));

        verify(courseService, times(1)).update(eq(1L), any(CourseDTO.class));
    }

    @Test
    @DisplayName("Should delete course successfully")
    void testDelete() throws Exception {
        // Given
        doNothing().when(courseService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/courses/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(courseService, times(1)).delete(1L);
    }
}

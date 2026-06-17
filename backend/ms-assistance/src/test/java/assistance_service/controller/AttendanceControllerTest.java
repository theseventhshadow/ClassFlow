package assistance_service.controller;

import assistance_service.dto.AttendanceDTO;
import assistance_service.dto.AttendanceRequestDTO;
import assistance_service.service.AttendanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceController.class)
@DisplayName("AttendanceController Tests")
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AttendanceService attendanceService;

    private AttendanceDTO attendanceDTO;

    @BeforeEach
    void setUp() {
        attendanceDTO = AttendanceDTO.builder()
                .id(1L)
                .studentId(101L)
                .courseId(201L)
                .date(LocalDate.of(2026, 5, 15))
                .present(true)
                .justification("Present")
                .build();
    }

    @Test
    @DisplayName("Should get all attendances")
    void testGetAll() throws Exception {
        AttendanceDTO attendance2 = AttendanceDTO.builder()
                .id(2L)
                .studentId(102L)
                .courseId(201L)
                .date(LocalDate.of(2026, 5, 16))
                .present(false)
                .build();

        when(attendanceService.findAll()).thenReturn(List.of(attendanceDTO, attendance2));

        mockMvc.perform(get("/api/attendance")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentId", equalTo(101)))
                .andExpect(jsonPath("$[1].studentId", equalTo(102)));

        verify(attendanceService, times(1)).findAll();
    }

    @Test
    @DisplayName("Should get attendances by student id")
    void testGetByStudent() throws Exception {
        when(attendanceService.findByStudentId(101L)).thenReturn(List.of(attendanceDTO));

        mockMvc.perform(get("/api/attendance/student/101")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].courseId", equalTo(201)));

        verify(attendanceService, times(1)).findByStudentId(101L);
    }

    @Test
    @DisplayName("Should get attendances by course and date")
    void testGetByCourseAndDate() throws Exception {
        when(attendanceService.findByCourseAndDate(201L, LocalDate.of(2026, 5, 15)))
                .thenReturn(List.of(attendanceDTO));

        mockMvc.perform(get("/api/attendance/course/201/date/2026-05-15")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId", equalTo(101)));

        verify(attendanceService, times(1)).findByCourseAndDate(201L, LocalDate.of(2026, 5, 15));
    }

    @Test
    @DisplayName("Should register attendance successfully")
    void testRegister() throws Exception {
        AttendanceRequestDTO requestDTO = new AttendanceRequestDTO();
        requestDTO.setStudentId(101L);
        requestDTO.setCourseId(201L);
        requestDTO.setDate(LocalDate.of(2026, 5, 15));
        requestDTO.setPresent(true);
        requestDTO.setJustification("Present");

        when(attendanceService.register(any(AttendanceRequestDTO.class))).thenReturn(attendanceDTO);

        mockMvc.perform(post("/api/attendance/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.present", equalTo(true)));

        verify(attendanceService, times(1)).register(any(AttendanceRequestDTO.class));
    }

    @Test
    @DisplayName("Should update attendance successfully")
    void testUpdate() throws Exception {
        AttendanceRequestDTO requestDTO = new AttendanceRequestDTO();
        requestDTO.setStudentId(101L);
        requestDTO.setCourseId(201L);
        requestDTO.setDate(LocalDate.of(2026, 5, 15));
        requestDTO.setPresent(false);
        requestDTO.setJustification("Late justification");

        AttendanceDTO updatedDTO = AttendanceDTO.builder()
                .id(1L)
                .studentId(101L)
                .courseId(201L)
                .date(LocalDate.of(2026, 5, 15))
                .present(false)
                .justification("Late justification")
                .build();

        when(attendanceService.update(eq(1L), any(AttendanceRequestDTO.class))).thenReturn(updatedDTO);

        mockMvc.perform(put("/api/attendance/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.present", equalTo(false)))
                .andExpect(jsonPath("$.justification", equalTo("Late justification")));

        verify(attendanceService, times(1)).update(eq(1L), any(AttendanceRequestDTO.class));
    }
}
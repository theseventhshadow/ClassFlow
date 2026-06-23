package com.ohiggins.classflow.assistance.service;

import com.ohiggins.classflow.assistance.dto.AttendanceDTO;
import com.ohiggins.classflow.assistance.dto.AttendanceRequestDTO;
import com.ohiggins.classflow.assistance.entity.Attendance;
import com.ohiggins.classflow.assistance.repository.AttendanceRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("AttendanceService Tests")
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    private Attendance attendance;
    private AttendanceDTO attendanceDTO;
    private AttendanceRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        attendance = new Attendance();
        attendance.setId(1L);
        attendance.setStudentId(101L);
        attendance.setCourseId(201L);
        attendance.setDate(LocalDate.of(2026, 5, 15));
        attendance.setPresent(true);
        attendance.setJustification("Present");

        attendanceDTO = AttendanceDTO.builder()
                .id(1L)
                .studentId(101L)
                .courseId(201L)
                .date(LocalDate.of(2026, 5, 15))
                .present(true)
                .justification("Present")
                .build();

        requestDTO = new AttendanceRequestDTO();
        requestDTO.setStudentId(101L);
        requestDTO.setCourseId(201L);
        requestDTO.setDate(LocalDate.of(2026, 5, 15));
        requestDTO.setPresent(true);
        requestDTO.setJustification("Present");
    }

    @Test
    @DisplayName("Should find all attendances")
    void testFindAll() {
        Attendance attendance2 = new Attendance();
        attendance2.setId(2L);
        attendance2.setStudentId(102L);
        attendance2.setCourseId(201L);
        attendance2.setDate(LocalDate.of(2026, 5, 16));
        attendance2.setPresent(false);

        when(attendanceRepository.findAll()).thenReturn(Arrays.asList(attendance, attendance2));

        List<AttendanceDTO> result = attendanceService.findAll();

        assertThat(result)
                .hasSize(2)
                .extracting(AttendanceDTO::getStudentId)
                .containsExactly(101L, 102L);

        verify(attendanceRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should find attendances by student id")
    void testFindByStudentId() {
        when(attendanceRepository.findByStudentId(101L)).thenReturn(List.of(attendance));

        List<AttendanceDTO> result = attendanceService.findByStudentId(101L);

        assertThat(result)
                .hasSize(1)
                .extracting(AttendanceDTO::getCourseId)
                .containsExactly(201L);

        verify(attendanceRepository, times(1)).findByStudentId(101L);
    }

    @Test
    @DisplayName("Should find attendances by course and date")
    void testFindByCourseAndDate() {
        when(attendanceRepository.findByCourseIdAndDate(201L, LocalDate.of(2026, 5, 15)))
                .thenReturn(List.of(attendance));

        List<AttendanceDTO> result = attendanceService.findByCourseAndDate(201L, LocalDate.of(2026, 5, 15));

        assertThat(result)
                .hasSize(1)
                .extracting(AttendanceDTO::getStudentId)
                .containsExactly(101L);

        verify(attendanceRepository, times(1)).findByCourseIdAndDate(201L, LocalDate.of(2026, 5, 15));
    }

    @Test
    @DisplayName("Should register attendance successfully")
    void testRegister() {
        Attendance otherDateAttendance = new Attendance();
        otherDateAttendance.setStudentId(101L);
        otherDateAttendance.setDate(LocalDate.of(2026, 5, 14));

        when(attendanceRepository.findByStudentId(101L)).thenReturn(List.of(otherDateAttendance));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AttendanceDTO result = attendanceService.register(requestDTO);

        assertThat(result)
                .isNotNull()
                .extracting(AttendanceDTO::getStudentId, AttendanceDTO::getPresent)
                .containsExactly(101L, true);

        verify(attendanceRepository, times(1)).findByStudentId(101L);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when attendance already exists for date")
    void testRegisterDuplicate() {
        when(attendanceRepository.findByStudentId(101L)).thenReturn(List.of(attendance));

        assertThatThrownBy(() -> attendanceService.register(requestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("An attendance record already exists for this student on date 2026-05-15");

        verify(attendanceRepository, times(1)).findByStudentId(101L);
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should update attendance successfully")
    void testUpdate() {
        AttendanceRequestDTO updateDTO = new AttendanceRequestDTO();
        updateDTO.setStudentId(101L);
        updateDTO.setCourseId(201L);
        updateDTO.setDate(LocalDate.of(2026, 5, 15));
        updateDTO.setPresent(false);
        updateDTO.setJustification("Late justification");

        Attendance updatedAttendance = new Attendance();
        updatedAttendance.setId(1L);
        updatedAttendance.setStudentId(101L);
        updatedAttendance.setCourseId(201L);
        updatedAttendance.setDate(LocalDate.of(2026, 5, 15));
        updatedAttendance.setPresent(false);
        updatedAttendance.setJustification("Late justification");

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(updatedAttendance);

        AttendanceDTO result = attendanceService.update(1L, updateDTO);

        assertThat(result)
                .isNotNull()
                .extracting(AttendanceDTO::getPresent, AttendanceDTO::getJustification)
                .containsExactly(false, "Late justification");

        verify(attendanceRepository, times(1)).findById(1L);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when attendance not found")
    void testUpdateNotFound() {
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> attendanceService.update(999L, requestDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Attendance record not found");

        verify(attendanceRepository, times(1)).findById(999L);
        verify(attendanceRepository, never()).save(any(Attendance.class));
    }
}
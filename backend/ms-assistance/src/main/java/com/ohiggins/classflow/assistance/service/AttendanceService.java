package com.ohiggins.classflow.assistance.service;

import com.ohiggins.classflow.assistance.dto.AttendanceDTO;
import com.ohiggins.classflow.assistance.dto.AttendanceRequestDTO;
import com.ohiggins.classflow.assistance.entity.Attendance;
import com.ohiggins.classflow.assistance.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene la logica de negocio para la gestion de asistencia diaria.
 */
@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    
    /**
     * Obtiene todos los registros de asistencia.
     *
     * @return lista de asistencias convertidas a DTO.
     */
    public List<AttendanceDTO> findAll() {
        return attendanceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los registros de asistencia de un estudiante.
     *
     * @param studentId identificador del estudiante.
     * @return lista de asistencias convertidas a DTO.
     */
    public List<AttendanceDTO> findByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los registros de asistencia de un curso en una fecha concreta.
     *
     * @param courseId identificador del curso.
     * @param date fecha de consulta.
     * @return lista de asistencias convertidas a DTO.
     */
    public List<AttendanceDTO> findByCourseAndDate(Long courseId, LocalDate date) {
        return attendanceRepository.findByCourseIdAndDate(courseId, date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Registra una nueva asistencia evitando duplicados por estudiante y fecha.
     *
     * @param request datos de la asistencia a registrar.
     * @return asistencia guardada convertida a DTO.
     */
    public AttendanceDTO register(AttendanceRequestDTO request) {
        // Check if a record already exists for this student on this date
        List<Attendance> existing = attendanceRepository.findByStudentId(request.getStudentId())
                .stream()
                .filter(a -> a.getDate().equals(request.getDate()))
                .collect(Collectors.toList());
        
        if (!existing.isEmpty()) {
            throw new RuntimeException("An attendance record already exists for this student on date " + request.getDate());
        }
        
        Attendance attendance = new Attendance();
        attendance.setStudentId(request.getStudentId());
        attendance.setCourseId(request.getCourseId());
        attendance.setDate(request.getDate());
        attendance.setPresent(request.getPresent());
        attendance.setJustification(request.getJustification());
        
        Attendance saved = attendanceRepository.save(attendance);
        return convertToDTO(saved);
    }
    
    /**
     * Actualiza un registro de asistencia existente.
     *
     * @param id identificador del registro.
     * @param request datos actualizados de la asistencia.
     * @return asistencia actualizada convertida a DTO.
     */
    public AttendanceDTO update(Long id, AttendanceRequestDTO request) {
        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Attendance record not found"));
        
        attendance.setPresent(request.getPresent());
        attendance.setJustification(request.getJustification());
        
        Attendance updated = attendanceRepository.save(attendance);
        return convertToDTO(updated);
    }
    
    private AttendanceDTO convertToDTO(Attendance attendance) {
        return AttendanceDTO.builder()
                .id(attendance.getId())
                .studentId(attendance.getStudentId())
                .courseId(attendance.getCourseId())
                .date(attendance.getDate())
                .present(attendance.getPresent())
                .justification(attendance.getJustification())
                .build();
    }
}

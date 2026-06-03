package assistance_service.service;

import assistance_service.dto.AttendanceDTO;
import assistance_service.dto.AttendanceRequestDTO;
import assistance_service.entity.Attendance;
import assistance_service.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    
    private final AttendanceRepository attendanceRepository;
    
    public List<AttendanceDTO> findAll() {
        return attendanceRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AttendanceDTO> findByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AttendanceDTO> findByCourseAndDate(Long courseId, LocalDate date) {
        return attendanceRepository.findByCourseIdAndDate(courseId, date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
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

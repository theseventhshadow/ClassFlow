package assistance_service.controller;

import assistance_service.dto.AttendanceDTO;
import assistance_service.dto.AttendanceRequestDTO;
import assistance_service.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    
    private final AttendanceService attendanceService;
    
    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAll() {
        return ResponseEntity.ok(attendanceService.findAll());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AttendanceDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.findByStudentId(studentId));
    }
    
    @GetMapping("/course/{courseId}/date/{date}")
    public ResponseEntity<List<AttendanceDTO>> getByCourseAndDate(
            @PathVariable Long courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.findByCourseAndDate(courseId, date));
    }
    
    @PostMapping("/register")
    public ResponseEntity<AttendanceDTO> register(@Valid @RequestBody AttendanceRequestDTO request) {
        return new ResponseEntity<>(attendanceService.register(request), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> update(@PathVariable Long id, @Valid @RequestBody AttendanceRequestDTO request) {
        return ResponseEntity.ok(attendanceService.update(id, request));
    }
}

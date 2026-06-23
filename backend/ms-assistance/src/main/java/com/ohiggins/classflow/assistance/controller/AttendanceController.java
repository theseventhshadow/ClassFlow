package com.ohiggins.classflow.assistance.controller;

import com.ohiggins.classflow.assistance.dto.AttendanceDTO;
import com.ohiggins.classflow.assistance.dto.AttendanceRequestDTO;
import com.ohiggins.classflow.assistance.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Asistencia", description = "Registro y consulta de asistencia diaria de estudiantes")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @GetMapping
    @Operation(summary = "Listar todos los registros de asistencia")
    @ApiResponse(responseCode = "200", description = "Lista de asistencias")
    public ResponseEntity<List<AttendanceDTO>> getAll() {
        return ResponseEntity.ok(attendanceService.findAll());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Asistencia por estudiante")
    @ApiResponse(responseCode = "200", description = "Registros del estudiante")
    public ResponseEntity<List<AttendanceDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.findByStudentId(studentId));
    }

    @GetMapping("/course/{courseId}/date/{date}")
    @Operation(summary = "Asistencia por curso y fecha")
    @ApiResponse(responseCode = "200", description = "Registros del curso en la fecha indicada")
    public ResponseEntity<List<AttendanceDTO>> getByCourseAndDate(
            @PathVariable Long courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.findByCourseAndDate(courseId, date));
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar asistencia")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asistencia registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AttendanceDTO> register(@Valid @RequestBody AttendanceRequestDTO request) {
        return new ResponseEntity<>(attendanceService.register(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro de asistencia")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asistencia actualizada"),
        @ApiResponse(responseCode = "404", description = "Registro no encontrado")
    })
    public ResponseEntity<AttendanceDTO> update(@PathVariable Long id, @Valid @RequestBody AttendanceRequestDTO request) {
        return ResponseEntity.ok(attendanceService.update(id, request));
    }
}

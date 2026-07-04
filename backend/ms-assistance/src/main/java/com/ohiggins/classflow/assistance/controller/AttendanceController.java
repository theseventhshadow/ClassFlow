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

/**
 * Expone los endpoints REST para gestionar asistencia diaria.
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Asistencia", description = "Registro y consulta de asistencia diaria de estudiantes")
public class AttendanceController {

    private final AttendanceService attendanceService;

    /**
     * Obtiene el listado completo de registros de asistencia.
     *
     * @return respuesta HTTP con la lista de asistencias.
     */
    @GetMapping
    @Operation(summary = "Listar todos los registros de asistencia")
    @ApiResponse(responseCode = "200", description = "Lista de asistencias")
    public ResponseEntity<List<AttendanceDTO>> getAll() {
        return ResponseEntity.ok(attendanceService.findAll());
    }

    /**
     * Obtiene la asistencia de un estudiante.
     *
     * @param studentId identificador del estudiante.
     * @return respuesta HTTP con los registros del estudiante.
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Asistencia por estudiante")
    @ApiResponse(responseCode = "200", description = "Registros del estudiante")
    public ResponseEntity<List<AttendanceDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceService.findByStudentId(studentId));
    }

    /**
     * Obtiene la asistencia de un curso en una fecha concreta.
     *
     * @param courseId identificador del curso.
     * @param date fecha de consulta.
     * @return respuesta HTTP con los registros filtrados.
     */
    @GetMapping("/course/{courseId}/date/{date}")
    @Operation(summary = "Asistencia por curso y fecha")
    @ApiResponse(responseCode = "200", description = "Registros del curso en la fecha indicada")
    public ResponseEntity<List<AttendanceDTO>> getByCourseAndDate(
            @PathVariable Long courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(attendanceService.findByCourseAndDate(courseId, date));
    }

    /**
     * Registra un nuevo evento de asistencia.
     *
     * @param request datos de la asistencia a registrar.
     * @return respuesta HTTP con la asistencia creada.
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar asistencia")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asistencia registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AttendanceDTO> register(@Valid @RequestBody AttendanceRequestDTO request) {
        return new ResponseEntity<>(attendanceService.register(request), HttpStatus.CREATED);
    }

    /**
     * Actualiza un registro de asistencia.
     *
     * @param id identificador del registro.
     * @param request datos actualizados de la asistencia.
     * @return respuesta HTTP con la asistencia actualizada.
     */
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

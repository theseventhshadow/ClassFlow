package com.ohiggins.classflow.academic.controller;

import com.ohiggins.classflow.academic.dto.GradeDTO;
import com.ohiggins.classflow.academic.service.GradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gestión de notas de estudiantes por evaluación")
public class GradeController {

    private final GradeService gradeService;

    @GetMapping
    @Operation(summary = "Listar todas las notas")
    @ApiResponse(responseCode = "200", description = "Lista de notas")
    public ResponseEntity<List<GradeDTO>> getAll() {
        return ResponseEntity.ok(gradeService.findAll());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Notas por estudiante")
    @ApiResponse(responseCode = "200", description = "Notas del estudiante")
    public ResponseEntity<List<GradeDTO>> getByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.findByStudentId(studentId));
    }

    @GetMapping("/evaluation/{evaluationId}")
    @Operation(summary = "Notas por evaluación")
    @ApiResponse(responseCode = "200", description = "Notas de la evaluación")
    public ResponseEntity<List<GradeDTO>> getByEvaluationId(@PathVariable Long evaluationId) {
        return ResponseEntity.ok(gradeService.findByEvaluationId(evaluationId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    public ResponseEntity<GradeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Registrar nota")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Nota registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<GradeDTO> create(@Valid @RequestBody GradeDTO dto) {
        return new ResponseEntity<>(gradeService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota actualizada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    public ResponseEntity<GradeDTO> update(@PathVariable Long id, @Valid @RequestBody GradeDTO dto) {
        return ResponseEntity.ok(gradeService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar nota")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Nota eliminada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

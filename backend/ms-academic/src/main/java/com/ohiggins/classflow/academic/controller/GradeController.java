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

/**
 * Expone los endpoints REST para gestionar calificaciones.
 */
@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
@Tag(name = "Notas", description = "Gestión de notas de estudiantes por evaluación")
public class GradeController {

    private final GradeService gradeService;

    /**
     * Obtiene el listado completo de calificaciones.
     *
     * @return respuesta HTTP con la lista de calificaciones.
     */
    @GetMapping
    @Operation(summary = "Listar todas las notas")
    @ApiResponse(responseCode = "200", description = "Lista de notas")
    public ResponseEntity<List<GradeDTO>> getAll() {
        return ResponseEntity.ok(gradeService.findAll());
    }

    /**
     * Obtiene las calificaciones de un estudiante.
     *
     * @param studentId identificador del estudiante.
     * @return respuesta HTTP con las calificaciones del estudiante.
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Notas por estudiante")
    @ApiResponse(responseCode = "200", description = "Notas del estudiante")
    public ResponseEntity<List<GradeDTO>> getByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.findByStudentId(studentId));
    }

    /**
     * Obtiene las calificaciones de una evaluacion.
     *
     * @param evaluationId identificador de la evaluacion.
     * @return respuesta HTTP con las calificaciones de la evaluacion.
     */
    @GetMapping("/evaluation/{evaluationId}")
    @Operation(summary = "Notas por evaluación")
    @ApiResponse(responseCode = "200", description = "Notas de la evaluación")
    public ResponseEntity<List<GradeDTO>> getByEvaluationId(@PathVariable Long evaluationId) {
        return ResponseEntity.ok(gradeService.findByEvaluationId(evaluationId));
    }

    /**
     * Obtiene una calificacion por su identificador.
     *
     * @param id identificador de la calificacion.
     * @return respuesta HTTP con la calificacion encontrada.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener nota por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota encontrada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    public ResponseEntity<GradeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.findById(id));
    }

    /**
     * Registra una nueva calificacion.
     *
     * @param dto datos de la calificacion a crear.
     * @return respuesta HTTP con la calificacion registrada.
     */
    @PostMapping
    @Operation(summary = "Registrar nota")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Nota registrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<GradeDTO> create(@Valid @RequestBody GradeDTO dto) {
        return new ResponseEntity<>(gradeService.create(dto), HttpStatus.CREATED);
    }

    /**
     * Actualiza una calificacion existente.
     *
     * @param id identificador de la calificacion.
     * @param dto datos actualizados de la calificacion.
     * @return respuesta HTTP con la calificacion actualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar nota")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Nota actualizada"),
        @ApiResponse(responseCode = "404", description = "Nota no encontrada")
    })
    public ResponseEntity<GradeDTO> update(@PathVariable Long id, @Valid @RequestBody GradeDTO dto) {
        return ResponseEntity.ok(gradeService.update(id, dto));
    }

    /**
     * Elimina una calificacion por su identificador.
     *
     * @param id identificador de la calificacion.
     * @return respuesta HTTP sin contenido.
     */
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

package com.ohiggins.classflow.academic.controller;

import com.ohiggins.classflow.academic.dto.EvaluationDTO;
import com.ohiggins.classflow.academic.service.EvaluationService;
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
 * Expone los endpoints REST para gestionar evaluaciones.
 */
@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@Tag(name = "Evaluaciones", description = "Gestión de evaluaciones por asignatura")
public class EvaluationController {

    private final EvaluationService evaluationService;

    /**
     * Obtiene el listado completo de evaluaciones.
     *
     * @return respuesta HTTP con la lista de evaluaciones.
     */
    @GetMapping
    @Operation(summary = "Listar todas las evaluaciones")
    @ApiResponse(responseCode = "200", description = "Lista de evaluaciones")
    public ResponseEntity<List<EvaluationDTO>> getAll() {
        return ResponseEntity.ok(evaluationService.findAll());
    }

    /**
     * Obtiene las evaluaciones de una asignatura.
     *
     * @param subjectId identificador de la asignatura.
     * @return respuesta HTTP con las evaluaciones de la asignatura.
     */
    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Listar evaluaciones por asignatura")
    @ApiResponse(responseCode = "200", description = "Evaluaciones de la asignatura")
    public ResponseEntity<List<EvaluationDTO>> getBySubjectId(@PathVariable Long subjectId) {
        return ResponseEntity.ok(evaluationService.findBySubjectId(subjectId));
    }

    /**
     * Obtiene una evaluacion por su identificador.
     *
     * @param id identificador de la evaluacion.
     * @return respuesta HTTP con la evaluacion encontrada.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener evaluación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evaluación encontrada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<EvaluationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.findById(id));
    }

    /**
     * Crea una nueva evaluacion.
     *
     * @param dto datos de la evaluacion a crear.
     * @return respuesta HTTP con la evaluacion creada.
     */
    @PostMapping
    @Operation(summary = "Crear evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Evaluación creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<EvaluationDTO> create(@Valid @RequestBody EvaluationDTO dto) {
        return new ResponseEntity<>(evaluationService.create(dto), HttpStatus.CREATED);
    }

    /**
     * Actualiza una evaluacion existente.
     *
     * @param id identificador de la evaluacion.
     * @param dto datos actualizados de la evaluacion.
     * @return respuesta HTTP con la evaluacion actualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evaluación actualizada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<EvaluationDTO> update(@PathVariable Long id, @Valid @RequestBody EvaluationDTO dto) {
        return ResponseEntity.ok(evaluationService.update(id, dto));
    }

    /**
     * Elimina una evaluacion por su identificador.
     *
     * @param id identificador de la evaluacion.
     * @return respuesta HTTP sin contenido.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Evaluación eliminada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        evaluationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

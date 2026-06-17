package academic_service.controller;

import academic_service.dto.EvaluationDTO;
import academic_service.service.EvaluationService;
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
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
@Tag(name = "Evaluaciones", description = "Gestión de evaluaciones por asignatura")
public class EvaluationController {

    private final EvaluationService evaluationService;

    @GetMapping
    @Operation(summary = "Listar todas las evaluaciones")
    @ApiResponse(responseCode = "200", description = "Lista de evaluaciones")
    public ResponseEntity<List<EvaluationDTO>> getAll() {
        return ResponseEntity.ok(evaluationService.findAll());
    }

    @GetMapping("/subject/{subjectId}")
    @Operation(summary = "Listar evaluaciones por asignatura")
    @ApiResponse(responseCode = "200", description = "Evaluaciones de la asignatura")
    public ResponseEntity<List<EvaluationDTO>> getBySubjectId(@PathVariable Long subjectId) {
        return ResponseEntity.ok(evaluationService.findBySubjectId(subjectId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener evaluación por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evaluación encontrada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<EvaluationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Evaluación creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<EvaluationDTO> create(@Valid @RequestBody EvaluationDTO dto) {
        return new ResponseEntity<>(evaluationService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar evaluación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Evaluación actualizada"),
        @ApiResponse(responseCode = "404", description = "Evaluación no encontrada")
    })
    public ResponseEntity<EvaluationDTO> update(@PathVariable Long id, @Valid @RequestBody EvaluationDTO dto) {
        return ResponseEntity.ok(evaluationService.update(id, dto));
    }

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

package assistance_service.controller;

import assistance_service.dto.AnnotationDTO;
import assistance_service.dto.AnnotationRequestDTO;
import assistance_service.service.AnnotationService;
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
@RequestMapping("/api/annotations")
@RequiredArgsConstructor
@Tag(name = "Anotaciones", description = "Anotaciones conductuales de estudiantes registradas por docentes")
public class AnnotationController {

    private final AnnotationService annotationService;

    @GetMapping
    @Operation(summary = "Listar todas las anotaciones")
    @ApiResponse(responseCode = "200", description = "Lista de anotaciones")
    public ResponseEntity<List<AnnotationDTO>> getAll() {
        return ResponseEntity.ok(annotationService.findAll());
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Anotaciones por estudiante")
    @ApiResponse(responseCode = "200", description = "Anotaciones del estudiante")
    public ResponseEntity<List<AnnotationDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(annotationService.findByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/type/{type}")
    @Operation(summary = "Anotaciones por estudiante y tipo", description = "Filtra por tipo: POSITIVE o NEGATIVE")
    @ApiResponse(responseCode = "200", description = "Anotaciones filtradas")
    public ResponseEntity<List<AnnotationDTO>> getByStudentAndType(@PathVariable Long studentId, @PathVariable String type) {
        return ResponseEntity.ok(annotationService.findByStudentIdAndType(studentId, type));
    }

    @PostMapping
    @Operation(summary = "Crear anotación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Anotación creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AnnotationDTO> create(@Valid @RequestBody AnnotationRequestDTO request) {
        return new ResponseEntity<>(annotationService.create(request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar anotación")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Anotación eliminada"),
        @ApiResponse(responseCode = "404", description = "Anotación no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        annotationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

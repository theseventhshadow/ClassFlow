package com.ohiggins.classflow.assistance.controller;

import com.ohiggins.classflow.assistance.dto.AnnotationDTO;
import com.ohiggins.classflow.assistance.dto.AnnotationRequestDTO;
import com.ohiggins.classflow.assistance.service.AnnotationService;
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
 * Expone los endpoints REST para gestionar anotaciones conductuales.
 */
@RestController
@RequestMapping("/api/annotations")
@RequiredArgsConstructor
@Tag(name = "Anotaciones", description = "Anotaciones conductuales de estudiantes registradas por docentes")
public class AnnotationController {

    private final AnnotationService annotationService;

    /**
     * Obtiene el listado completo de anotaciones.
     *
     * @return respuesta HTTP con la lista de anotaciones.
     */
    @GetMapping
    @Operation(summary = "Listar todas las anotaciones")
    @ApiResponse(responseCode = "200", description = "Lista de anotaciones")
    public ResponseEntity<List<AnnotationDTO>> getAll() {
        return ResponseEntity.ok(annotationService.findAll());
    }

    /**
     * Obtiene las anotaciones de un estudiante.
     *
     * @param studentId identificador del estudiante.
     * @return respuesta HTTP con las anotaciones del estudiante.
     */
    @GetMapping("/student/{studentId}")
    @Operation(summary = "Anotaciones por estudiante")
    @ApiResponse(responseCode = "200", description = "Anotaciones del estudiante")
    public ResponseEntity<List<AnnotationDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(annotationService.findByStudentId(studentId));
    }

    /**
     * Obtiene las anotaciones de un estudiante filtradas por tipo.
     *
     * @param studentId identificador del estudiante.
     * @param type tipo de anotacion.
     * @return respuesta HTTP con las anotaciones filtradas.
     */
    @GetMapping("/student/{studentId}/type/{type}")
    @Operation(summary = "Anotaciones por estudiante y tipo", description = "Filtra por tipo: POSITIVE o NEGATIVE")
    @ApiResponse(responseCode = "200", description = "Anotaciones filtradas")
    public ResponseEntity<List<AnnotationDTO>> getByStudentAndType(@PathVariable Long studentId, @PathVariable String type) {
        return ResponseEntity.ok(annotationService.findByStudentIdAndType(studentId, type));
    }

    /**
     * Crea una nueva anotacion.
     *
     * @param request datos de la anotacion a crear.
     * @return respuesta HTTP con la anotacion creada.
     */
    @PostMapping
    @Operation(summary = "Crear anotación")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Anotación creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AnnotationDTO> create(@Valid @RequestBody AnnotationRequestDTO request) {
        return new ResponseEntity<>(annotationService.create(request), HttpStatus.CREATED);
    }

    /**
     * Elimina una anotacion por su identificador.
     *
     * @param id identificador de la anotacion.
     * @return respuesta HTTP sin contenido.
     */
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

package com.ohiggins.classflow.academic.controller;

import com.ohiggins.classflow.academic.dto.SubjectDTO;
import com.ohiggins.classflow.academic.service.SubjectService;
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
 * Expone los endpoints REST para gestionar asignaturas.
 */
@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
@Tag(name = "Asignaturas", description = "Gestión de asignaturas por curso")
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * Obtiene el listado completo de asignaturas.
     *
     * @return respuesta HTTP con la lista de asignaturas.
     */
    @GetMapping
    @Operation(summary = "Listar todas las asignaturas")
    @ApiResponse(responseCode = "200", description = "Lista de asignaturas")
    public ResponseEntity<List<SubjectDTO>> getAll() {
        return ResponseEntity.ok(subjectService.findAll());
    }

    /**
     * Obtiene las asignaturas de un curso.
     *
     * @param courseId identificador del curso.
     * @return respuesta HTTP con las asignaturas del curso.
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "Listar asignaturas por curso")
    @ApiResponse(responseCode = "200", description = "Asignaturas del curso")
    public ResponseEntity<List<SubjectDTO>> getByCourseId(@PathVariable Long courseId) {
        return ResponseEntity.ok(subjectService.findByCourseId(courseId));
    }

    /**
     * Obtiene una asignatura por su identificador.
     *
     * @param id identificador de la asignatura.
     * @return respuesta HTTP con la asignatura encontrada.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener asignatura por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignatura encontrada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    public ResponseEntity<SubjectDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.findById(id));
    }

    /**
     * Crea una nueva asignatura.
     *
     * @param dto datos de la asignatura a crear.
     * @return respuesta HTTP con la asignatura creada.
     */
    @PostMapping
    @Operation(summary = "Crear asignatura")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Asignatura creada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<SubjectDTO> create(@Valid @RequestBody SubjectDTO dto) {
        return new ResponseEntity<>(subjectService.create(dto), HttpStatus.CREATED);
    }

    /**
     * Actualiza una asignatura existente.
     *
     * @param id identificador de la asignatura.
     * @param dto datos actualizados de la asignatura.
     * @return respuesta HTTP con la asignatura actualizada.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar asignatura")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignatura actualizada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    public ResponseEntity<SubjectDTO> update(@PathVariable Long id, @Valid @RequestBody SubjectDTO dto) {
        return ResponseEntity.ok(subjectService.update(id, dto));
    }

    /**
     * Desactiva una asignatura por su identificador.
     *
     * @param id identificador de la asignatura.
     * @return respuesta HTTP sin contenido.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar asignatura")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Asignatura eliminada"),
        @ApiResponse(responseCode = "404", description = "Asignatura no encontrada")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

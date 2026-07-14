package com.ohiggins.classflow.message.controller;

import com.ohiggins.classflow.message.dto.AnnouncementDTO;
import com.ohiggins.classflow.message.dto.AnnouncementRequestDTO;
import com.ohiggins.classflow.message.service.AnnouncementService;
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
 * Expone los endpoints REST para anuncios por curso.
 */
@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Anuncios", description = "Anuncios publicados por docentes o administradores para cursos")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    /**
     * Obtiene el listado completo de anuncios.
     *
     * @return respuesta HTTP con la lista de anuncios.
     */
    @GetMapping
    @Operation(summary = "Listar todos los anuncios")
    @ApiResponse(responseCode = "200", description = "Lista de anuncios")
    public ResponseEntity<List<AnnouncementDTO>> getAll() {
        return ResponseEntity.ok(announcementService.findAll());
    }

    /**
     * Obtiene los anuncios activos.
     *
     * @return respuesta HTTP con los anuncios activos.
     */
    @GetMapping("/active")
    @Operation(summary = "Listar anuncios activos")
    @ApiResponse(responseCode = "200", description = "Anuncios activos")
    public ResponseEntity<List<AnnouncementDTO>> getActive() {
        return ResponseEntity.ok(announcementService.findActive());
    }

    /**
     * Obtiene los anuncios asociados a un curso.
     *
     * @param courseId identificador del curso.
     * @return respuesta HTTP con los anuncios del curso.
     */
    @GetMapping("/course/{courseId}")
    @Operation(summary = "Anuncios por curso")
    @ApiResponse(responseCode = "200", description = "Anuncios del curso")
    public ResponseEntity<List<AnnouncementDTO>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(announcementService.findByCourseId(courseId));
    }

    /**
     * Crea un nuevo anuncio.
     *
     * @param request datos del anuncio a crear.
     * @return respuesta HTTP con el anuncio creado.
     */
    @PostMapping
    @Operation(summary = "Crear anuncio")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Anuncio creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AnnouncementDTO> create(@Valid @RequestBody AnnouncementRequestDTO request) {
        return new ResponseEntity<>(announcementService.create(request), HttpStatus.CREATED);
    }

    /**
     * Desactiva un anuncio por su identificador.
     *
     * @param id identificador del anuncio.
     * @return respuesta HTTP sin contenido.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar anuncio")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Anuncio eliminado"),
        @ApiResponse(responseCode = "404", description = "Anuncio no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

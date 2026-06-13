package message_service.controller;

import message_service.dto.AnnouncementDTO;
import message_service.dto.AnnouncementRequestDTO;
import message_service.service.AnnouncementService;
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
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
@Tag(name = "Anuncios", description = "Anuncios publicados por docentes o administradores para cursos")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @GetMapping
    @Operation(summary = "Listar todos los anuncios")
    @ApiResponse(responseCode = "200", description = "Lista de anuncios")
    public ResponseEntity<List<AnnouncementDTO>> getAll() {
        return ResponseEntity.ok(announcementService.findAll());
    }

    @GetMapping("/active")
    @Operation(summary = "Listar anuncios activos")
    @ApiResponse(responseCode = "200", description = "Anuncios activos")
    public ResponseEntity<List<AnnouncementDTO>> getActive() {
        return ResponseEntity.ok(announcementService.findActive());
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Anuncios por curso")
    @ApiResponse(responseCode = "200", description = "Anuncios del curso")
    public ResponseEntity<List<AnnouncementDTO>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(announcementService.findByCourseId(courseId));
    }

    @PostMapping
    @Operation(summary = "Crear anuncio")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Anuncio creado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AnnouncementDTO> create(@Valid @RequestBody AnnouncementRequestDTO request) {
        return new ResponseEntity<>(announcementService.create(request), HttpStatus.CREATED);
    }

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

package com.ohiggins.classflow.notification.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ohiggins.classflow.notification.dto.AlertRequestDTO;
import com.ohiggins.classflow.notification.dto.EmailRequestDTO;
import com.ohiggins.classflow.notification.dto.NotificationResponseDTO;
import com.ohiggins.classflow.notification.service.NotificationService;

import java.util.List;

/**
 * Expone los endpoints REST para notificaciones y correos.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificaciones", description = "Envío de emails y alertas internas del sistema")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Envía un correo electrónico directo.
     *
     * @param request datos del correo a enviar.
     * @return respuesta HTTP con el resultado del envío.
     */
    @PostMapping("/email")
    @Operation(summary = "Enviar email", description = "Envía un correo electrónico al destinatario indicado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email enviado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<NotificationResponseDTO> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        NotificationResponseDTO response = notificationService.sendEmailNotification(
                request.getTo(),
                request.getSubject(),
                request.getBody()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * Envía una alerta del sistema.
     *
     * @param request datos de la alerta a enviar.
     * @return respuesta HTTP con el resultado del envío.
     */
    @PostMapping("/alert")
    @Operation(summary = "Enviar alerta", description = "Tipos: ABSENCE, NEW_GRADE, ANNOTATION, MESSAGE, ANNOUNCEMENT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alerta enviada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<NotificationResponseDTO> sendAlert(@Valid @RequestBody AlertRequestDTO request) {
        return ResponseEntity.ok(notificationService.sendAlert(request));
    }

    /**
     * Crea una notificacion pendiente sin enviar correo.
     *
     * @param request datos de la notificacion a crear.
     * @return respuesta HTTP con la notificacion creada.
     */
    @PostMapping("/create")
    @Operation(summary = "Crear notificación sin enviar")
    @ApiResponse(responseCode = "200", description = "Notificación creada en estado pendiente")
    public ResponseEntity<NotificationResponseDTO> createNotification(@Valid @RequestBody AlertRequestDTO request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    /**
     * Obtiene las notificaciones de un usuario.
     *
     * @param userId identificador del usuario.
     * @return respuesta HTTP con la lista de notificaciones.
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Notificaciones de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones del usuario")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    /**
     * Obtiene las notificaciones pendientes de un usuario.
     *
     * @param userId identificador del usuario.
     * @return respuesta HTTP con la lista de notificaciones pendientes.
     */
    @GetMapping("/user/{userId}/pending")
    @Operation(summary = "Notificaciones pendientes de un usuario")
    @ApiResponse(responseCode = "200", description = "Notificaciones aún no enviadas del usuario")
    public ResponseEntity<List<NotificationResponseDTO>> getUserPendingNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getPendingNotifications(userId));
    }

    /**
     * Obtiene todas las notificaciones pendientes del sistema.
     *
     * @return respuesta HTTP con la lista global de pendientes.
     */
    @GetMapping("/pending")
    @Operation(summary = "Todas las notificaciones pendientes del sistema")
    @ApiResponse(responseCode = "200", description = "Lista global de notificaciones pendientes")
    public ResponseEntity<List<NotificationResponseDTO>> getAllPendingNotifications() {
        return ResponseEntity.ok(notificationService.getAllPendingNotifications());
    }

    /**
     * Marca una notificacion como enviada.
     *
     * @param id identificador de la notificacion.
     * @return respuesta HTTP con la notificacion actualizada.
     */
    @PutMapping("/{id}/sent")
    @Operation(summary = "Marcar notificación como enviada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Notificación marcada como enviada"),
        @ApiResponse(responseCode = "404", description = "Notificación no encontrada")
    })
    public ResponseEntity<NotificationResponseDTO> markAsSent(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsSent(id));
    }
}

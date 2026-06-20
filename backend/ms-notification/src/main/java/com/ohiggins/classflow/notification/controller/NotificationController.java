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

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Notificaciones", description = "Envío de emails y alertas internas del sistema")
public class NotificationController {

    private final NotificationService notificationService;

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

    @PostMapping("/alert")
    @Operation(summary = "Enviar alerta", description = "Tipos: ABSENCE, NEW_GRADE, ANNOTATION, MESSAGE, ANNOUNCEMENT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alerta enviada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<NotificationResponseDTO> sendAlert(@Valid @RequestBody AlertRequestDTO request) {
        return ResponseEntity.ok(notificationService.sendAlert(request));
    }

    @PostMapping("/create")
    @Operation(summary = "Crear notificación sin enviar")
    @ApiResponse(responseCode = "200", description = "Notificación creada en estado pendiente")
    public ResponseEntity<NotificationResponseDTO> createNotification(@Valid @RequestBody AlertRequestDTO request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Notificaciones de un usuario")
    @ApiResponse(responseCode = "200", description = "Lista de notificaciones del usuario")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/pending")
    @Operation(summary = "Notificaciones pendientes de un usuario")
    @ApiResponse(responseCode = "200", description = "Notificaciones aún no enviadas del usuario")
    public ResponseEntity<List<NotificationResponseDTO>> getUserPendingNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getPendingNotifications(userId));
    }

    @GetMapping("/pending")
    @Operation(summary = "Todas las notificaciones pendientes del sistema")
    @ApiResponse(responseCode = "200", description = "Lista global de notificaciones pendientes")
    public ResponseEntity<List<NotificationResponseDTO>> getAllPendingNotifications() {
        return ResponseEntity.ok(notificationService.getAllPendingNotifications());
    }

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

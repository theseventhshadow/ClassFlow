package notification_service.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import notification_service.dto.AlertRequestDTO;
import notification_service.dto.EmailRequestDTO;
import notification_service.dto.NotificationResponseDTO;
import notification_service.service.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    // Enviar email
    @PostMapping("/email")
    public ResponseEntity<NotificationResponseDTO> sendEmail(@Valid @RequestBody EmailRequestDTO request) {
        NotificationResponseDTO response = notificationService.sendEmailNotification(
                request.getTo(),
                request.getSubject(),
                request.getBody()
        );
        return ResponseEntity.ok(response);
    }

    // Enviar alerta (ABSENCE, NEW_GRADE, ANNOTATION, MESSAGE, ANNOUNCEMENT)
    @PostMapping("/alert")
    public ResponseEntity<NotificationResponseDTO> sendAlert(@Valid @RequestBody AlertRequestDTO request) {
        NotificationResponseDTO response = notificationService.sendAlert(request);
        return ResponseEntity.ok(response);
    }

    // Crear notificación (sin enviar aún)
    @PostMapping("/create")
    public ResponseEntity<NotificationResponseDTO> createNotification(@Valid @RequestBody AlertRequestDTO request) {
        NotificationResponseDTO response = notificationService.createNotification(request);
        return ResponseEntity.ok(response);
    }

    // Notificaciones de un usuario
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    // Notificaciones pendientes de un usuario
    @GetMapping("/user/{userId}/pending")
    public ResponseEntity<List<NotificationResponseDTO>> getUserPendingNotifications(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getPendingNotifications(userId));
    }

    // Todas las notificaciones pendientes (sistema)
    @GetMapping("/pending")
    public ResponseEntity<List<NotificationResponseDTO>> getAllPendingNotifications() {
        return ResponseEntity.ok(notificationService.getAllPendingNotifications());
    }

    // Marcar como enviada
    @PutMapping("/{id}/sent")
    public ResponseEntity<NotificationResponseDTO> markAsSent(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsSent(id));
    }
}
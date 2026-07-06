package com.ohiggins.classflow.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.ohiggins.classflow.notification.dto.AlertRequestDTO;
import com.ohiggins.classflow.notification.dto.NotificationResponseDTO;
import com.ohiggins.classflow.notification.entity.Notification;
import com.ohiggins.classflow.notification.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene la logica de negocio para notificaciones del sistema.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    /**
     * Crea una notificacion pendiente en la base de datos.
     *
     * @param request datos de la notificacion a crear.
     * @return notificacion creada convertida a DTO.
     */
    public NotificationResponseDTO createNotification(AlertRequestDTO request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setSent(false);

        Notification saved = notificationRepository.save(notification);
        log.info("Notification created for user {}: {}", request.getUserId(), request.getType());

        return convertToDTO(saved);
    }

    /**
     * Envía un correo y registra la notificacion asociada.
     *
     * @param to destinatario del correo.
     * @param subject asunto del correo.
     * @param body contenido del correo.
     * @return notificacion registrada convertida a DTO.
     */
    public NotificationResponseDTO sendEmailNotification(String to, String subject, String body) {
        boolean emailSent = emailService.sendEmail(to, subject, body);

        Notification notification = new Notification();
        notification.setUserId(null); // email sin userId asociado
        notification.setType(null);
        notification.setSubject(subject);
        notification.setContent(body);
        notification.setSent(emailSent);
        notification.setSentAt(emailSent ? LocalDateTime.now() : null);
        if (!emailSent) {
            notification.setErrorMessage("Email delivery failed");
        }

        Notification saved = notificationRepository.save(notification);
        return convertToDTO(saved);
    }

    /**
     * Registra y envía una alerta del sistema.
     *
     * @param request datos de la alerta.
     * @return alerta registrada convertida a DTO.
     */
    public NotificationResponseDTO sendAlert(AlertRequestDTO request) {
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setSubject(request.getSubject());
        notification.setContent(request.getContent());
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        log.info("Alert sent to user {}: {}", request.getUserId(), request.getType());

        // Si se proporcionó email, también enviar por correo
        if (request.getUserEmail() != null && !request.getUserEmail().isEmpty()) {
            emailService.sendEmail(request.getUserEmail(), request.getSubject(), request.getContent());
        }

        return convertToDTO(saved);
    }

    /**
     * Obtiene las notificaciones de un usuario.
     *
     * @param userId identificador del usuario.
     * @return lista de notificaciones convertidas a DTO.
     */
    public List<NotificationResponseDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene las notificaciones pendientes de un usuario.
     *
     * @param userId identificador del usuario.
     * @return lista de notificaciones convertidas a DTO.
     */
    public List<NotificationResponseDTO> getPendingNotifications(Long userId) {
        return notificationRepository.findByUserIdAndSentFalse(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las notificaciones pendientes del sistema.
     *
     * @return lista de notificaciones convertidas a DTO.
     */
    public List<NotificationResponseDTO> getAllPendingNotifications() {
        return notificationRepository.findBySentFalse()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Marca una notificacion como enviada.
     *
     * @param id identificador de la notificacion.
     * @return notificacion actualizada convertida a DTO.
     */
    public NotificationResponseDTO markAsSent(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());
        return convertToDTO(notificationRepository.save(notification));
    }

    private NotificationResponseDTO convertToDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setType(notification.getType());
        dto.setSubject(notification.getSubject());
        dto.setContent(notification.getContent());
        dto.setSent(notification.getSent());
        dto.setSentAt(notification.getSentAt());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setErrorMessage(notification.getErrorMessage());
        return dto;
    }
}
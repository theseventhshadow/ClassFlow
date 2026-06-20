package com.ohiggins.classflow.notification.service;

import com.ohiggins.classflow.notification.dto.AlertRequestDTO;
import com.ohiggins.classflow.notification.dto.NotificationResponseDTO;
import com.ohiggins.classflow.notification.entity.Notification;
import com.ohiggins.classflow.notification.enums.NotificationType;
import com.ohiggins.classflow.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationService Tests")
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;
    private AlertRequestDTO alertRequest;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1L);
        notification.setUserId(101L);
        notification.setType(NotificationType.ALERT);
        notification.setSubject("Test subject");
        notification.setContent("Test content");
        notification.setSent(false);
        notification.setCreatedAt(LocalDateTime.now());

        alertRequest = new AlertRequestDTO();
        alertRequest.setUserId(101L);
        alertRequest.setType(NotificationType.ALERT);
        alertRequest.setSubject("Test subject");
        alertRequest.setContent("Test content");
        alertRequest.setUserEmail("user@example.com");
    }

    @Test
    @DisplayName("Should create notification successfully")
    void testCreateNotification() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificationResponseDTO result = notificationService.createNotification(alertRequest);

        assertThat(result)
                .isNotNull()
                .extracting(NotificationResponseDTO::getUserId, NotificationResponseDTO::getType, NotificationResponseDTO::getSent)
                .containsExactly(101L, NotificationType.ALERT, false);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should send email notification successfully")
    void testSendEmailNotification() {
        Notification saved = new Notification();
        saved.setId(2L);
        saved.setUserId(null);
        saved.setType(null);
        saved.setSubject("Welcome");
        saved.setContent("Hello");
        saved.setSent(true);
        saved.setSentAt(LocalDateTime.now());
        saved.setCreatedAt(LocalDateTime.now());

        when(emailService.sendEmail("user@example.com", "Welcome", "Hello")).thenReturn(true);
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponseDTO result = notificationService.sendEmailNotification("user@example.com", "Welcome", "Hello");

        assertThat(result)
                .isNotNull()
                .extracting(NotificationResponseDTO::getSent, NotificationResponseDTO::getSubject)
                .containsExactly(true, "Welcome");

        verify(emailService, times(1)).sendEmail("user@example.com", "Welcome", "Hello");
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should store failed email notification with error message")
    void testSendEmailNotificationFailure() {
        Notification saved = new Notification();
        saved.setId(3L);
        saved.setSubject("Welcome");
        saved.setContent("Hello");
        saved.setSent(false);
        saved.setErrorMessage("Email delivery failed");
        saved.setCreatedAt(LocalDateTime.now());

        when(emailService.sendEmail("user@example.com", "Welcome", "Hello")).thenReturn(false);
        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponseDTO result = notificationService.sendEmailNotification("user@example.com", "Welcome", "Hello");

        assertThat(result)
                .isNotNull()
                .extracting(NotificationResponseDTO::getSent, NotificationResponseDTO::getErrorMessage)
                .containsExactly(false, "Email delivery failed");
    }

    @Test
    @DisplayName("Should send alert and email when user email is present")
    void testSendAlertWithEmail() {
        Notification saved = new Notification();
        saved.setId(4L);
        saved.setUserId(101L);
        saved.setType(NotificationType.ALERT);
        saved.setSubject("Alert subject");
        saved.setContent("Alert content");
        saved.setSent(true);
        saved.setSentAt(LocalDateTime.now());
        saved.setCreatedAt(LocalDateTime.now());

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);
        when(emailService.sendEmail("user@example.com", "Test subject", "Test content")).thenReturn(true);

        NotificationResponseDTO result = notificationService.sendAlert(alertRequest);

        assertThat(result)
                .isNotNull()
                .extracting(NotificationResponseDTO::getUserId, NotificationResponseDTO::getSent)
                .containsExactly(101L, true);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emailService, times(1)).sendEmail("user@example.com", "Test subject", "Test content");
    }

    @Test
    @DisplayName("Should send alert without email when user email is absent")
    void testSendAlertWithoutEmail() {
        alertRequest.setUserEmail(null);

        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationService.sendAlert(alertRequest);

        verify(notificationRepository, times(1)).save(any(Notification.class));
        verify(emailService, never()).sendEmail(any(), any(), any());
    }

    @Test
    @DisplayName("Should find notifications by user id")
    void testGetNotificationsByUser() {
        when(notificationRepository.findByUserId(101L)).thenReturn(List.of(notification));

        List<NotificationResponseDTO> result = notificationService.getNotificationsByUser(101L);

        assertThat(result)
                .hasSize(1)
                .extracting(NotificationResponseDTO::getSubject)
                .containsExactly("Test subject");
    }

    @Test
    @DisplayName("Should find pending notifications by user id")
    void testGetPendingNotifications() {
        when(notificationRepository.findByUserIdAndSentFalse(101L)).thenReturn(List.of(notification));

        List<NotificationResponseDTO> result = notificationService.getPendingNotifications(101L);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should find all pending notifications")
    void testGetAllPendingNotifications() {
        when(notificationRepository.findBySentFalse()).thenReturn(List.of(notification));

        List<NotificationResponseDTO> result = notificationService.getAllPendingNotifications();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should mark notification as sent")
    void testMarkAsSent() {
        Notification updated = new Notification();
        updated.setId(1L);
        updated.setUserId(101L);
        updated.setType(NotificationType.ALERT);
        updated.setSubject("Test subject");
        updated.setContent("Test content");
        updated.setSent(true);
        updated.setSentAt(LocalDateTime.now());
        updated.setCreatedAt(LocalDateTime.now());

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(updated);

        NotificationResponseDTO result = notificationService.markAsSent(1L);

        assertThat(result)
                .isNotNull()
                .extracting(NotificationResponseDTO::getSent)
                .isEqualTo(true);

        verify(notificationRepository, times(1)).findById(1L);
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    @DisplayName("Should throw RuntimeException when notification not found")
    void testMarkAsSentNotFound() {
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsSent(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Notification not found with id: 999");
    }
}
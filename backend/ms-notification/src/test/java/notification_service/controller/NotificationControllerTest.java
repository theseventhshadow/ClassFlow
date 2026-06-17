package notification_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import notification_service.dto.AlertRequestDTO;
import notification_service.dto.EmailRequestDTO;
import notification_service.dto.NotificationResponseDTO;
import notification_service.enums.NotificationType;
import notification_service.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@DisplayName("NotificationController Tests")
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private NotificationService notificationService;

    private NotificationResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        responseDTO = new NotificationResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setUserId(101L);
        responseDTO.setType(NotificationType.ALERT);
        responseDTO.setSubject("Subject");
        responseDTO.setContent("Content");
        responseDTO.setSent(true);
        responseDTO.setSentAt(LocalDateTime.of(2026, 5, 15, 12, 0));
        responseDTO.setCreatedAt(LocalDateTime.of(2026, 5, 15, 11, 0));
    }

    @Test
    @DisplayName("Should send email notification")
    void testSendEmail() throws Exception {
        EmailRequestDTO request = new EmailRequestDTO();
        request.setTo("user@example.com");
        request.setSubject("Subject");
        request.setBody("Body");

        when(notificationService.sendEmailNotification("user@example.com", "Subject", "Body")).thenReturn(responseDTO);

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)))
                .andExpect(jsonPath("$.sent", equalTo(true)));

        verify(notificationService, times(1)).sendEmailNotification("user@example.com", "Subject", "Body");
    }

    @Test
    @DisplayName("Should send alert notification")
    void testSendAlert() throws Exception {
        AlertRequestDTO request = new AlertRequestDTO();
        request.setUserId(101L);
        request.setType(NotificationType.ALERT);
        request.setSubject("Subject");
        request.setContent("Content");
        request.setUserEmail("user@example.com");

        when(notificationService.sendAlert(any(AlertRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/notifications/alert")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", equalTo(101)));

        verify(notificationService, times(1)).sendAlert(any(AlertRequestDTO.class));
    }

    @Test
    @DisplayName("Should create notification")
    void testCreateNotification() throws Exception {
        AlertRequestDTO request = new AlertRequestDTO();
        request.setUserId(101L);
        request.setType(NotificationType.PUSH_NOTIFICATION);
        request.setSubject("Subject");
        request.setContent("Content");

        when(notificationService.createNotification(any(AlertRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/notifications/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.subject", equalTo("Subject")));

        verify(notificationService, times(1)).createNotification(any(AlertRequestDTO.class));
    }

    @Test
    @DisplayName("Should get notifications by user")
    void testGetUserNotifications() throws Exception {
        when(notificationService.getNotificationsByUser(101L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notifications/user/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(notificationService, times(1)).getNotificationsByUser(101L);
    }

    @Test
    @DisplayName("Should get pending notifications by user")
    void testGetUserPendingNotifications() throws Exception {
        when(notificationService.getPendingNotifications(101L)).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notifications/user/101/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(notificationService, times(1)).getPendingNotifications(101L);
    }

    @Test
    @DisplayName("Should get all pending notifications")
    void testGetAllPendingNotifications() throws Exception {
        when(notificationService.getAllPendingNotifications()).thenReturn(List.of(responseDTO));

        mockMvc.perform(get("/api/notifications/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(notificationService, times(1)).getAllPendingNotifications();
    }

    @Test
    @DisplayName("Should mark notification as sent")
    void testMarkAsSent() throws Exception {
        when(notificationService.markAsSent(1L)).thenReturn(responseDTO);

        mockMvc.perform(put("/api/notifications/1/sent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sent", equalTo(true)));

        verify(notificationService, times(1)).markAsSent(1L);
    }
}
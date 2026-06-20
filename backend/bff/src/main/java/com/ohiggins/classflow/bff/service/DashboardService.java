package com.ohiggins.classflow.bff.service;

import com.ohiggins.classflow.bff.dto.AdminAlertDTO;
import com.ohiggins.classflow.bff.dto.CourseAttendanceDTO;
import com.ohiggins.classflow.bff.dto.DashboardResponse;
import com.ohiggins.classflow.bff.dto.DashboardStatsDTO;
import com.ohiggins.classflow.bff.dto.DashboardUserDTO;
import com.ohiggins.classflow.bff.dto.RecentActivityDTO;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Service
public class DashboardService {

    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);

    private final WebClient authWebClient;
    private final WebClient academicWebClient;
    private final WebClient assistanceWebClient;
    private final WebClient messageWebClient;
    private final WebClient notificationWebClient;

        public DashboardService(
                        @Qualifier("authWebClient") WebClient authWebClient,
                        @Qualifier("academicWebClient") WebClient academicWebClient,
                        @Qualifier("assistanceWebClient") WebClient assistanceWebClient,
                        @Qualifier("messageWebClient") WebClient messageWebClient,
                        @Qualifier("notificationWebClient") WebClient notificationWebClient) {
                this.authWebClient = authWebClient;
                this.academicWebClient = academicWebClient;
                this.assistanceWebClient = assistanceWebClient;
                this.messageWebClient = messageWebClient;
                this.notificationWebClient = notificationWebClient;
        }

    public Mono<DashboardResponse> getDashboard(Long userId) {
        Mono<JsonNode> userMono = fetchObject(authWebClient, "/api/auth/users/{userId}", userId);
        Mono<List<JsonNode>> coursesMono = fetchList(academicWebClient, "/api/courses");
        Mono<List<JsonNode>> subjectsMono = fetchList(academicWebClient, "/api/subjects");
        Mono<List<JsonNode>> evaluationsMono = fetchList(academicWebClient, "/api/evaluations");

        return userMono.switchIfEmpty(Mono.error(new RuntimeException("User not found: " + userId)))
                .flatMap(user -> {
            String role = user.path("role").asText("UNKNOWN");
            boolean isStudent = "STUDENT".equals(role);

            Mono<List<JsonNode>> gradesMono = isStudent
                ? fetchList(academicWebClient, "/api/grades/student/{userId}", userId)
                : fetchList(academicWebClient, "/api/grades");

            Mono<List<JsonNode>> attendancesMono = isStudent
                ? fetchList(assistanceWebClient, "/api/attendance/student/{userId}", userId)
                : fetchList(assistanceWebClient, "/api/attendance");

            Mono<List<JsonNode>> annotationsMono = isStudent
                ? fetchList(assistanceWebClient, "/api/annotations/student/{userId}", userId)
                : fetchList(assistanceWebClient, "/api/annotations");

            Mono<List<JsonNode>> messagesMono = fetchList(messageWebClient, "/api/messages/receiver/{userId}", userId);
            Mono<List<JsonNode>> unreadMessagesMono = fetchList(messageWebClient, "/api/messages/receiver/{userId}/unread", userId);
            Mono<List<JsonNode>> announcementsMono = fetchList(messageWebClient, "/api/announcements/active");
            Mono<List<JsonNode>> notificationsMono = fetchList(notificationWebClient, "/api/notifications/user/{userId}", userId);
            Mono<List<JsonNode>> pendingNotificationsMono = fetchList(notificationWebClient, "/api/notifications/user/{userId}/pending", userId);

            return Mono.zip(objects -> new DashboardResponse(
                            user,
                            role,
                            (List<JsonNode>) objects[0],
                            (List<JsonNode>) objects[1],
                            (List<JsonNode>) objects[2],
                            (List<JsonNode>) objects[3],
                            (List<JsonNode>) objects[4],
                            (List<JsonNode>) objects[5],
                            (List<JsonNode>) objects[6],
                            (List<JsonNode>) objects[7],
                            (List<JsonNode>) objects[8],
                            (List<JsonNode>) objects[9],
                            (List<JsonNode>) objects[10]
                    ),
                    coursesMono,
                    subjectsMono,
                    evaluationsMono,
                    gradesMono,
                    attendancesMono,
                    annotationsMono,
                    messagesMono,
                    unreadMessagesMono,
                    announcementsMono,
                    notificationsMono,
                    pendingNotificationsMono
            );
        });
    }

    // En DashboardService.java, agregar:

    public Mono<DashboardStatsDTO> getStats(Long userId) {
        Mono<List<JsonNode>> coursesMono = fetchList(academicWebClient, "/api/courses");
        Mono<List<JsonNode>> attendancesMono = fetchList(assistanceWebClient, "/api/attendance/student/{userId}", userId);
    
        return Mono.zip(coursesMono, attendancesMono)
            .map(tuple -> {
                List<JsonNode> courses = tuple.getT1();
                List<JsonNode> attendances = tuple.getT2();
            
                long totalStudents = 150L;  // TODO: Obtener de ms-auth
                long activeCourses = courses.stream().filter(c -> c.path("active").asBoolean()).count();
            
                long present = attendances.stream().filter(a -> a.path("present").asBoolean()).count();
                double attendanceRate = attendances.isEmpty() ? 0 : (present * 100.0) / attendances.size();
            
                long activeAlerts = 7L;     // TODO: Obtener de ms-notification
            
                return DashboardStatsDTO.builder()
                    .totalStudents(totalStudents)
                    .activeCourses(activeCourses)
                    .attendanceRate(attendanceRate)
                    .activeAlerts(activeAlerts)
                    .studentsTrend(2.5)
                    .attendanceTrend(1.2)
                    .build();
            });
}

    public Mono<List<DashboardUserDTO>> getRecentUsers(Long limit) {
        return fetchList(authWebClient, "/api/auth/users?limit={limit}", limit)
            .map(users -> users.stream()
                .map(u -> DashboardUserDTO.builder()
                    .id(u.path("id").asLong())
                    .nombre(u.path("nombre").asText())
                    .email(u.path("email").asText())
                    .rol(u.path("rol").asText())
                    .estado("activo")
                    .lastAccess(null) // Calcular de attendances si existe
                    .build()
                )
                .toList()
            );
    }

    public Mono<List<CourseAttendanceDTO>> getCourseAttendance() {
        return fetchList(academicWebClient, "/api/courses")
            .map(courses -> courses.stream()
                .map(c -> CourseAttendanceDTO.builder()
                    .courseId(c.path("id").asLong())
                    .courseName(c.path("name").asText())
                    .attendanceRate(85.0)  // TODO: Calcular desde attendance records
                    .build()
                )
                .toList()
            );
    }

    public Mono<List<RecentActivityDTO>> getRecentActivity(Long limit) {
        return fetchList(assistanceWebClient, "/api/annotations?limit={limit}", limit)
            .map(annotations -> annotations.stream()
                .map(a -> RecentActivityDTO.builder()
                    .id(a.path("id").asLong())
                    .type(a.path("type").asText().equals("POSITIVE") ? "success" : "warning")
                    .text("Anotación: " + a.path("description").asText())
                    .timestamp(null)  // Parse date
                    .build()
                )
                .toList()
            );
    }

    public Mono<List<AdminAlertDTO>> getAlerts() {
        return fetchList(notificationWebClient, "/api/notifications")
            .map(notifications -> notifications.stream()
                .map(n -> AdminAlertDTO.builder()
                    .id(n.path("id").asLong())
                    .text(n.path("content").asText())
                    .severity("medium")  // Mapear de type
                    .build()
                )
                .toList()
            );
    }

    private Mono<JsonNode> fetchObject(WebClient client, String uri, Object... uriVariables) {
        return client.get()
                .uri(uri, uriVariables)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.empty();
                    }
                    log.warn("Error fetching from {}: {} {}", uri, e.getStatusCode(), e.getMessage());
                    return Mono.empty();
                })
                .onErrorResume(e -> {
                    log.warn("Unexpected error fetching from {}: {}", uri, e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<List<JsonNode>> fetchList(WebClient client, String uri, Object... uriVariables) {
        return client.get()
                .uri(uri, uriVariables)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .collectList()
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        return Mono.just(Collections.emptyList());
                    }
                    log.warn("Error fetching list from {}: {} {}", uri, e.getStatusCode(), e.getMessage());
                    return Mono.just(Collections.emptyList());
                })
                .onErrorResume(e -> {
                    log.warn("Unexpected error fetching list from {}: {}", uri, e.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }
}

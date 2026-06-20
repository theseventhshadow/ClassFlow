package com.ohiggins.classflow.bff.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ohiggins.classflow.bff.dto.*;
import com.ohiggins.classflow.bff.service.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.*;

@WebFluxTest(DashboardController.class)
@DisplayName("DashboardController Tests")
class DashboardControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private DashboardService dashboardService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/{userId} returns dashboard data")
    void testGetDashboard() throws Exception {
        JsonNode userNode = objectMapper.readTree("""
                {"id":1,"firstName":"Admin","role":"ADMINISTRATOR"}
                """);
        DashboardResponse response = new DashboardResponse(
                userNode, "ADMINISTRATOR",
                List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of()
        );

        when(dashboardService.getDashboard(1L)).thenReturn(Mono.just(response));

        webTestClient.get().uri("/api/bff/dashboard/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role").isEqualTo("ADMINISTRATOR")
                .jsonPath("$.user.role").isEqualTo("ADMINISTRATOR");

        verify(dashboardService, times(1)).getDashboard(1L);
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/stats/{userId} returns stats")
    void testGetStats() {
        DashboardStatsDTO stats = DashboardStatsDTO.builder()
                .totalStudents(150L)
                .activeCourses(10L)
                .attendanceRate(85.0)
                .activeAlerts(7L)
                .studentsTrend(2.5)
                .attendanceTrend(1.2)
                .build();

        when(dashboardService.getStats(1L)).thenReturn(Mono.just(stats));

        webTestClient.get().uri("/api/bff/dashboard/stats/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.totalStudents").isEqualTo(150)
                .jsonPath("$.activeCourses").isEqualTo(10)
                .jsonPath("$.attendanceRate").isEqualTo(85.0);

        verify(dashboardService, times(1)).getStats(1L);
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/users/recent returns users list")
    void testGetRecentUsers() {
        List<DashboardUserDTO> users = List.of(
                DashboardUserDTO.builder().id(1L).nombre("Admin").email("admin@c.cl").rol("ADMIN").estado("activo").build()
        );

        when(dashboardService.getRecentUsers(5L)).thenReturn(Mono.just(users));

        webTestClient.get().uri("/api/bff/dashboard/users/recent?limit=5")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].nombre").isEqualTo("Admin");

        verify(dashboardService, times(1)).getRecentUsers(5L);
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/users/recent uses default limit")
    void testGetRecentUsersDefaultLimit() {
        when(dashboardService.getRecentUsers(5L)).thenReturn(Mono.just(List.of()));

        webTestClient.get().uri("/api/bff/dashboard/users/recent")
                .exchange()
                .expectStatus().isOk();

        verify(dashboardService, times(1)).getRecentUsers(5L);
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/attendance/by-course returns attendance")
    void testGetCourseAttendance() {
        List<CourseAttendanceDTO> attendance = List.of(
                CourseAttendanceDTO.builder().courseId(1L).courseName("Math").attendanceRate(90.0).build()
        );

        when(dashboardService.getCourseAttendance()).thenReturn(Mono.just(attendance));

        webTestClient.get().uri("/api/bff/dashboard/attendance/by-course")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].courseName").isEqualTo("Math");

        verify(dashboardService, times(1)).getCourseAttendance();
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/activity returns recent activity")
    void testGetRecentActivity() {
        List<RecentActivityDTO> activities = List.of(
                RecentActivityDTO.builder().id(1L).type("success").text("Actividad reciente").build()
        );

        when(dashboardService.getRecentActivity(4L)).thenReturn(Mono.just(activities));

        webTestClient.get().uri("/api/bff/dashboard/activity?limit=4")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].text").isEqualTo("Actividad reciente");

        verify(dashboardService, times(1)).getRecentActivity(4L);
    }

    @Test
    @DisplayName("GET /api/bff/dashboard/alerts returns alerts")
    void testGetAlerts() {
        List<AdminAlertDTO> alerts = List.of(
                AdminAlertDTO.builder().id(1L).text("Alerta crítica").severity("high").build()
        );

        when(dashboardService.getAlerts()).thenReturn(Mono.just(alerts));

        webTestClient.get().uri("/api/bff/dashboard/alerts")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].text").isEqualTo("Alerta crítica");

        verify(dashboardService, times(1)).getAlerts();
    }
}

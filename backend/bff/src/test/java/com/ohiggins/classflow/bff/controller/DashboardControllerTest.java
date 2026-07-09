package com.ohiggins.classflow.bff.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ohiggins.classflow.bff.dto.DashboardResponse;
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
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

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

        when(dashboardService.getDashboard(eq(1L), anyString())).thenReturn(Mono.just(response));

        webTestClient
                .mutateWith(mockUser().roles("ADMINISTRATOR"))
                .get().uri("/api/bff/dashboard/1")
                .header("Authorization", "Bearer test-token")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role").isEqualTo("ADMINISTRATOR")
                .jsonPath("$.user.role").isEqualTo("ADMINISTRATOR");

        verify(dashboardService, times(1)).getDashboard(eq(1L), anyString());
    }
}

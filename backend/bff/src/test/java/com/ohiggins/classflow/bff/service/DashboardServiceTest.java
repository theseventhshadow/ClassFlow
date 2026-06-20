package com.ohiggins.classflow.bff.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("DashboardService Tests")
class DashboardServiceTest {

    @Mock
    private WebClient authWebClient;

    @Mock
    private WebClient academicWebClient;

    @Mock
    private WebClient assistanceWebClient;

    @Mock
    private WebClient messageWebClient;

    @Mock
    private WebClient notificationWebClient;

    private DashboardService dashboardService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        dashboardService = new DashboardService(
                authWebClient, academicWebClient, assistanceWebClient,
                messageWebClient, notificationWebClient
        );
        objectMapper = new ObjectMapper();
    }

    private void mockFetchList(WebClient client, Flux<JsonNode> flux) {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(client.get()).thenReturn(uriSpec);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(headersSpec);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(JsonNode.class)).thenReturn(flux);
    }

    private void mockFetchObject(WebClient client, Mono<JsonNode> mono) {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        when(client.get()).thenReturn(uriSpec);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        when(uriSpec.uri(anyString(), any(Object[].class))).thenReturn(headersSpec);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(JsonNode.class)).thenReturn(mono);
    }

    @Test
    @DisplayName("getDashboard returns DashboardResponse for a student user")
    void testGetDashboardStudent() throws Exception {
        JsonNode userNode = objectMapper.readTree("{\"id\":10,\"firstName\":\"Benjamín\",\"role\":\"STUDENT\"}");
        mockFetchObject(authWebClient, Mono.just(userNode));
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(assistanceWebClient, Flux.just());
        mockFetchList(assistanceWebClient, Flux.just());
        mockFetchList(messageWebClient, Flux.just());
        mockFetchList(messageWebClient, Flux.just());
        mockFetchList(messageWebClient, Flux.just());
        mockFetchList(notificationWebClient, Flux.just());
        mockFetchList(notificationWebClient, Flux.just());

        var result = dashboardService.getDashboard(10L).block();

        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo("STUDENT");
        assertThat(result.user().path("firstName").asText()).isEqualTo("Benjamín");
    }

    @Test
    @DisplayName("getDashboard returns DashboardResponse for a teacher user")
    void testGetDashboardTeacher() throws Exception {
        JsonNode userNode = objectMapper.readTree("{\"id\":2,\"firstName\":\"María\",\"role\":\"TEACHER\"}");
        mockFetchObject(authWebClient, Mono.just(userNode));
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(assistanceWebClient, Flux.just());
        mockFetchList(assistanceWebClient, Flux.just());
        mockFetchList(messageWebClient, Flux.just());
        mockFetchList(messageWebClient, Flux.just());
        mockFetchList(messageWebClient, Flux.just());
        mockFetchList(notificationWebClient, Flux.just());
        mockFetchList(notificationWebClient, Flux.just());

        var result = dashboardService.getDashboard(2L).block();

        assertThat(result).isNotNull();
        assertThat(result.role()).isEqualTo("TEACHER");
        assertThat(result.user().path("firstName").asText()).isEqualTo("María");
    }

    @Test
    @DisplayName("getDashboard handles user not found")
    void testGetDashboardUserNotFound() {
        mockFetchObject(authWebClient, Mono.empty());
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(academicWebClient, Flux.just());
        mockFetchList(academicWebClient, Flux.just());

        assertThatThrownBy(() -> dashboardService.getDashboard(999L).block())
                .hasMessageContaining("User not found");
    }

    @Test
    @DisplayName("getStats returns stats with calculated values")
    void testGetStats() throws Exception {
        ArrayNode courses = objectMapper.createArrayNode();
        courses.add(objectMapper.readTree("{\"id\":1,\"name\":\"Math\",\"active\":true}"));

        ArrayNode attendances = objectMapper.createArrayNode();
        attendances.add(objectMapper.readTree("{\"id\":1,\"present\":true}"));
        attendances.add(objectMapper.readTree("{\"id\":2,\"present\":false}"));

        mockFetchList(academicWebClient, Flux.fromIterable(courses));
        mockFetchList(assistanceWebClient, Flux.fromIterable(attendances));

        var result = dashboardService.getStats(10L).block();

        assertThat(result).isNotNull();
        assertThat(result.getActiveCourses()).isEqualTo(1);
        assertThat(result.getAttendanceRate()).isEqualTo(50.0);
        assertThat(result.getTotalStudents()).isEqualTo(150L);
        assertThat(result.getActiveAlerts()).isEqualTo(7L);
    }

    @Test
    @DisplayName("getRecentUsers returns user list")
    void testGetRecentUsers() throws Exception {
        ArrayNode users = objectMapper.createArrayNode();
        users.add(objectMapper.readTree("{\"id\":1,\"nombre\":\"Admin\",\"email\":\"admin@c.cl\",\"rol\":\"ADMIN\"}"));

        mockFetchList(authWebClient, Flux.fromIterable(users));

        var result = dashboardService.getRecentUsers(5L).block();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNombre()).isEqualTo("Admin");
    }

    @Test
    @DisplayName("getCourseAttendance returns course list")
    void testGetCourseAttendance() throws Exception {
        ArrayNode courses = objectMapper.createArrayNode();
        courses.add(objectMapper.readTree("{\"id\":1,\"name\":\"Math\"}"));

        mockFetchList(academicWebClient, Flux.fromIterable(courses));

        var result = dashboardService.getCourseAttendance().block();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCourseName()).isEqualTo("Math");
    }

    @Test
    @DisplayName("getRecentActivity returns activity list")
    void testGetRecentActivity() throws Exception {
        ArrayNode annotations = objectMapper.createArrayNode();
        annotations.add(objectMapper.readTree("{\"id\":1,\"type\":\"POSITIVE\",\"description\":\"Buen trabajo\"}"));

        mockFetchList(assistanceWebClient, Flux.fromIterable(annotations));

        var result = dashboardService.getRecentActivity(4L).block();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).contains("Buen trabajo");
    }

    @Test
    @DisplayName("getAlerts returns alert list")
    void testGetAlerts() throws Exception {
        ArrayNode notifications = objectMapper.createArrayNode();
        notifications.add(objectMapper.readTree("{\"id\":1,\"content\":\"Alerta de prueba\"}"));

        mockFetchList(notificationWebClient, Flux.fromIterable(notifications));

        var result = dashboardService.getAlerts().block();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getText()).isEqualTo("Alerta de prueba");
    }

    @Test
    @DisplayName("getAlerts handles empty response")
    void testGetAlertsEmpty() {
        mockFetchList(notificationWebClient, Flux.empty());

        var result = dashboardService.getAlerts().block();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}

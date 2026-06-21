package com.ohiggins.classflow.bff.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
}

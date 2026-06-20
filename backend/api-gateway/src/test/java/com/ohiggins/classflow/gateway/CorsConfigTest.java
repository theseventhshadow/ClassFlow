package com.ohiggins.classflow.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CorsConfig Tests")
class CorsConfigTest {

    private final CorsConfig corsConfig = new CorsConfig();

    @Test
    @DisplayName("Should create a CorsWebFilter bean")
    void testCorsWebFilterBean() {
        CorsWebFilter filter = corsConfig.corsWebFilter();

        assertThat(filter).isNotNull();
    }

    @Test
    @DisplayName("Should allow any origin, header and method with credentials")
    void testCorsConfiguration() {
        CorsWebFilter filter = corsConfig.corsWebFilter();

        MockServerWebExchange exchange = MockServerWebExchange.from(
                MockServerHttpRequest.method(HttpMethod.GET, "/api/auth/login")
                        .header("Origin", "http://localhost:4200")
                        .build());

        CorsConfigurationSource source = (CorsConfigurationSource) ReflectionTestUtils.getField(filter, "configSource");
        CorsConfiguration configuration = source.getCorsConfiguration(exchange);

        assertThat(configuration).isNotNull();
        assertThat(configuration.checkOrigin("http://localhost:4200")).isEqualTo("http://localhost:4200");
        assertThat(configuration.getAllowedMethods()).contains("*");
        assertThat(configuration.getAllowedHeaders()).contains("*");
        assertThat(configuration.getAllowCredentials()).isTrue();
    }
}
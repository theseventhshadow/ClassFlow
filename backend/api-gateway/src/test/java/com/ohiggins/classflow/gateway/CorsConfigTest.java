package com.ohiggins.classflow.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.cors.reactive.CorsWebFilter;

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
}
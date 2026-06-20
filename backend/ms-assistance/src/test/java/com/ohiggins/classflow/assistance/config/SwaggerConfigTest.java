package com.ohiggins.classflow.assistance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SwaggerConfig Tests")
class SwaggerConfigTest {

    private SwaggerConfig swaggerConfig;

    @BeforeEach
    void setUp() {
        swaggerConfig = new SwaggerConfig();
    }

    @Test
    @DisplayName("Should create OpenAPI bean with correct info")
    void testOpenAPI() {
        OpenAPI openAPI = swaggerConfig.openAPI();
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).contains("Asistencia");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
    }

    @Test
    @DisplayName("Should include contact and license")
    void testContactAndLicense() {
        OpenAPI openAPI = swaggerConfig.openAPI();
        assertThat(openAPI.getInfo().getContact().getEmail()).isEqualTo("team@classflow.cl");
        assertThat(openAPI.getInfo().getLicense().getName()).isEqualTo("MIT");
    }

    @Test
    @DisplayName("Should include security scheme")
    void testSecurityScheme() {
        OpenAPI openAPI = swaggerConfig.openAPI();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearer-jwt");
        assertThat(openAPI.getSecurity()).isNotEmpty();
    }

    @Test
    @DisplayName("Should include server URLs")
    void testServers() {
        OpenAPI openAPI = swaggerConfig.openAPI();
        assertThat(openAPI.getServers()).hasSize(2);
    }
}

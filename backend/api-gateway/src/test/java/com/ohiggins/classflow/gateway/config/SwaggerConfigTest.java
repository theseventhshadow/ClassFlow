package com.ohiggins.classflow.gateway.config;

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
        assertThat(openAPI.getInfo()).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("ClassFlow - API Gateway");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0");
        assertThat(openAPI.getInfo().getDescription()).contains("API Gateway");
    }

    @Test
    @DisplayName("Should include contact and license info")
    void testContactAndLicense() {
        OpenAPI openAPI = swaggerConfig.openAPI();

        Contact contact = openAPI.getInfo().getContact();
        assertThat(contact).isNotNull();
        assertThat(contact.getEmail()).isEqualTo("team@classflow.cl");

        License license = openAPI.getInfo().getLicense();
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT");
    }

    @Test
    @DisplayName("Should include security scheme")
    void testSecurityScheme() {
        OpenAPI openAPI = swaggerConfig.openAPI();

        assertThat(openAPI.getComponents()).isNotNull();
        assertThat(openAPI.getComponents().getSecuritySchemes()).containsKey("bearer-jwt");
        assertThat(openAPI.getSecurity()).isNotEmpty();
    }

    @Test
    @DisplayName("Should include server URLs")
    void testServers() {
        OpenAPI openAPI = swaggerConfig.openAPI();

        assertThat(openAPI.getServers()).isNotEmpty();
        assertThat(openAPI.getServers().get(0).getUrl()).contains("localhost");
    }
}

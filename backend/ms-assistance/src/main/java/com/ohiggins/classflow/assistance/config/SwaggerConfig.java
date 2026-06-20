package com.ohiggins.classflow.assistance.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ClassFlow - Servicio de Asistencia")
                        .version("1.0")
                        .description("API para registro de asistencia diaria y anotaciones conductuales de estudiantes."));
    }
}

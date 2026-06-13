package bff_service.config;

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
                        .title("ClassFlow - BFF (Backend for Frontend)")
                        .version("1.0")
                        .description("API agregada que consolida datos de todos los microservicios para el dashboard del frontend."));
    }
}

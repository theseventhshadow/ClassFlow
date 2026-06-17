package gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    @Bean
public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    // Permite patrones (comodín). Usamos addAllowedOriginPattern en vez de addAllowedOrigin
    // para que setAllowCredentials(true) funcione con orígenes dinámicos.
    corsConfig.addAllowedOriginPattern("*");
    corsConfig.addAllowedMethod("*");
    corsConfig.addAllowedHeader("*");
    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
}
}
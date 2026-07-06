package com.ohiggins.classflow.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configura los WebClient usados para consumir los microservicios del backend.
 */
@Configuration
public class WebClientConfig {

    /**
     * Crea el WebClient para el servicio de autenticacion.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient authWebClient(@Value("${services.auth.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Crea el WebClient para el servicio academico.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient academicWebClient(@Value("${services.academic.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Crea el WebClient para el servicio de asistencia.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient assistanceWebClient(@Value("${services.assistance.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Crea el WebClient para el servicio de mensajeria.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient messageWebClient(@Value("${services.message.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    /**
     * Crea el WebClient para el servicio de notificaciones.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient notificationWebClient(@Value("${services.notification.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}

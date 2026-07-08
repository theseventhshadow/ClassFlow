package com.ohiggins.classflow.bff.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Configura los WebClient usados para consumir los microservicios del backend.
 */
@Configuration
public class WebClientConfig {

    /**
     * Clave del Reactor Context donde DashboardService deja el header Authorization
     * de la peticion entrante, para que todos los WebClient lo reenvien sin que cada
     * llamada tenga que pasarlo como parametro explicito.
     */
    public static final String AUTHORIZATION_CONTEXT_KEY = "classflow.authorization";

    /**
     * Filtro compartido: toma el Authorization del Reactor Context (si existe) y lo agrega
     * a cada peticion saliente, sin que el codigo que arma cada llamada tenga que saberlo.
     */
    private static ExchangeFilterFunction forwardAuthorizationFilter() {
        return (request, next) -> Mono.deferContextual(ctx -> {
            String authorization = ctx.getOrDefault(AUTHORIZATION_CONTEXT_KEY, null);
            if (authorization == null) {
                return next.exchange(request);
            }
            ClientRequest forwarded = ClientRequest.from(request)
                    .header(HttpHeaders.AUTHORIZATION, authorization)
                    .build();
            return next.exchange(forwarded);
        });
    }

    /**
     * Crea el WebClient para el servicio de autenticacion.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient authWebClient(@Value("${services.auth.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).filter(forwardAuthorizationFilter()).build();
    }

    /**
     * Crea el WebClient para el servicio academico.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient academicWebClient(@Value("${services.academic.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).filter(forwardAuthorizationFilter()).build();
    }

    /**
     * Crea el WebClient para el servicio de asistencia.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient assistanceWebClient(@Value("${services.assistance.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).filter(forwardAuthorizationFilter()).build();
    }

    /**
     * Crea el WebClient para el servicio de mensajeria.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient messageWebClient(@Value("${services.message.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).filter(forwardAuthorizationFilter()).build();
    }

    /**
     * Crea el WebClient para el servicio de notificaciones.
     *
     * @param baseUrl URL base del servicio.
     * @return cliente reactivo configurado.
     */
    @Bean
    public WebClient notificationWebClient(@Value("${services.notification.base-url}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).filter(forwardAuthorizationFilter()).build();
    }
}

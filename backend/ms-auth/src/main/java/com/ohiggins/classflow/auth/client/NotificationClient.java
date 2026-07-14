package com.ohiggins.classflow.auth.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Cliente HTTP hacia ms-notification para el envio de correos.
 */
@Component
@Slf4j
public class NotificationClient {

    private static final int CONNECT_TIMEOUT_MS = 3000;
    private static final int READ_TIMEOUT_MS = 5000;

    private final RestClient restClient;
    private final String internalApiKey;

    public NotificationClient(@Value("${services.notification.url}") String notificationServiceUrl,
                               @Value("${internal.api.key}") String internalApiKey) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT_MS);
        requestFactory.setReadTimeout(READ_TIMEOUT_MS);

        this.restClient = RestClient.builder()
                .baseUrl(notificationServiceUrl)
                .requestFactory(requestFactory)
                .build();
        this.internalApiKey = internalApiKey;
    }

    /**
     * Envia un correo a traves de ms-notification. No propaga el error si el envio falla,
     * solo lo registra, para no filtrar el estado interno del sistema de notificaciones al llamador.
     * Este endpoint no tiene un usuario logueado del que reenviar un JWT (recuperacion de
     * contrasena), asi que se identifica como llamada de servicio via X-Internal-Api-Key.
     *
     * @param to destinatario.
     * @param subject asunto.
     * @param body contenido.
     */
    public void sendEmail(String to, String subject, String body) {
        try {
            restClient.post()
                    .uri("/api/notifications/email")
                    .header("X-Internal-Api-Key", internalApiKey)
                    .body(Map.of("to", to, "subject", subject, "body", body))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("No se pudo enviar el correo de recuperación a {}: {}", to, e.getMessage());
        }
    }
}

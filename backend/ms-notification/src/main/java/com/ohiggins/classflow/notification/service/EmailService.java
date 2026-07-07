package com.ohiggins.classflow.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Gestiona el envio de correos electronicos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    /**
     * Remitente verificado configurado explicitamente (recomendado, ej. Brevo exige uno).
     * Puede llegar vacio (no solo ausente) si una variable de entorno del contenedor
     * se declara sin valor, por lo que no se puede confiar en el default de Spring
     * ({@code ${a:${b}}} solo aplica cuando la clave esta ausente, no cuando esta vacia).
     */
    @Value("${app.mail.from:}")
    private String configuredFromEmail;

    @Value("${spring.mail.username:}")
    private String smtpUsername;

    /**
     * Envia un correo electronico simple.
     *
     * @param to destinatario.
     * @param subject asunto.
     * @param body contenido.
     * @return true si el envio fue exitoso.
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(resolveFromEmail());
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to: {}", to);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Usa app.mail.from si tiene contenido real; si no, cae a spring.mail.username.
     * Chequea contenido (no solo ausencia) porque una variable de entorno de Docker
     * declarada sin valor llega como cadena vacia, no como propiedad ausente.
     */
    private String resolveFromEmail() {
        return StringUtils.hasText(configuredFromEmail) ? configuredFromEmail : smtpUsername;
    }
}
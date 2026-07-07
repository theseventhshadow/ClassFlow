package com.ohiggins.classflow.auth.service;

import com.ohiggins.classflow.auth.client.NotificationClient;
import com.ohiggins.classflow.auth.dto.ForgotPasswordRequestDTO;
import com.ohiggins.classflow.auth.dto.ResetPasswordRequestDTO;
import com.ohiggins.classflow.auth.entity.PasswordResetToken;
import com.ohiggins.classflow.auth.entity.User;
import com.ohiggins.classflow.auth.repository.PasswordResetTokenRepository;
import com.ohiggins.classflow.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

/**
 * Genera y valida los tokens de un solo uso del flujo de recuperacion de contrasena.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationClient notificationClient;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${password-reset.expiration-minutes:30}")
    private long expirationMinutes;

    /**
     * Genera un token de recuperacion y envia el correo correspondiente si la cuenta existe.
     * Responde igual exista o no el correo, para no revelar que cuentas estan registradas.
     * No esta envuelto en una transaccion: el envio de correo hace una llamada de red
     * potencialmente lenta, y no debe mantener abierta una conexion de base de datos mientras dura.
     *
     * @param request correo del usuario que solicita el restablecimiento.
     */
    public void requestReset(ForgotPasswordRequestDTO request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isEmpty()) {
            log.info("Solicitud de restablecimiento para un correo no registrado: {}", request.getEmail());
            return;
        }

        User user = userOpt.get();
        tokenRepository.deleteByUserId(user.getId());

        String rawToken = generateRawToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUserId(user.getId());
        resetToken.setTokenHash(hash(rawToken));
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(expirationMinutes));
        resetToken.setUsed(false);
        tokenRepository.save(resetToken);

        String resetLink = trimTrailingSlash(frontendUrl) + "/reset-password?token=" + rawToken;
        notificationClient.sendEmail(
                user.getEmail(),
                "Recuperación de contraseña - ClassFlow",
                "Hola " + user.getFirstName() + ",\n\n"
                        + "Solicitaste restablecer tu contraseña. Este enlace vence en "
                        + expirationMinutes + " minutos:\n" + resetLink + "\n\n"
                        + "Si no fuiste tú, ignora este correo."
        );
    }

    /**
     * Valida el token recibido y, si es correcto, actualiza la contrasena del usuario asociado.
     * El consumo del token (marcarlo usado) es una actualizacion condicional atomica, para que
     * dos solicitudes con el mismo token que lleguen casi al mismo tiempo no puedan ambas tener exito.
     *
     * @param request token entregado por correo y nueva contrasena.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO request) {
        PasswordResetToken resetToken = tokenRepository.findByTokenHash(hash(request.getToken()))
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        if (tokenRepository.markUsedIfNotUsed(resetToken.getId()) == 0) {
            throw new RuntimeException("Token inválido o expirado");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new RuntimeException("Token inválido o expirado"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private String trimTrailingSlash(String url) {
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String generateRawToken() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hash(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return Base64.getEncoder().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }
}

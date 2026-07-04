package com.ohiggins.classflow.notification.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import com.ohiggins.classflow.notification.enums.NotificationType;

/**
 * Datos de entrada para crear o enviar una alerta.
 */
@Data
public class AlertRequestDTO {
    @NotNull
    private Long userId;

    @NotNull
    private NotificationType type;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;

    private String userEmail;
}
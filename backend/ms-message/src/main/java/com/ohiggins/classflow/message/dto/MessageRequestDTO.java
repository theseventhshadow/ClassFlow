package com.ohiggins.classflow.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Datos de entrada para enviar un mensaje.
 */
@Data
public class MessageRequestDTO {
    @NotNull
    private Long senderId;
    
    @NotNull
    private Long receiverId;
    
    @NotBlank
    private String subject;
    
    @NotBlank
    private String body;
}

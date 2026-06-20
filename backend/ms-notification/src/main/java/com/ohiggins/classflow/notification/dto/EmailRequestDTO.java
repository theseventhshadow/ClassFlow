package com.ohiggins.classflow.notification.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailRequestDTO {
    @NotBlank @Email
    private String to;

    @NotBlank
    private String subject;

    @NotBlank
    private String body;
}
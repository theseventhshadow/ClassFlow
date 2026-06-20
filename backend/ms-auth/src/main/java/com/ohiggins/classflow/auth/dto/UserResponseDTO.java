package com.ohiggins.classflow.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Datos del usuario")
public class UserResponseDTO {

    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Schema(description = "Nombre(s) del usuario", example = "María")
    private String firstName;

    @Schema(description = "Apellido(s) del usuario", example = "Gómez")
    private String lastName;

    @Schema(description = "Nombre completo", example = "María Gómez")
    private String fullName;

    @Schema(description = "RUT del usuario", example = "12.345.678-9")
    private String idNumber;

    @Schema(description = "Correo electrónico", example = "maria.gomez@example.com")
    private String email;

    @Schema(description = "Rol en el sistema", example = "TEACHER", allowableValues = {"ADMINISTRATOR","TEACHER","GUARDIAN","STUDENT"})
    private String role;

    @Schema(description = "Curso o asignatura asignada (docentes/estudiantes)", example = "Matemáticas")
    private String course;

    @Schema(description = "Estado de la cuenta", example = "true")
    private Boolean active;
}

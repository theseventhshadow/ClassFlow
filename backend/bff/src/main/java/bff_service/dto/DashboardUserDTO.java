package bff_service.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class DashboardUserDTO {
    private Long id;
    private String nombre;
    private String email;
    private String rol;                 // ESTUDIANTE, DOCENTE, ADMINISTRADOR
    private String estado;              // activo, inactivo, pendiente
    private LocalDateTime lastAccess;
}
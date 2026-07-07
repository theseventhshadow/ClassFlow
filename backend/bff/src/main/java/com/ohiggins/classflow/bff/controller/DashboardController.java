package com.ohiggins.classflow.bff.controller;

import com.ohiggins.classflow.bff.dto.DashboardResponse;
import com.ohiggins.classflow.bff.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * Expone los endpoints agregados del dashboard para el frontend.
 */
@RestController
@RequestMapping("/api/bff")
@Tag(name = "Dashboard BFF", description = "Endpoints agregados que consolidan datos de todos los microservicios para el frontend")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Obtiene el dashboard completo para un usuario.
     *
     * @param userId identificador del usuario.
     * @return respuesta HTTP con el dashboard consolidado.
     */
    @GetMapping("/dashboard/{userId}")
    @Operation(summary = "Dashboard completo del usuario", description = "Retorna cursos, notas, asistencias, mensajes, anotaciones y notificaciones del usuario")
    @ApiResponse(responseCode = "200", description = "Datos del dashboard")
    public Mono<ResponseEntity<DashboardResponse>> getDashboard(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorization) {
        return dashboardService.getDashboard(userId, authorization)
                .map(ResponseEntity::ok);
    }
}

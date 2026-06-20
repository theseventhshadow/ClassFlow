package com.ohiggins.classflow.bff.controller;

import com.ohiggins.classflow.bff.dto.AdminAlertDTO;
import com.ohiggins.classflow.bff.dto.CourseAttendanceDTO;
import com.ohiggins.classflow.bff.dto.DashboardResponse;
import com.ohiggins.classflow.bff.dto.DashboardStatsDTO;
import com.ohiggins.classflow.bff.dto.DashboardUserDTO;
import com.ohiggins.classflow.bff.dto.RecentActivityDTO;
import com.ohiggins.classflow.bff.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/bff")
@CrossOrigin(origins = "*")
@Tag(name = "Dashboard BFF", description = "Endpoints agregados que consolidan datos de todos los microservicios para el frontend")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/{userId}")
    @Operation(summary = "Dashboard completo del usuario", description = "Retorna cursos, notas, asistencias, mensajes, anotaciones y notificaciones del usuario")
    @ApiResponse(responseCode = "200", description = "Datos del dashboard")
    public Mono<ResponseEntity<DashboardResponse>> getDashboard(@PathVariable Long userId) {
        return dashboardService.getDashboard(userId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/stats/{userId}")
    @Operation(summary = "Estadísticas del dashboard", description = "Resumen numérico: total alumnos, cursos activos, asistencia y alertas")
    @ApiResponse(responseCode = "200", description = "Estadísticas calculadas")
    public Mono<ResponseEntity<DashboardStatsDTO>> getStats(@PathVariable Long userId) {
        return dashboardService.getStats(userId)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/users/recent")
    @Operation(summary = "Usuarios recientes", description = "Últimos N usuarios con actividad en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios recientes")
    public Mono<ResponseEntity<List<DashboardUserDTO>>> getRecentUsers(
            @RequestParam(defaultValue = "5") Long limit) {
        return dashboardService.getRecentUsers(limit)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/attendance/by-course")
    @Operation(summary = "Asistencia por curso", description = "Porcentaje de asistencia agrupado por curso")
    @ApiResponse(responseCode = "200", description = "Asistencia por curso")
    public Mono<ResponseEntity<List<CourseAttendanceDTO>>> getCourseAttendance() {
        return dashboardService.getCourseAttendance()
                .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/activity")
    @Operation(summary = "Actividad reciente", description = "Últimas N actividades del sistema")
    @ApiResponse(responseCode = "200", description = "Lista de actividades recientes")
    public Mono<ResponseEntity<List<RecentActivityDTO>>> getRecentActivity(
            @RequestParam(defaultValue = "4") Long limit) {
        return dashboardService.getRecentActivity(limit)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/alerts")
    @Operation(summary = "Alertas activas", description = "Alertas pendientes de gestión en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de alertas")
    public Mono<ResponseEntity<List<AdminAlertDTO>>> getAlerts() {
        return dashboardService.getAlerts()
                .map(ResponseEntity::ok);
    }
}

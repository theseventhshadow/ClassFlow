package bff_service.controller;

import bff_service.dto.AdminAlertDTO;
import bff_service.dto.CourseAttendanceDTO;
import bff_service.dto.DashboardResponse;
import bff_service.dto.DashboardStatsDTO;
import bff_service.dto.DashboardUserDTO;
import bff_service.dto.RecentActivityDTO;
import bff_service.service.DashboardService;

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
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard/{userId}")
    public Mono<ResponseEntity<DashboardResponse>> getDashboard(@PathVariable Long userId) {
        return dashboardService.getDashboard(userId)
                .map(ResponseEntity::ok);
    }

    // En DashboardController.java, agregar:

    @GetMapping("/dashboard/stats/{userId}")
    public Mono<ResponseEntity<DashboardStatsDTO>> getStats(@PathVariable Long userId) {
        return dashboardService.getStats(userId)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/users/recent")
    public Mono<ResponseEntity<List<DashboardUserDTO>>> getRecentUsers(
            @RequestParam(defaultValue = "5") Long limit) {
        return dashboardService.getRecentUsers(limit)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/attendance/by-course")
    public Mono<ResponseEntity<List<CourseAttendanceDTO>>> getCourseAttendance() {
        return dashboardService.getCourseAttendance()
            .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/activity")
    public Mono<ResponseEntity<List<RecentActivityDTO>>> getRecentActivity(
            @RequestParam(defaultValue = "4") Long limit) {
        return dashboardService.getRecentActivity(limit)
            .map(ResponseEntity::ok);
    }

    @GetMapping("/dashboard/alerts")
    public Mono<ResponseEntity<List<AdminAlertDTO>>> getAlerts() {
        return dashboardService.getAlerts()
            .map(ResponseEntity::ok);
    }

}

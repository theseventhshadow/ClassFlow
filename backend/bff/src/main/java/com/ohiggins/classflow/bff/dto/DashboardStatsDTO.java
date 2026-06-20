package com.ohiggins.classflow.bff.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsDTO {
    private Long totalStudents;
    private Long activeCourses;
    private Double attendanceRate;      // 0-100
    private Long activeAlerts;
    private Double studentsTrend;       // % cambio
    private Double attendanceTrend;     // % cambio
} 
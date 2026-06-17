package bff_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CourseAttendanceDTO {
    private Long courseId;
    private String courseName;
    private Double attendanceRate;      // 0-100
}
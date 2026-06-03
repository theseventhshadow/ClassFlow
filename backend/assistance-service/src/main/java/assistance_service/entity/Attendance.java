package assistance_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "attendances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long studentId;
    private Long courseId;
    private LocalDate date;
    private Boolean present;
    private String justification;
}

package academic_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "grades")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long studentId;
    private Double score;
    private String observations;
    
    @ManyToOne
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;
}
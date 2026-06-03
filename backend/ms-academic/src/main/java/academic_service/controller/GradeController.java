package academic_service.controller;

import academic_service.dto.GradeDTO;
import academic_service.service.GradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {
    
    private final GradeService gradeService;
    
    @GetMapping
    public ResponseEntity<List<GradeDTO>> getAll() {
        return ResponseEntity.ok(gradeService.findAll());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDTO>> getByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.findByStudentId(studentId));
    }
    
    @GetMapping("/evaluation/{evaluationId}")
    public ResponseEntity<List<GradeDTO>> getByEvaluationId(@PathVariable Long evaluationId) {
        return ResponseEntity.ok(gradeService.findByEvaluationId(evaluationId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<GradeDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(gradeService.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<GradeDTO> create(@Valid @RequestBody GradeDTO dto) {
        return new ResponseEntity<>(gradeService.create(dto), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<GradeDTO> update(@PathVariable Long id, @Valid @RequestBody GradeDTO dto) {
        return ResponseEntity.ok(gradeService.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
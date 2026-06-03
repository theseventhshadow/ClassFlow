package academic_service.controller;

import academic_service.dto.EvaluationDTO;
import academic_service.service.EvaluationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@RequiredArgsConstructor
public class EvaluationController {
    
    private final EvaluationService evaluationService;
    
    @GetMapping
    public ResponseEntity<List<EvaluationDTO>> getAll() {
        return ResponseEntity.ok(evaluationService.findAll());
    }
    
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<EvaluationDTO>> getBySubjectId(@PathVariable Long subjectId) {
        return ResponseEntity.ok(evaluationService.findBySubjectId(subjectId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EvaluationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(evaluationService.findById(id));
    }
    
    @PostMapping
    public ResponseEntity<EvaluationDTO> create(@Valid @RequestBody EvaluationDTO dto) {
        return new ResponseEntity<>(evaluationService.create(dto), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EvaluationDTO> update(@PathVariable Long id, @Valid @RequestBody EvaluationDTO dto) {
        return ResponseEntity.ok(evaluationService.update(id, dto));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        evaluationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
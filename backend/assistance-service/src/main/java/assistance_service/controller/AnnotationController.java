package assistance_service.controller;

import assistance_service.dto.AnnotationDTO;
import assistance_service.dto.AnnotationRequestDTO;
import assistance_service.service.AnnotationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/annotations")
@RequiredArgsConstructor
public class AnnotationController {
    
    private final AnnotationService annotationService;
    
    @GetMapping
    public ResponseEntity<List<AnnotationDTO>> getAll() {
        return ResponseEntity.ok(annotationService.findAll());
    }
    
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<AnnotationDTO>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(annotationService.findByStudentId(studentId));
    }
    
    @GetMapping("/student/{studentId}/type/{type}")
    public ResponseEntity<List<AnnotationDTO>> getByStudentAndType(@PathVariable Long studentId, @PathVariable String type) {
        return ResponseEntity.ok(annotationService.findByStudentIdAndType(studentId, type));
    }
    
    @PostMapping
    public ResponseEntity<AnnotationDTO> create(@Valid @RequestBody AnnotationRequestDTO request) {
        return new ResponseEntity<>(annotationService.create(request), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        annotationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

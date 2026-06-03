package message_service.controller;

import message_service.dto.AnnouncementDTO;
import message_service.dto.AnnouncementRequestDTO;
import message_service.service.AnnouncementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {
    
    private final AnnouncementService announcementService;
    
    @GetMapping
    public ResponseEntity<List<AnnouncementDTO>> getAll() {
        return ResponseEntity.ok(announcementService.findAll());
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<AnnouncementDTO>> getActive() {
        return ResponseEntity.ok(announcementService.findActive());
    }
    
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AnnouncementDTO>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(announcementService.findByCourseId(courseId));
    }
    
    @PostMapping
    public ResponseEntity<AnnouncementDTO> create(@Valid @RequestBody AnnouncementRequestDTO request) {
        return new ResponseEntity<>(announcementService.create(request), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

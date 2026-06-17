package message_service.service;

import message_service.dto.AnnouncementDTO;
import message_service.dto.AnnouncementRequestDTO;
import message_service.entity.Announcement;
import message_service.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    
    public List<AnnouncementDTO> findAll() {
        return announcementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AnnouncementDTO> findActive() {
        return announcementRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<AnnouncementDTO> findByCourseId(Long courseId) {
        return announcementRepository.findByCourseIdOrCourseIdIsNull(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public AnnouncementDTO create(AnnouncementRequestDTO request) {
        Announcement announcement = new Announcement();
        announcement.setTitle(request.getTitle());
        announcement.setContent(request.getContent());
        announcement.setCourseId(request.getCourseId());
        announcement.setSenderId(request.getSenderId());
        announcement.setPublishedAt(LocalDateTime.now());
        announcement.setActive(true);
        
        Announcement saved = announcementRepository.save(announcement);
        return convertToDTO(saved);
    }
    
    public void delete(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Announcement not found"));
        announcement.setActive(false);
        announcementRepository.save(announcement);
    }
    
    private AnnouncementDTO convertToDTO(Announcement announcement) {
        return AnnouncementDTO.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .courseId(announcement.getCourseId())
                .senderId(announcement.getSenderId())
                .publishedAt(announcement.getPublishedAt())
                .active(announcement.getActive())
                .build();
    }
}

package com.ohiggins.classflow.message.service;

import com.ohiggins.classflow.message.dto.AnnouncementDTO;
import com.ohiggins.classflow.message.dto.AnnouncementRequestDTO;
import com.ohiggins.classflow.message.entity.Announcement;
import com.ohiggins.classflow.message.repository.AnnouncementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene la logica de negocio para anuncios.
 */
@Service
@RequiredArgsConstructor
public class AnnouncementService {
    
    private final AnnouncementRepository announcementRepository;
    
    /**
     * Obtiene todos los anuncios.
     *
     * @return lista de anuncios convertidos a DTO.
     */
    public List<AnnouncementDTO> findAll() {
        return announcementRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los anuncios activos.
     *
     * @return lista de anuncios convertidos a DTO.
     */
    public List<AnnouncementDTO> findActive() {
        return announcementRepository.findByActiveTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los anuncios de un curso.
     *
     * @param courseId identificador del curso.
     * @return lista de anuncios convertidos a DTO.
     */
    public List<AnnouncementDTO> findByCourseId(Long courseId) {
        return announcementRepository.findByCourseIdOrCourseIdIsNull(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Crea un nuevo anuncio.
     *
     * @param request datos del anuncio a crear.
     * @return anuncio guardado convertido a DTO.
     */
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
    
    /**
     * Desactiva un anuncio existente.
     *
     * @param id identificador del anuncio.
     */
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

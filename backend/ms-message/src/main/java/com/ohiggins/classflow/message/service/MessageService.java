package com.ohiggins.classflow.message.service;

import com.ohiggins.classflow.message.dto.MessageDTO;
import com.ohiggins.classflow.message.dto.MessageRequestDTO;
import com.ohiggins.classflow.message.entity.Message;
import com.ohiggins.classflow.message.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contiene la logica de negocio para mensajes privados.
 */
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    
    /**
     * Obtiene todos los mensajes.
     *
     * @return lista de mensajes convertidos a DTO.
     */
    public List<MessageDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los mensajes recibidos por un usuario.
     *
     * @param receiverId identificador del receptor.
     * @return lista de mensajes convertidos a DTO.
     */
    public List<MessageDTO> findByReceiverId(Long receiverId) {
        return messageRepository.findByReceiverId(receiverId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los mensajes enviados por un usuario.
     *
     * @param senderId identificador del emisor.
     * @return lista de mensajes convertidos a DTO.
     */
    public List<MessageDTO> findBySenderId(Long senderId) {
        return messageRepository.findBySenderId(senderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene los mensajes no leidos de un receptor.
     *
     * @param receiverId identificador del receptor.
     * @return lista de mensajes convertidos a DTO.
     */
    public List<MessageDTO> findUnreadByReceiverId(Long receiverId) {
        return messageRepository.findByReceiverIdAndReadFalse(receiverId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Envia un mensaje nuevo.
     *
     * @param request datos del mensaje a enviar.
     * @return mensaje guardado convertido a DTO.
     */
    public MessageDTO send(MessageRequestDTO request) {
        Message message = new Message();
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setSubject(request.getSubject());
        message.setBody(request.getBody());
        message.setRead(false);
        message.setSentAt(LocalDateTime.now());
        
        Message saved = messageRepository.save(message);
        return convertToDTO(saved);
    }
    
    /**
     * Marca un mensaje como leido.
     *
     * @param id identificador del mensaje.
     * @return mensaje actualizado convertido a DTO.
     */
    public MessageDTO markAsRead(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        Message updated = messageRepository.save(message);
        return convertToDTO(updated);
    }
    
    /**
     * Elimina un mensaje por su identificador.
     *
     * @param id identificador del mensaje.
     */
    public void delete(Long id) {
        messageRepository.deleteById(id);
    }
    
    private MessageDTO convertToDTO(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .senderId(message.getSenderId())
                .receiverId(message.getReceiverId())
                .subject(message.getSubject())
                .body(message.getBody())
                .read(message.getRead())
                .sentAt(message.getSentAt())
                .build();
    }
}

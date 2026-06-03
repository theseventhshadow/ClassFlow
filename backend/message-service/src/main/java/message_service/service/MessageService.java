package message_service.service;

import message_service.dto.MessageDTO;
import message_service.dto.MessageRequestDTO;
import message_service.entity.Message;
import message_service.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    
    public List<MessageDTO> findAll() {
        return messageRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MessageDTO> findByReceiverId(Long receiverId) {
        return messageRepository.findByReceiverId(receiverId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MessageDTO> findBySenderId(Long senderId) {
        return messageRepository.findBySenderId(senderId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<MessageDTO> findUnreadByReceiverId(Long receiverId) {
        return messageRepository.findByReceiverIdAndReadFalse(receiverId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
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
    
    public MessageDTO markAsRead(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        Message updated = messageRepository.save(message);
        return convertToDTO(updated);
    }
    
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

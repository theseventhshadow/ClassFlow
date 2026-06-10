package message_service.controller;

import message_service.dto.MessageDTO;
import message_service.dto.MessageRequestDTO;
import message_service.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @GetMapping
    public ResponseEntity<List<MessageDTO>> getAll() {
        return ResponseEntity.ok(messageService.findAll());
    }
    
    @GetMapping("/receiver/{receiverId}")
    public ResponseEntity<List<MessageDTO>> getByReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(messageService.findByReceiverId(receiverId));
    }
    
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<List<MessageDTO>> getBySender(@PathVariable Long senderId) {
        return ResponseEntity.ok(messageService.findBySenderId(senderId));
    }
    
    @GetMapping("/receiver/{receiverId}/unread")
    public ResponseEntity<List<MessageDTO>> getUnreadByReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(messageService.findUnreadByReceiverId(receiverId));
    }
    
    @PostMapping("/send")
    public ResponseEntity<MessageDTO> send(@Valid @RequestBody MessageRequestDTO request) {
        return new ResponseEntity<>(messageService.send(request), HttpStatus.CREATED);
    }
    
    @PutMapping("/{id}/read")
    public ResponseEntity<MessageDTO> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.markAsRead(id));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

package message_service.controller;

import message_service.dto.MessageDTO;
import message_service.dto.MessageRequestDTO;
import message_service.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Mensajes", description = "Mensajes privados entre usuarios del sistema")
public class MessageController {

    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Listar todos los mensajes")
    @ApiResponse(responseCode = "200", description = "Lista de mensajes")
    public ResponseEntity<List<MessageDTO>> getAll() {
        return ResponseEntity.ok(messageService.findAll());
    }

    @GetMapping("/receiver/{receiverId}")
    @Operation(summary = "Mensajes recibidos por usuario")
    @ApiResponse(responseCode = "200", description = "Mensajes del receptor")
    public ResponseEntity<List<MessageDTO>> getByReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(messageService.findByReceiverId(receiverId));
    }

    @GetMapping("/sender/{senderId}")
    @Operation(summary = "Mensajes enviados por usuario")
    @ApiResponse(responseCode = "200", description = "Mensajes del emisor")
    public ResponseEntity<List<MessageDTO>> getBySender(@PathVariable Long senderId) {
        return ResponseEntity.ok(messageService.findBySenderId(senderId));
    }

    @GetMapping("/receiver/{receiverId}/unread")
    @Operation(summary = "Mensajes no leídos por receptor")
    @ApiResponse(responseCode = "200", description = "Mensajes no leídos")
    public ResponseEntity<List<MessageDTO>> getUnreadByReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(messageService.findUnreadByReceiverId(receiverId));
    }

    @PostMapping("/send")
    @Operation(summary = "Enviar mensaje")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mensaje enviado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<MessageDTO> send(@Valid @RequestBody MessageRequestDTO request) {
        return new ResponseEntity<>(messageService.send(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Marcar mensaje como leído")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje marcado como leído"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<MessageDTO> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.markAsRead(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar mensaje")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Mensaje eliminado"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

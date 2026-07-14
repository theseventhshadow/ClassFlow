package com.ohiggins.classflow.message.controller;

import com.ohiggins.classflow.message.dto.MessageDTO;
import com.ohiggins.classflow.message.dto.MessageRequestDTO;
import com.ohiggins.classflow.message.service.MessageService;
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

/**
 * Expone los endpoints REST para mensajes privados entre usuarios.
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Mensajes", description = "Mensajes privados entre usuarios del sistema")
public class MessageController {

    private final MessageService messageService;

    /**
     * Obtiene el listado completo de mensajes.
     *
     * @return respuesta HTTP con la lista de mensajes.
     */
    @GetMapping
    @Operation(summary = "Listar todos los mensajes")
    @ApiResponse(responseCode = "200", description = "Lista de mensajes")
    public ResponseEntity<List<MessageDTO>> getAll() {
        return ResponseEntity.ok(messageService.findAll());
    }

    /**
     * Obtiene los mensajes recibidos por un usuario.
     *
     * @param receiverId identificador del receptor.
     * @return respuesta HTTP con los mensajes recibidos.
     */
    @GetMapping("/receiver/{receiverId}")
    @Operation(summary = "Mensajes recibidos por usuario")
    @ApiResponse(responseCode = "200", description = "Mensajes del receptor")
    public ResponseEntity<List<MessageDTO>> getByReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(messageService.findByReceiverId(receiverId));
    }

    /**
     * Obtiene los mensajes enviados por un usuario.
     *
     * @param senderId identificador del emisor.
     * @return respuesta HTTP con los mensajes enviados.
     */
    @GetMapping("/sender/{senderId}")
    @Operation(summary = "Mensajes enviados por usuario")
    @ApiResponse(responseCode = "200", description = "Mensajes del emisor")
    public ResponseEntity<List<MessageDTO>> getBySender(@PathVariable Long senderId) {
        return ResponseEntity.ok(messageService.findBySenderId(senderId));
    }

    /**
     * Obtiene los mensajes no leidos de un receptor.
     *
     * @param receiverId identificador del receptor.
     * @return respuesta HTTP con los mensajes no leidos.
     */
    @GetMapping("/receiver/{receiverId}/unread")
    @Operation(summary = "Mensajes no leídos por receptor")
    @ApiResponse(responseCode = "200", description = "Mensajes no leídos")
    public ResponseEntity<List<MessageDTO>> getUnreadByReceiver(@PathVariable Long receiverId) {
        return ResponseEntity.ok(messageService.findUnreadByReceiverId(receiverId));
    }

    /**
     * Envía un mensaje nuevo.
     *
     * @param request datos del mensaje a enviar.
     * @return respuesta HTTP con el mensaje creado.
     */
    @PostMapping("/send")
    @Operation(summary = "Enviar mensaje")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Mensaje enviado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<MessageDTO> send(@Valid @RequestBody MessageRequestDTO request) {
        return new ResponseEntity<>(messageService.send(request), HttpStatus.CREATED);
    }

    /**
     * Marca un mensaje como leido.
     *
     * @param id identificador del mensaje.
     * @return respuesta HTTP con el mensaje actualizado.
     */
    @PutMapping("/{id}/read")
    @Operation(summary = "Marcar mensaje como leído")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Mensaje marcado como leído"),
        @ApiResponse(responseCode = "404", description = "Mensaje no encontrado")
    })
    public ResponseEntity<MessageDTO> markAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(messageService.markAsRead(id));
    }

    /**
     * Elimina un mensaje por su identificador.
     *
     * @param id identificador del mensaje.
     * @return respuesta HTTP sin contenido.
     */
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

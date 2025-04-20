package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.message.Message;
import br.com.leofonseca.bigchatbr.domain.message.MessageRequestDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponseDTO> createMessage(
            @RequestBody @Valid MessageRequestDTO requestDTO
    ){
        try {
            MessageResponseDTO createdMessage = messageService.createMessage(requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageResponseDTO> getMessageById(
            @PathVariable Long id
    ) {
        try {
            MessageResponseDTO message = messageService.findById(id);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MessageResponseDTO>> getMessages(
            @RequestParam(required = false) String conversationId,
            @RequestParam(required = false) String senderId,
            @RequestParam(required = false) String recipientId,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String status) {

        try {
            List<MessageResponseDTO> messages = messageService.listByFilters(
                    conversationId,
                    senderId,
                    recipientId,
                    priority,
                    status
            );

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("{ID}/status")
    public ResponseEntity<MessageResponseDTO> getMessageStatus(
            @PathVariable Long ID
    ) {
        try {
            MessageResponseDTO message = messageService.findById(ID);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}

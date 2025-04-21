package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.message.Message;
import br.com.leofonseca.bigchatbr.domain.message.MessageRequestDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.domain.user.User;
import br.com.leofonseca.bigchatbr.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Tag(name = "Mensagens", description = "Endpoints para operações de mensagens")
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    @Operation(summary = "Criar mensagem", description = "Cria uma nova mensagem.")
    @ApiResponse(responseCode = "201", description = "Mensagem criada com sucesso")
    public ResponseEntity<MessageResponseDTO> createMessage(
            @AuthenticationPrincipal User loggedUser,
            @RequestBody @Valid MessageRequestDTO requestDTO
    ){
        try {
            String documentId = loggedUser.getDocumentId();
            MessageResponseDTO createdMessage = messageService.createMessage(documentId, requestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter mensagem por ID", description = "Retorna a mensagem com o ID especificado")
    @ApiResponse(responseCode = "200", description = "Mensagem encontrada")
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
    @Operation(summary = "Listar mensagens", description = "Retorna uma lista de mensagens de acordo com os filtros informados")
    @ApiResponse(responseCode = "200", description = "Lista de mensagens obtida com sucesso")
    public ResponseEntity<List<MessageResponseDTO>> getMessages(
            @RequestParam(required = false) Long conversationId,
            @RequestParam(required = false) Long senderId,
            @RequestParam(required = false) Long recipientId,
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
    @Operation(summary = "Obter status da mensagem", description = "Retorna o status da mensagem com o ID informado")
    @ApiResponse(responseCode = "200", description = "Status retornado")
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

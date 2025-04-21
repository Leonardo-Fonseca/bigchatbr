package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import br.com.leofonseca.bigchatbr.domain.conversation.ConversationResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.domain.user.User;
import br.com.leofonseca.bigchatbr.service.ConversationService;
import br.com.leofonseca.bigchatbr.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Conversas", description = "Endpoints para operações de conversas.")
public class ConversationController {
    private final ConversationService conversationService;
    private final MessageService messageService;

    @GetMapping
    @Operation(summary = "Obter lista de conversas", description = "Retorna a lista de conversas. Admins veem todas, demais usuários vêem suas conversas.")
    public ResponseEntity<List<ConversationResponseDTO>> getConversations(
            @AuthenticationPrincipal User loggedUser
    ) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            List<ConversationResponseDTO> list;

            if (isAdmin) {
                list = conversationService.list();
            } else {
                list = conversationService.listForUser(loggedUser.getDocumentId());
            }
            return ResponseEntity.ok().body(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obter conversa por ID", description = "Retorna a conversa correspondente ao ID informado.")
    public ResponseEntity<ConversationResponseDTO> getConversationById(
            @PathVariable Long id
    ) {
        try {
            ConversationResponseDTO conversation = conversationService.findConversationById(id);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/message")
    @Operation(summary = "Obter mensagens da conversa", description = "Retorna a lista de mensagens da conversa especificada pelo ID.")
    public ResponseEntity<List<MessageResponseDTO>> getMessagesFromConversation(
            @PathVariable Long id,
            @AuthenticationPrincipal User loggedUser
    ) {
        try {
            List<MessageResponseDTO> messages = messageService.listMessagesFromConversation(id, loggedUser.getId());
            return ResponseEntity.ok().body(messages);
        } catch (Exception e) {
            log.error("Erro ao listar mensagens da conversa id={}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
}

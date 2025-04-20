package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import br.com.leofonseca.bigchatbr.domain.conversation.ConversationResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.service.ConversationService;
import br.com.leofonseca.bigchatbr.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@Slf4j
public class ConversationController {
    private final ConversationService conversationService;
    private final MessageService messageService;

    @GetMapping
    public ResponseEntity<List<ConversationResponseDTO>> getConversations() {
        try {
            List<ConversationResponseDTO> list = conversationService.list();
            return ResponseEntity.ok().body(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/{id}")
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
    public ResponseEntity<List<MessageResponseDTO>> getMessagesFromConversation(
            @PathVariable Long id
    ) {
        try {
            Conversation conversation = conversationService.findById(id);
            List<MessageResponseDTO> messages = messageService.listByFilters(
                    id,
                    null,
                    null,
                    null,
                    null
            );
            return ResponseEntity.ok().body(messages);
        } catch (Exception e) {
            log.error("Erro ao listar mensagens da conversa id={}", id, e);
            return ResponseEntity.badRequest().build();
        }
    }
}

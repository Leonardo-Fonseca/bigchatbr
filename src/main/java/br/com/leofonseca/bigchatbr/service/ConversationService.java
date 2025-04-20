package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import br.com.leofonseca.bigchatbr.domain.message.Message;
import br.com.leofonseca.bigchatbr.domain.message.MessageRequestDTO;
import br.com.leofonseca.bigchatbr.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository convesationRepository;

    public Conversation createFromMessage(MessageRequestDTO messageRequestDTO) {
        Conversation newConversation = new Conversation(
                messageRequestDTO.senderId(),
                messageRequestDTO.recipientId()
        );
        return convesationRepository.save(newConversation);
    }

    public void updateFromMessage(Message message) {
        Conversation conversation = message.getConversationId();
        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageDate(message.getSentAt());
    }

    public Conversation findById(Long id) {
        return convesationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    public List<Conversation> list() {
        return convesationRepository.findAll();
    }

}

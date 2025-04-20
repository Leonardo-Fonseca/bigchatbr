package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import br.com.leofonseca.bigchatbr.domain.conversation.ConversationResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.Message;
import br.com.leofonseca.bigchatbr.domain.message.MessageRequestDTO;
import br.com.leofonseca.bigchatbr.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ConversationService {
    private final ConversationRepository convesationRepository;
    private final ClientService clientService;

    public Conversation createFromMessage(MessageRequestDTO messageRequestDTO) {
        Client sender = clientService.findClientById(messageRequestDTO.senderId());
        Client recipient = clientService.findClientById(messageRequestDTO.recipientId());
        Conversation newConversation = new Conversation(
                sender,
                recipient,
                recipient.getName(),
                0
        );
        return convesationRepository.save(newConversation);
    }

    public void updateFromMessage(Message message) {
        Conversation conversation = message.getConversationId();
        Integer updateUnreadCount = conversation.getUnreadCount();

        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageDate(message.getSentAt());
        this.updateUnreadCount(conversation, updateUnreadCount + 1);
    }

    public void updateUnreadCount(Conversation conversation, Integer value) {
        var updateUnreadCount = value;
        conversation.setUnreadCount(updateUnreadCount);
    }

    public ConversationResponseDTO findConversationById(Long id) {
        return new ConversationResponseDTO(this.findById(id));
    }
    public Conversation findById(Long id) {
        return convesationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    public List<ConversationResponseDTO> list() {
        return this.convesationRepository.findAll().stream().map(ConversationResponseDTO::new).toList();
    }
}

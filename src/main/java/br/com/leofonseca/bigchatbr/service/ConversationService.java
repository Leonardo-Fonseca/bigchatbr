package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import br.com.leofonseca.bigchatbr.domain.conversation.ConversationResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.Message;
import br.com.leofonseca.bigchatbr.domain.message.MessageRequestDTO;
import br.com.leofonseca.bigchatbr.repository.ConversationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ConversationService {
    private final ConversationRepository convesationRepository;
    private final ClientService clientService;

    public Conversation createFromMessage(MessageRequestDTO messageRequestDTO, String senderDocumentId) {
        log.debug("Criando conversa a partir da mensagem: senderDocumentId={}", senderDocumentId);

        Client sender = clientService.findClientByDocumentId(senderDocumentId);
        Client recipient = clientService.findClientById(messageRequestDTO.recipientId());

        Conversation newConversation = new Conversation(
                sender,
                recipient,
                recipient.getName(),
                0
        );

        Conversation savedConversation = convesationRepository.save(newConversation);

        log.info("Conversa criada com ID: {}", savedConversation.getId());

        return savedConversation;
    }

    public void updateFromMessage(Message message) {
        log.debug("Atualizando conversa a partir da mensagem com ID: {}", message.getId());

        Conversation conversation = message.getConversation();
        Integer updateUnreadCount = conversation.getUnreadCount();

        conversation.setLastMessageContent(message.getContent());
        conversation.setLastMessageDate(message.getSentAt());

        this.updateUnreadCount(conversation, updateUnreadCount + 1);

        log.info("Conversa atualizada para mensagem ID: {}", message.getId());
    }

    public void updateUnreadCount(Conversation conversation, Integer value) {
        conversation.setUnreadCount(value);

        log.debug("Contador de mensagens não lidas atualizado para: {}", value);
    }

    public ConversationResponseDTO findConversationById(Long id) {
        return new ConversationResponseDTO(this.findById(id));
    }
    public Conversation findById(Long id) {
        log.debug("Buscando conversa com ID: {}", id);

        return convesationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado"));
    }

    public List<ConversationResponseDTO> list() {
        log.debug("Listando todas as conversas");

        return this.convesationRepository.findAll().stream().map(ConversationResponseDTO::new).toList();
    }

    public List<ConversationResponseDTO> listForUser(String documentId) {
        log.debug("Listando conversas para usuário com documentId: {}", documentId);

        var client = clientService.findClientByDocumentId(documentId);

        return listByClient(client.getId());
    }

    public List<ConversationResponseDTO> listByClient(Long clientId) {
        log.debug("Listando conversas para cliente com ID: {}", clientId);

        List<Conversation> conversations = convesationRepository.findByClient_IdOrRecipient_Id(clientId, clientId);

        return conversations.stream().map(ConversationResponseDTO::new).toList();
    }
}

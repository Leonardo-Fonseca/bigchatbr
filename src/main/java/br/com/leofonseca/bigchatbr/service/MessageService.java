package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.client.ClientResponseDTO;
import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import br.com.leofonseca.bigchatbr.domain.message.Message;
import br.com.leofonseca.bigchatbr.domain.message.MessageRequestDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.PriorityAndCost;
import br.com.leofonseca.bigchatbr.repository.MessageRepository;
import br.com.leofonseca.bigchatbr.specification.MessageSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final ClientService clientService;

    public MessageResponseDTO createMessage(MessageRequestDTO requestDTO) {
        Message newMessage = new Message();
        if (requestDTO.conversationId() == null){
            Conversation newConversation = conversationService.createFromMessage(requestDTO);
            newMessage.setConversationId(newConversation);
        } else {
            Conversation conversation = conversationService.findById(requestDTO.conversationId());

            newMessage.setConversationId(conversation);
        }

        Client sender = clientService.findClientById(requestDTO.senderId());
        Client recipient = clientService.findClientById(requestDTO.recipientId());
        String priority = PriorityAndCost.valueOf(requestDTO.priority()).name();
        BigDecimal cost = PriorityAndCost.valueOf(priority).getCost();

        newMessage.setSenderId(sender);
        newMessage.setRecipientId(recipient);
        newMessage.setContent(requestDTO.content());
        newMessage.setPriority(priority);
        newMessage.setCost(cost);
        newMessage.setStatus(requestDTO.status());

        Message savedMessage = messageRepository.save(newMessage);

        conversationService.updateFromMessage(savedMessage);

        return new MessageResponseDTO(savedMessage);
    }

    public MessageResponseDTO findById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensagem não encontrada"));
        return new MessageResponseDTO(message);
    }

    public List<MessageResponseDTO> listByFilters(
            Long conversationId,
            Long senderId,
            Long recipientId,
            String priority,
            String status
    ) {
        Specification<Message> filtros = Specification
                .where(MessageSpecification.hasConversationId(conversationId))
                .and(MessageSpecification.hasSenderId(senderId))
                .and(MessageSpecification.hasRecipientId(recipientId))
                .and(MessageSpecification.hasPriority(priority))
                .and(MessageSpecification.hasStatus(status));

        return messageRepository.findAll(filtros).stream().map(MessageResponseDTO::new).toList();
    }
}

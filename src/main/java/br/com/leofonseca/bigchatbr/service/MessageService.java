package br.com.leofonseca.bigchatbr.service;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import br.com.leofonseca.bigchatbr.domain.message.*;
import br.com.leofonseca.bigchatbr.repository.MessageRepository;
import br.com.leofonseca.bigchatbr.service.queue.MessageQueue;
import br.com.leofonseca.bigchatbr.specification.MessageSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final ConversationService conversationService;
    private final ClientService clientService;
    private final MessageQueue queue;

    @Autowired
    private ApplicationEventPublisher publisher;

    public MessageResponseDTO createMessage(String senderDocumentId, MessageRequestDTO requestDTO) {
        // Cria ou carrega a conversa.
        Message newMessage = new Message();
        if (requestDTO.conversationId() == null){
            Conversation newConversation = conversationService.createFromMessage(requestDTO, senderDocumentId);
            newMessage.setConversation(newConversation);
        } else {
            Conversation conversation = conversationService.findById(requestDTO.conversationId());

            newMessage.setConversation(conversation);
        }

        // Carrega o cliente remetente e destinatário
        Client sender = clientService.findClientByDocumentId(senderDocumentId);
        Client recipient = clientService.findClientById(requestDTO.recipientId());
        // Carrega os dados de prioridade e custo
        String priority = PriorityAndCost.valueOf(requestDTO.priority()).name();
        BigDecimal cost = PriorityAndCost.valueOf(priority).getCost();

        // Carrega o plano.
        String plan = sender.getPlanType();

        if ("PREPAID".equalsIgnoreCase(plan)) {
            // valida saldo
            if (sender.getBalance().compareTo(cost) < 0) {
                throw new IllegalArgumentException("Saldo insuficiente para enviar mensagem");
            }
            // debita do balance
            sender.setBalance(sender.getBalance().subtract(cost));
        } else if ("POSTPAID".equalsIgnoreCase(plan)) {
            // valida limite (balance armazena o limite restante)
            if (sender.getBalance().compareTo(cost) < 0) {
                throw new IllegalArgumentException("Limite mensal excedido");
            }
            // atualiza limite restante
            sender.setBalance(sender.getBalance().subtract(cost));
            // incrementa fatura
            sender.setInvoice(sender.getInvoice().add(cost));
        } else {
            throw new IllegalStateException("Plano desconhecido: " + plan);
        }

        // Salvar os dados alterados do cliente remetente
        clientService.saveClient(sender);

        // Termina de montar a mensagem.
        newMessage.setSender(sender);
        newMessage.setRecipient(recipient);
        newMessage.setContent(requestDTO.content());
        newMessage.setPriority(priority);
        newMessage.setCost(cost);
        newMessage.setStatus(MessageStatus.QUEUED);

        Message savedMessage = messageRepository.save(newMessage);

        boolean urgent = "URGENT".equalsIgnoreCase(savedMessage.getPriority());
        publisher.publishEvent(new MessageCreatedEvent(savedMessage.getId(), urgent));

        //Atualiza a contagem de mensagens nao lida.
        conversationService.updateFromMessage(savedMessage);

        // Retorna o dto de resposta com os dados da mensagem criada.
        return new MessageResponseDTO(savedMessage);
    }

    public MessageResponseDTO findById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensagem não encontrada"));
        return new MessageResponseDTO(message);
    }

    public Message findMessageById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Mensagem não encontrada"));
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
    public List<MessageResponseDTO> listMessagesFromConversation(Long id){
        List<Message> messages = messageRepository.findAll();

        if (!messages.isEmpty()) {
            // TODO: ATUALIZAR PARA MARCAR SOMENTE AS MENSAGENS DO USUARIO LOGADO
            // Marcaa todas as mensagens SENT como READ
            int toRead = 0;
            for (Message message : messages) {
                if (message.getStatus() == MessageStatus.SENT) {
                    // atualiza no banco
                    updateStatus(message.getId(), MessageStatus.READ);
                    // reflete na DTO que vamos retornar
                    message.setStatus(MessageStatus.READ);

                    toRead++;
                }
            }

            // Atualizar contador de unread na conversa
            Conversation conversation = conversationService.findById(id);
            int currentUnread = conversation.getUnreadCount();
            int newUnread = Math.max(0, currentUnread - toRead);
            conversationService.updateUnreadCount(conversation, newUnread);
        }

        return messages.stream().map(MessageResponseDTO::new).toList();
    }

    public void updateStatus(Long id, MessageStatus status) {
        Message msg = this.findMessageById(id);
        msg.setStatus(status);
        messageRepository.save(msg);
    }

    public void sendMessage(Long id) throws Exception {
        // Aguarda 5 segundos para simular o envio da mensagem.
        // Para dar tempo de acumuluar mensagens na fila.
        Thread.sleep(2500);
    }
}

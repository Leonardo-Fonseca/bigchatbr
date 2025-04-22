package br.com.leofonseca.bigchatbr.domain.message;

import br.com.leofonseca.bigchatbr.enums.MessageStatus;

import java.math.BigDecimal;

public record MessageResponseDTO (
        Long id,
        Long conversationId,
        Long senderId,
        Long recipientId,
        String content,
        String priority,
        MessageStatus status,
        BigDecimal cost,
        String sentAt
){
    public MessageResponseDTO(Message message) {
        this(
                message.getId(),
                message.getConversation().getId(),
                message.getSender().getId(),
                message.getRecipient().getId(),
                message.getContent(),
                message.getPriority(),
                message.getStatus(),
                message.getCost(),
                message.getSentAt().toString()
        );
    }
}

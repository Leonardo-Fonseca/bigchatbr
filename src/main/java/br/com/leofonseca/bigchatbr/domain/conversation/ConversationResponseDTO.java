package br.com.leofonseca.bigchatbr.domain.conversation;

import java.time.LocalDateTime;

public record ConversationResponseDTO (
        Long id,
        Long clientId,
        Long recipientId,
        String nameRecipient,
        String lastMessageContent,
        LocalDateTime lastMessageDate,
        Integer unreadCount
){
    public ConversationResponseDTO(Conversation conversation) {
        this(
                conversation.getId(),
                conversation.getClient().getId(),
                conversation.getRecipient().getId(),
                conversation.getRecipientName(),
                conversation.getLastMessageContent(),
                conversation.getLastMessageDate(),
                conversation.getUnreadCount()
        );
    }
}
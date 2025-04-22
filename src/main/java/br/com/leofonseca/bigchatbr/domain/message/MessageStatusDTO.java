package br.com.leofonseca.bigchatbr.domain.message;

import br.com.leofonseca.bigchatbr.enums.MessageStatus;

public record MessageStatusDTO(
        MessageStatus status
) {
    public MessageStatusDTO(Message message) {
        this(
                message.getStatus()
        );
    }
}

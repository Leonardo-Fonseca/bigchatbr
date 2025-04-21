package br.com.leofonseca.bigchatbr.domain.message;

public record MessageStatusDTO(
        MessageStatus status
) {
    public MessageStatusDTO(Message message) {
        this(
                message.getStatus()
        );
    }
}

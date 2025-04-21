package br.com.leofonseca.bigchatbr.domain.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class MessageCreatedEvent {
    private final Long messageId;
    private final boolean urgent;
}
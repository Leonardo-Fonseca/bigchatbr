package br.com.leofonseca.bigchatbr.queue;

import br.com.leofonseca.bigchatbr.domain.message.MessageCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MessageQueueListener {

    @Autowired
    private MessageQueue queue;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(MessageCreatedEvent event) {
        queue.enqueue(event.getMessageId(), event.isUrgent());
    }
}
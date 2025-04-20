package br.com.leofonseca.bigchatbr.specification;

import br.com.leofonseca.bigchatbr.domain.message.Message;
import org.springframework.data.jpa.domain.Specification;

public class MessageSpecification {
    public static Specification<Message> hasConversationId(String conversationId) {
        return (root, query, builder) ->
                conversationId == null ? builder.conjunction() :
                        builder.equal(root.get("conversationId"), conversationId);
    }
    public static Specification<Message> hasSenderId(String senderId) {
        return (root, query, builder) ->
                senderId == null ? builder.conjunction() :
                        builder.equal(root.get("senderId"), senderId);
    }
    public static Specification<Message> hasRecipientId(String recipientId) {
        return (root, query, builder) ->
                recipientId == null ? builder.conjunction() :
                        builder.equal(root.get("recipientId"), recipientId);
    }
    public static Specification<Message> hasPriority(String priority) {
        return (root, query, builder) ->
                priority == null ? builder.conjunction() :
                        builder.equal(root.get("priority"), priority);
    }
    public static Specification<Message> hasStatus(String status) {
        return (root, query, builder) ->
                status == null ? builder.conjunction() :
                        builder.equal(root.get("status"), status);
    }
}

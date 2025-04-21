package br.com.leofonseca.bigchatbr.repository;

import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    List<Conversation> findByClient_IdOrRecipient_Id(Long clientId, Long recipientId);
}


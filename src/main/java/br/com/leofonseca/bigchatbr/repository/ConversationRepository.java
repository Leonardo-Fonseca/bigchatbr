package br.com.leofonseca.bigchatbr.repository;

import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {
}

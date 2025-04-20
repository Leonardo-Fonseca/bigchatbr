package br.com.leofonseca.bigchatbr.domain.message;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.conversation.Conversation;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "messages")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "conversation_id", referencedColumnName = "id")
    private Conversation conversationId;
    @ManyToOne
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    private Client senderId;
    @ManyToOne
    @JoinColumn(name = "recipient_id", referencedColumnName = "id")
    private Client recipientId;
    private String content;
    private String priority;
    private String status;
    private BigDecimal cost;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime sentAt;
}

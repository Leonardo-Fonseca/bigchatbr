package br.com.leofonseca.bigchatbr.domain.conversation;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "conversations")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String clientId;
    private String recipientId;
    @Column(nullable = true)
    private String lastMessageContent;
    @Column(nullable = true)
    private LocalDateTime lastMessageDate;

    public Conversation(Long senderId,
                        Long recipientId
    ) {
        this.clientId = senderId.toString();
        this.recipientId = recipientId.toString();
    }
}

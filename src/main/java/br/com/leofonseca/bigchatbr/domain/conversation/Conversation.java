package br.com.leofonseca.bigchatbr.domain.conversation;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "id")
    private Client clientId;
    @ManyToOne
    @JoinColumn(name = "recepient_id", referencedColumnName = "id")
    private Client recipientId;
    private String recipientName;
    @Column(nullable = true)
    private String lastMessageContent;
    @Column(nullable = true)
    private LocalDateTime lastMessageDate;
    private Integer unreadCount;

    public Conversation(Client sender,
                        Client recipient,
                        String recipientName,
                        Integer unreadCount
    ) {
        this.clientId = sender;
        this.recipientId = recipient;
        this.recipientName = recipientName;
        this.unreadCount = unreadCount;
    }

}

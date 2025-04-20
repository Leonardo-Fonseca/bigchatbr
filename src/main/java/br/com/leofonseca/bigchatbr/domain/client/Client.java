package br.com.leofonseca.bigchatbr.domain.client;

import br.com.leofonseca.bigchatbr.domain.enums.DocumentType;
import br.com.leofonseca.bigchatbr.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "clients")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
public class Client {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String documentId;
    private String documentType;
    private String planType;
    private BigDecimal balance;
    private BigDecimal invoice = BigDecimal.ZERO;
    private Boolean isActive;
    @OneToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public void setUser(User user) {
        this.user = user;
    }
}

package br.com.leofonseca.bigchatbr.flow;

import br.com.leofonseca.bigchatbr.domain.client.Client;
import br.com.leofonseca.bigchatbr.domain.conversation.ConversationResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageRequestDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageStatus;
import br.com.leofonseca.bigchatbr.repository.ClientRepository;
import br.com.leofonseca.bigchatbr.repository.ConversationRepository;
import br.com.leofonseca.bigchatbr.repository.MessageRepository;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class MessageFlowIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private ConversationRepository convRepo;
    @Autowired
    private MessageRepository msgRepo;

    private Long senderId, recipientId;

    @BeforeEach
    void cleanupAndSetup() {
        // limpa todas as tabelas envolvidas
        msgRepo.deleteAll();
        convRepo.deleteAll();

        // cria dois clientes de teste
        Client sender = new Client();
        sender.setName("Remetente");
        sender.setPlanType("PREPAID");
        sender.setBalance(BigDecimal.valueOf(50));
        sender.setInvoice(BigDecimal.ZERO);
        clientRepo.save(sender);
        senderId = sender.getId();

        Client recipient = new Client();
        recipient.setName("Destinatário");
        recipient.setPlanType("PREPAID");
        recipient.setBalance(BigDecimal.ZERO);
        recipient.setInvoice(BigDecimal.ZERO);
        clientRepo.save(recipient);
        recipientId = recipient.getId();
    }

    @Test
    void fullMessageFlow_endToEnd() {

        // 1) Criação da mensagem → status inicial QUEUED
        MessageRequestDTO req = new MessageRequestDTO(
                null,
                senderId,
                recipientId,
                "Fluxo completo",
                "NORMAL"
        );

        // cria a mensagem e obtém o ID
        Long msgId = webTestClient.post().uri("/messages")
                .bodyValue(req)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(MessageResponseDTO.class)
                .returnResult()
                .getResponseBody()
                .id();

        // confere QUEUED imediatamente
        webTestClient.get().uri("/messages/{id}", msgId)
                .exchange()
                .expectBody()
                .jsonPath("$.status").isEqualTo(MessageStatus.QUEUED.name());

        // 2) Aguarda processamento até SENT (timeout 10s)
        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(500))
                .untilAsserted(() ->
                        webTestClient.get().uri("/messages/{id}", msgId)
                                .exchange()
                                .expectBody()
                                .jsonPath("$.status").isEqualTo(MessageStatus.SENT.name())
                );

        // 3) Listagem via conversation → GET /conversations/{id}/message
        // isso deve marcar a mensagem como READ e zerar unreadCount
        // primeiro, obtém o ID da conversa retornado ao criar a mensagem:
        ConversationResponseDTO conv = webTestClient.get()
                .uri("/conversations")
                .exchange()
                .expectBodyList(ConversationResponseDTO.class)
                .returnResult()
                .getResponseBody()
                .stream()
                .filter(c -> c.recipientId().equals(recipientId))
                .findFirst()
                .orElseThrow();
        Long convId = conv.id();

        List<MessageResponseDTO> msgs = webTestClient.get()
                .uri("/conversations/{id}/message", convId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageResponseDTO.class)
                .returnResult()
                .getResponseBody();

        // deve ter exatamente 1 mensagem, agora em READ
        assertEquals(1, msgs.size());
        assertEquals(MessageStatus.READ, msgs.get(0).status());

        // 4) Verifica que unreadCount da conversa foi zerado
        webTestClient.get().uri("/conversations/{id}", convId)
                .exchange()
                .expectBody()
                .jsonPath("$.unreadCount").isEqualTo(0);
    }
}

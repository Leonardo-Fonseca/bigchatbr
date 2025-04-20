package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.conversation.ConversationResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class ConversationControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void listConversations_success() {
        webTestClient.get().uri("/conversations")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ConversationResponseDTO.class);
    }

    @Test
    void getConversationById_success() {
        webTestClient.get().uri("/conversations/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void getConversationById_notFound() {
        webTestClient.get().uri("/conversations/{id}", 9999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getMessagesFromConversation_success() {
        webTestClient.get().uri("/conversations/{id}/message", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageResponseDTO.class);
    }

    @Test
    void getMessagesFromConversation_badRequest_onInvalidId() {
        webTestClient.get().uri("/conversations/{id}/message", "abc")
                .exchange()
                .expectStatus().isBadRequest();
    }
}

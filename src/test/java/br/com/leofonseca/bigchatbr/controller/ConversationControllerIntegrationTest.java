package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.conversation.ConversationResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.domain.user.AuthenticationDTO;
import br.com.leofonseca.bigchatbr.domain.user.LoginResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class ConversationControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    private String token;

    @BeforeEach
    void setup() {
        // Longing como cliente para obter o token
        // Endpoints de Conversas estao disponiveis para todos os usuarios.
        var loginDto = new AuthenticationDTO("11122233344", "leo");
        var loginResponse = webTestClient.post().uri("/auth/login")
                .contentType(APPLICATION_JSON)
                .bodyValue(loginDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponseDTO.class)
                .returnResult()
                .getResponseBody();

        this.token = loginResponse.token();

        this.webTestClient = webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + this.token)
                .build();
    }


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

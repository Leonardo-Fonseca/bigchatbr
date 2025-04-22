package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import br.com.leofonseca.bigchatbr.domain.message.MessageStatus;
import br.com.leofonseca.bigchatbr.domain.user.AuthenticationDTO;
import br.com.leofonseca.bigchatbr.domain.user.LoginResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class MessageControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;
    private String token;

    @BeforeEach
    void setup() {
        // Login como usuario comum
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
    void createMessage_success() throws IOException {
        /** {
                "conversationId": 1,
                "recipientId": 2,
                "content": "Mensagem de teste automatizada",
                "priority": "URGENT",
                "status": "SENT"
        }*/
        String valid = Files.readString(Path.of("src/test/resources/test-data/valid-message.json"));
        // Testa a criaçao de mensagem
        webTestClient.post().uri("/messages")
                .contentType(APPLICATION_JSON)
                .bodyValue(valid)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.content").isEqualTo("Mensagem de teste automatizada")
                .jsonPath("$.priority").isEqualTo("URGENT");

        /**
         *  Segundo login agora como ADMIN, pois para verificar o saldo do cliente após o envio da mensagem.
         *  e nessecessario ser ADMIN ja que a /clientes so pode ser acessada por ADMIN
         */
        var loginDtoAdmin = new AuthenticationDTO("10708787908", "leo");
        var loginResponseAdmin = webTestClient.post().uri("/auth/login")
                .contentType(APPLICATION_JSON)
                .bodyValue(loginDtoAdmin)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponseDTO.class)
                .returnResult()
                .getResponseBody();

        this.token = loginResponseAdmin.token();

        this.webTestClient = webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer " + this.token)
                .build();

        /**
         *  Apos o login e verificado o saldo do cliente para conferir se houve alteraçao pelo envio da mensagem.
         */
        webTestClient.get().uri("/clientes/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("Jose Souza")
                .jsonPath("$.balance").isEqualTo(99.50)
                .jsonPath("$.invoice").isEqualTo(0);
    }

    @Test
    void createMessage_badRequest_onInvalidPayload() throws IOException {
        String invalid = Files.readString(Path.of("src/test/resources/test-data/invalid-message.json"));
        webTestClient.post().uri("/messages")
                .contentType(APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getMessageById_success() {
        webTestClient.get().uri("/messages/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1);
    }

    @Test
    void getMessageById_notFound() {
        webTestClient.get().uri("/messages/{id}", 9999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void listMessages_success_withFilter() {
        webTestClient.get().uri(uri -> uri.path("/messages")
                        .queryParam("senderId", 1)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MessageResponseDTO.class);
    }

    @Test
    void listMessages_badRequest_onInvalidFilter() {
        webTestClient.get().uri("/messages?senderId=abc")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getMessageStatus_success() {
        webTestClient.get().uri("/messages/{id}/status", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(MessageStatus.SENT);
    }

    @Test
    void getMessageStatus_notFound() {
        webTestClient.get().uri("/messages/{id}/status", 9999)
                .exchange()
                .expectStatus().isNotFound();
    }
}

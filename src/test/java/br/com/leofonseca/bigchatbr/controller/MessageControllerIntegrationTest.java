package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.message.MessageResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class MessageControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createMessage_success() throws IOException {
        String valid = Files.readString(Path.of("src/test/resources/test-data/valid-message.json"));
        webTestClient.post().uri("/messages")
                .contentType(APPLICATION_JSON)
                .bodyValue(valid)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.content").isEqualTo("Mensagem de teste automatizada")
                .jsonPath("$.priority").isEqualTo("URGENT");
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
                .jsonPath("$.status").isEqualTo("SENT");
    }

    @Test
    void getMessageStatus_notFound() {
        webTestClient.get().uri("/messages/{id}/status", 9999)
                .exchange()
                .expectStatus().isNotFound();
    }
}

package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.client.ClientResponseDTO;
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
public class ClientControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createClient_success() throws IOException {
        String valid = Files.readString(Path.of("src/test/resources/test-data/valid-client.json"));
        webTestClient.post().uri("/clientes")
                .contentType(APPLICATION_JSON)
                .bodyValue(valid)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Teste Client")
                .jsonPath("$.documentId").isEqualTo("99988877711")
                .jsonPath("$.documentType").isEqualTo("CPF")
                .jsonPath("$.planType").isEqualTo("PREPAID")
                .jsonPath("$.balance").isEqualTo(100.0)
                .jsonPath("$.invoice").isEqualTo(0)
                .jsonPath("$.isActive").isEqualTo(true);
    }

    @Test
    void createClient_badRequest_onInvalidPayload() throws IOException {
        String invalid = Files.readString(Path.of("src/test/resources/test-data/invalid-client.json"));
        webTestClient.post().uri("/clientes")
                .contentType(APPLICATION_JSON)
                .bodyValue(invalid)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateClient_success() {
        String update = """
      {"name":"Maria","balance":200.0}
      """;
        webTestClient.put().uri("/clientes/{id}", 1)
                .contentType(APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Maria")
                .jsonPath("$.balance").isEqualTo(200.0);
    }

    @Test
    void updateClient_notFound() {
        String update = "{}";
        webTestClient.put().uri("/clientes/{id}", 9999)
                .contentType(APPLICATION_JSON)
                .bodyValue(update)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void listClients_success() {
        webTestClient.get().uri("/clientes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientResponseDTO.class);
    }

    @Test
    void getClientById_success() {
        webTestClient.get().uri("/clientes/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Maria");
    }

    @Test
    void getClientById_notFound() {
        webTestClient.get().uri("/clientes/{id}", 9999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getClientBalance_success() {
        webTestClient.get().uri("/clientes/{id}/balance", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.balance").isEqualTo(200);
    }

    @Test
    void getClientBalance_failure() {
        webTestClient.get().uri("/clientes/{id}/balance", 999)
                .exchange()
                .expectStatus().isNotFound();
    }

}

package br.com.leofonseca.bigchatbr.controller;

import br.com.leofonseca.bigchatbr.domain.client.ClientCreateRequestDTO;
import br.com.leofonseca.bigchatbr.domain.client.ClientResponseDTO;
import br.com.leofonseca.bigchatbr.domain.client.PlanType;
import br.com.leofonseca.bigchatbr.domain.enums.DocumentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class ClientControllerIntegrationTest {
    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testCreateClientSuccess() {
        var client = new ClientCreateRequestDTO(
                "12345678901",
                "CPF",
                "prepaid",
                "teste",
                BigDecimal.valueOf(100.00),
                Boolean.TRUE
        );
        webTestClient.post()
                .uri("/clientes")
                .bodyValue(client)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.documentId").isEqualTo("12345678901")
                .jsonPath("$.documentType").isEqualTo(DocumentType.CPF)
                .jsonPath("$.planType").isEqualTo(PlanType.PREPAID)
                .jsonPath("$.balance").isEqualTo(100.00)
                .jsonPath("$.isActive").isEqualTo(Boolean.TRUE);
    }

    @Test
    void testCreateClientWithInvalidDocumentType() {
        var client = new ClientCreateRequestDTO(
                "12345678901",
                "INVALID_TYPE",
                "prepaid",
                "teste",
                BigDecimal.valueOf(100.00),
                Boolean.TRUE
        );
        webTestClient.post()
                .uri("/clientes")
                .bodyValue(client)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateClientWithInvalidPlanType() {
        var client = new ClientCreateRequestDTO(
                "12345678901",
                "CPF",
                "INVALID_PLAN",
                "teste",
                BigDecimal.valueOf(100.00),
                Boolean.TRUE
        );
        webTestClient.post()
                .uri("/clientes")
                .bodyValue(client)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateClientWithAllNullValues() {
        // Fazer um for each para cada campo
        var client = new ClientCreateRequestDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );
        webTestClient.post()
                .uri("/clientes")
                .bodyValue(client)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateClientWithAllEmptyValues() {
        // Fazer um for each para cada campo
        var client = new ClientCreateRequestDTO(
                "",
                "",
                "",
                "",
                BigDecimal.valueOf(0.00),
                Boolean.FALSE
        );
        webTestClient.post()
                .uri("/clientes")
                .bodyValue(client)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateClientWithInvalidDocumentId() {
        var client = new ClientCreateRequestDTO(
                "12345678901234567890",
                "CPF",
                "prepaid",
                "teste",
                BigDecimal.valueOf(100.00),
                Boolean.TRUE
        );
        webTestClient.post()
                .uri("/clientes")
                .bodyValue(client)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testListClients() {
        webTestClient.get()
                .uri("/clientes")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ClientResponseDTO.class);
    }

    @Test
    void testUpdateClientSuccess() {
        var client = new ClientCreateRequestDTO(
                "12345678901",
                "CPF",
                "prepaid",
                "teste",
                BigDecimal.valueOf(100.00),
                Boolean.TRUE
        );
        ClientResponseDTO clientCreated = webTestClient.post()
                .uri("/clientes")
                .bodyValue(client)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponseDTO.class)
                .returnResult()
                .getResponseBody();

        var updatedClient = new ClientCreateRequestDTO(
                "12345678901",
                "CPF",
                "postpaid",
                "teste",
                BigDecimal.valueOf(200.00),
                Boolean.FALSE
        );
        webTestClient.put()
                .uri("/clientes/" + clientCreated.id().toString())
                .bodyValue(updatedClient)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ClientResponseDTO.class)
                .consumeWith(response -> {
                    var responseBody = response.getResponseBody();
                    assertNotNull(responseBody, "o body não pode ser nulo");
                    assertEquals("12345678901", responseBody.documentId());
                    assertEquals("CPF", responseBody.documentType());
                    assertEquals("POSTPAID", responseBody.planType());
                    assertEquals(0, responseBody.balance().compareTo(BigDecimal.valueOf(200.0)),
                            "o balance deveria ser 200.0");
                    assertFalse(responseBody.isActive(), "isActive deveria ser false");
                });
    }

    @Test
    void testUpdateClientFailure() {
        var client = new ClientCreateRequestDTO(
                "12345678901",
                "CPF",
                "prepaid",
                "teste",
                BigDecimal.valueOf(100.00),
                Boolean.TRUE
        );
        ClientResponseDTO clientCreated = webTestClient.post()
                .uri("/clientes")
                .bodyValue(client)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(ClientResponseDTO.class)
                .returnResult()
                .getResponseBody();

        var updatedClient = new ClientCreateRequestDTO(
                "12345678901",
                "INVALID_TYPE",
                "postpaid",
                "teste",
                BigDecimal.valueOf(200.00),
                Boolean.FALSE
        );
        webTestClient.put()
                .uri("/clientes/" + clientCreated.id().toString())
                .bodyValue(updatedClient)
                .exchange()
                .expectStatus().isBadRequest();
    }
}

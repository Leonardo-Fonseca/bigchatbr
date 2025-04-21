package br.com.leofonseca.bigchatbr.domain.user;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
        @NotBlank
        String documentId,
        @NotBlank
        String password
) {
}

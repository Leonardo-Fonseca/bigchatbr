package br.com.leofonseca.bigchatbr.domain.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessageRequestDTO(
        Long conversationId,
        @NotNull
        Long senderId,
        @NotNull
        Long recipientId,
        @NotBlank
        String content,
        @NotBlank
        String priority
) {
}

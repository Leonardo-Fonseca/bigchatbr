package br.com.leofonseca.bigchatbr.domain.client;

import br.com.leofonseca.bigchatbr.validation.ValidDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@ValidDocument
public record ClientCreateRequestDTO(
        @NotBlank
        String documentId,
        @NotBlank
        String documentType,
        @NotBlank
        @Pattern(regexp = "(?i)^(prepaid|postpaid)$", message = "planType deve ser prepaid ou postpaid")
        String planType,
        @NotBlank
        String password,
        @NotNull
        BigDecimal balance,
        @NotNull
        Boolean isActive
) {
}

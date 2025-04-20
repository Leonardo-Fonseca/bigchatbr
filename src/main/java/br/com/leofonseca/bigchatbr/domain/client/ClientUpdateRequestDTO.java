package br.com.leofonseca.bigchatbr.domain.client;

import br.com.leofonseca.bigchatbr.validation.AtLeastOneNotNull;
import br.com.leofonseca.bigchatbr.validation.ValidDocument;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

@AtLeastOneNotNull
@ValidDocument
public record ClientUpdateRequestDTO(
        String documentId,
        String documentType,
        @Pattern(regexp = "(?i)^(prepaid|postpaid)$", message = "planType deve ser prepaid ou postpaid")
        String planType,
        String password,
        BigDecimal balance,
        Boolean isActive
) {
}

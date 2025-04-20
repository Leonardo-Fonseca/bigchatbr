package br.com.leofonseca.bigchatbr.domain.client;

import java.math.BigDecimal;

public record ClientResponseDTO(
        Long id,
        String documentId,
        String documentType,
        String planType,
        BigDecimal balance,
        Boolean isActive
) {
    public ClientResponseDTO(Client c) {
        this(
                c.getId(),
                c.getDocumentId(),
                c.getDocumentType().toString(),
                c.getPlanType().toString(),
                c.getBalance(),
                c.getIsActive()
        );
    }
}

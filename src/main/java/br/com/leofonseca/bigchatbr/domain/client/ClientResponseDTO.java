package br.com.leofonseca.bigchatbr.domain.client;

import java.math.BigDecimal;

public record ClientResponseDTO(
        Long id,
        String name,
        String documentId,
        String documentType,
        String planType,
        BigDecimal balance,
        BigDecimal invoice,
        Boolean isActive
) {
    public ClientResponseDTO(Client c) {
        this(
                c.getId(),
                c.getName(),
                c.getDocumentId(),
                c.getDocumentType().toString(),
                c.getPlanType().toString(),
                c.getBalance(),
                c.getInvoice(),
                c.getIsActive()
        );
    }
}

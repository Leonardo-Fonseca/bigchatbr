package br.com.leofonseca.bigchatbr.domain.client;

import java.math.BigDecimal;

public record ClientLimitedResponseDTO(
        Long id,
        String name,
        Boolean isActive
) {
    public ClientLimitedResponseDTO(Client c) {
        this(
                c.getId(),
                c.getName(),
                c.getIsActive()
        );
    }
}

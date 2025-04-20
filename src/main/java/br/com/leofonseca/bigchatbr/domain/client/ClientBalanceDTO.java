package br.com.leofonseca.bigchatbr.domain.client;

import java.math.BigDecimal;

public record ClientBalanceDTO(
        BigDecimal balance
) {
    public ClientBalanceDTO(Client client) {
        this(
                client.getBalance()
        );
    }
}

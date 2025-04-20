package br.com.leofonseca.bigchatbr.domain.message;

import java.math.BigDecimal;

public enum PriorityAndCost {
    NORMAL(new BigDecimal("0.25")),
    URGENT(new BigDecimal("0.50"));

    private final BigDecimal cost;

    PriorityAndCost(BigDecimal cost) {
        this.cost = cost;
    }

    public BigDecimal getCost() {
        return cost;
    }
}

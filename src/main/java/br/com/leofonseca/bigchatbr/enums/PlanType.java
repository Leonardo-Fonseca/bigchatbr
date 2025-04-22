package br.com.leofonseca.bigchatbr.enums;

public enum PlanType {
    PREPAID("PREPAID"),
    POSTPAID("POSTPAID");

    private final String planType;

    PlanType(String planType) {
        this.planType = planType;
    }

    public String getPlanType() {
        return planType;
    }
}

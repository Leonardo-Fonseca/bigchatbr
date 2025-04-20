package br.com.leofonseca.bigchatbr.domain.enums;

public enum DocumentType {
    CPF("CPF"),
    CNPJ("CNPJ");

    private final String documentType;

    DocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentType() {
        return documentType;
    }
}

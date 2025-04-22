package br.com.leofonseca.bigchatbr.enums;

public enum UserRole {
    ADMIN("ADMIN"),
    CLIENTE("CLIENTE");

    private final String role;


    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}

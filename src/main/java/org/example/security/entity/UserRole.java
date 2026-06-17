package org.example.security.entity;

public enum UserRole {

    ROLE_USER("Пользователь"),
    ROLE_ADMIN("Администратор");

    private String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

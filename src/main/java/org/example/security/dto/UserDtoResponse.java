package org.example.security.dto;

import org.example.security.entity.UserRole;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class UserDtoResponse {

    private UUID id;

    private String username;

    private String email;

    private Integer age;

    private Instant createdAt;

    private Set<UserRole> userRoleSet;

    public UserDtoResponse() {
    }

    public UserDtoResponse(
            UUID id,
            String username,
            String email,
            Integer age,
            Instant createdAt,
            Set<UserRole> userRoleSet
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
        this.userRoleSet = userRoleSet;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Integer getAge() {
        return age;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Set<UserRole> getUserRoleSet() {
        return userRoleSet;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUserRoleSet(Set<UserRole> userRoleSet) {
        this.userRoleSet = userRoleSet;
    }
}

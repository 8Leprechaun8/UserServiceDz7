package org.example.security.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "title")
    @Enumerated(EnumType.STRING)
    private Set<UserRole> userRoleSet = new HashSet<>();

    public User() {
    }

    public User(
            UUID id,
            String username,
            String password,
            Boolean isActive,
            String email,
            Integer age,
            Instant createdAt,
            Set<UserRole> userRoleSet) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isActive = isActive;
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

    public String getPassword() {
        return password;
    }

    public Boolean getActive() {
        return isActive;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setActive(Boolean active) {
        isActive = active;
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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User user)) return false;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(password, user.password) && Objects.equals(isActive, user.isActive) && Objects.equals(email, user.email) && Objects.equals(age, user.age) && Objects.equals(createdAt, user.createdAt) && Objects.equals(userRoleSet, user.userRoleSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, isActive, email, age, createdAt, userRoleSet);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", isActive=" + isActive +
                ", email='" + email + '\'' +
                ", age=" + age +
                ", createdAt=" + createdAt +
                ", userRoleSet=" + userRoleSet +
                '}';
    }

    public static class UserBuilder {
        private UUID id;

        private String username;

        private String password;

        private Boolean isActive;

        private String email;

        private Integer age;

        private Instant createdAt;

        private Set<UserRole> userRoleSet;

        public UserBuilder id(UUID id) {
            this.id = id;
            return this;
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder isActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public UserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public UserBuilder age(Integer age) {
            this.age = age;
            return this;
        }

        public UserBuilder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public UserBuilder userRoleSet(Set<UserRole> userRoleSet) {
            this.userRoleSet = userRoleSet;
            return this;
        }

        public User build() {
            return new User(id, username, password, isActive, email, age, createdAt, userRoleSet);
        }
    }
}

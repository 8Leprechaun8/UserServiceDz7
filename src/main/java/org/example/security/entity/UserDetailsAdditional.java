package org.example.security.entity;

import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.UUID;

public interface UserDetailsAdditional extends UserDetails {

    UUID getId();

    String getMail();

    Integer getAge();

    Instant getCreatedAt();
}

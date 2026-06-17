package org.example.security.dto;

import org.example.security.entity.User;
import org.example.security.entity.UserDetailsAdditional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

public class SecurityUser implements UserDetails, UserDetailsAdditional {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getUserRoleSet().stream()
                .map(role -> new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return role.name();
                    }
                })
                .toList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getActive();
    }

    @Override
    public UUID getId() {
        return user.getId();
    }

    @Override
    public String getMail() {
        return user.getEmail();
    }

    @Override
    public Integer getAge() {
        return user.getAge();
    }

    @Override
    public Instant getCreatedAt() {
        return user.getCreatedAt();
    }
}

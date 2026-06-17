package org.example.security.mapper.impl;

import org.example.security.dto.SecurityUser;
import org.example.security.dto.UserDtoForSaving;
import org.example.security.dto.UserDtoForUpdating;
import org.example.security.dto.UserDtoResponse;
import org.example.security.entity.User;
import org.example.security.entity.UserDetailsAdditional;
import org.example.security.entity.UserRole;
import org.example.security.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDetails userDtoForSavingToUserDetails(UserDtoForSaving userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User.UserBuilder()
                .id(null)
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .isActive(true)
                .email(userDto.getEmail())
                .age(userDto.getAge())
                .createdAt(Instant.now())
                .userRoleSet(Set.of(UserRole.ROLE_USER))
                .build();
        SecurityUser securityUser = new SecurityUser(user);
        return securityUser;
    }

    @Override
    public UserDetailsAdditional userDtoForSavingToUserDetailsAdditional(UserDtoForSaving userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User.UserBuilder()
                .id(null)
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .isActive(true)
                .email(userDto.getEmail())
                .age(userDto.getAge())
                .createdAt(Instant.now())
                .userRoleSet(Set.of(UserRole.ROLE_USER))
                .build();
        SecurityUser securityUser = new SecurityUser(user);
        return securityUser;
    }

    @Override
    public UserDetailsAdditional userDtoForUpdatingToUserDetailsAdditional(UserDtoForUpdating userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User.UserBuilder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .password(userDto.getPassword())
                .isActive(true)
                .email(userDto.getEmail())
                .age(userDto.getAge())
                .createdAt(Instant.now())
                .userRoleSet(Set.of(UserRole.ROLE_USER))
                .build();
        SecurityUser securityUser = new SecurityUser(user);
        return securityUser;
    }

    @Override
    public User userDetailsToUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setActive(userDetails.isEnabled());
        user.setUsername(userDetails.getUsername());
        user.setPassword(userDetails.getPassword());
        user.setUserRoleSet(userDetails.getAuthorities().stream()
                .map(grantedAuthority ->
                        stringToUserRole(grantedAuthority.getAuthority()))
                .collect(Collectors.toSet()));
        return user;
    }

    @Override
    public User userDetailsAdditionalToUser(UserDetailsAdditional userDetailsAdditional) {
        if (userDetailsAdditional == null) {
            return null;
        }
        User user = new User();
        user.setId(userDetailsAdditional.getId());
        user.setActive(userDetailsAdditional.isEnabled());
        user.setUsername(userDetailsAdditional.getUsername());
        user.setPassword(userDetailsAdditional.getPassword());
        user.setUserRoleSet(userDetailsAdditional.getAuthorities().stream()
                .map(grantedAuthority ->
                        stringToUserRole(grantedAuthority.getAuthority()))
                .collect(Collectors.toSet()));
        user.setEmail(userDetailsAdditional.getMail());
        user.setAge(userDetailsAdditional.getAge());
        user.setCreatedAt(userDetailsAdditional.getCreatedAt());
        return user;
    }

    @Override
    public UserDtoResponse userToUserDtoResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserDtoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt(),
                user.getUserRoleSet()
        );
    }

    @Override
    public List<UserDtoResponse> userListToUserDtoResponseList(List<User> userList) {
        if (userList == null || userList.isEmpty()) {
            return List.of();
        }
        return userList.stream()
                .map(s -> userToUserDtoResponse(s))
                .toList();
    }

    private UserRole stringToUserRole(String stringUserRole) {
        switch (stringUserRole) {
            case "ROLE_USER":
                return UserRole.ROLE_USER;
            case "ROLE_ADMIN":
                return UserRole.ROLE_ADMIN;
            default:
                return UserRole.ROLE_USER;
        }
    }
}

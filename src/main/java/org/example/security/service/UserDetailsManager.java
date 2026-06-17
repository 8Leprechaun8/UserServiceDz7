package org.example.security.service;

import org.example.security.dto.UserDtoResponse;
import org.example.security.entity.UserDetailsAdditional;
import org.example.security.exception.UserFoundException;
import org.example.security.exception.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.UUID;

public interface UserDetailsManager extends UserDetailsService {

    void createUser(UserDetailsAdditional userDetailsAdditional) throws UserFoundException;

    UserDtoResponse readUser(UUID id) throws UserNotFoundException;

    void updateUser(UserDetailsAdditional userDetailsAdditional) throws UserNotFoundException;

    void deleteUser(UUID id) throws UserNotFoundException;

    List<UserDtoResponse> findAll();
}

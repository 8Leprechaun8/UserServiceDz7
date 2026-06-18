package org.example.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.example.security.dto.UserDtoForSaving;
import org.example.security.dto.UserDtoForUpdating;
import org.example.security.dto.UserDtoResponse;
import org.example.security.entity.UserDetailsAdditional;
import org.example.security.exception.UserFoundException;
import org.example.security.exception.UserNotFoundException;
import org.example.security.mapper.UserMapper;
import org.example.security.service.UserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserDetailsManager userDetailsManager;

    private final UserMapper userMapper;

    @Autowired
    public UserController(UserDetailsManager userDetailsManager, UserMapper userMapper) {
        this.userDetailsManager = userDetailsManager;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Регистрация в системе как пользователь (без аутентификации)")
    @PostMapping("/signup")
    public void signUp(@RequestBody UserDtoForSaving userDto) throws UserFoundException {
        UserDetailsAdditional userDetails = userMapper.userDtoForSavingToUserDetailsAdditional(userDto);
        userDetailsManager.createUser(userDetails);
    }

    @Operation(summary = "Чтение пользователя по id (с аутентификацией, только для ADMIN)")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/read/{id}")
    public UserDtoResponse readUser(@PathVariable(name = "id") UUID id) throws UserNotFoundException {
        return userDetailsManager.readUser(id);
    }

    @Operation(summary = "Обновление пользователя (с аутентификацией, только для ADMIN)")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/update")
    public void updateUser(@RequestBody UserDtoForUpdating userDto) throws UserNotFoundException {
        UserDetailsAdditional userDetails = userMapper.userDtoForUpdatingToUserDetailsAdditional(userDto);
        userDetailsManager.updateUser(userDetails);
    }

    @Operation(summary = "Удаление пользователя по id (с аутентификацией, только для ADMIN)")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/delete/{id}")
    public void deleteUser(@PathVariable(name = "id") UUID id) throws UserNotFoundException {
        userDetailsManager.deleteUser(id);
    }

    @Operation(summary = "Чтение всех пользователей (с аутентификацией, только для ADMIN)")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/readAll")
    public List<UserDtoResponse> readAllUsers() {
        return userDetailsManager.findAll();
    }
}

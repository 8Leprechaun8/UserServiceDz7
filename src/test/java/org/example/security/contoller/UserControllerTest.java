package org.example.security.contoller;

import org.example.security.controller.UserController;
import org.example.security.dto.SecurityUser;
import org.example.security.dto.UserDtoForSaving;
import org.example.security.dto.UserDtoForUpdating;
import org.example.security.dto.UserDtoResponse;
import org.example.security.entity.User;
import org.example.security.entity.UserDetailsAdditional;
import org.example.security.entity.UserRole;
import org.example.security.exception.UserFoundException;
import org.example.security.exception.UserNotFoundException;
import org.example.security.mapper.impl.UserMapperImpl;
import org.example.security.service.impl.UserDetailsManagerImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean(name = "userDetailsManagerImpl")
    private UserDetailsManagerImpl userDetailsManager;

    @MockitoBean(name = "userMapperImpl")
    private UserMapperImpl userMapper;

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void signUpTest_whenSuccess() throws Exception {
        UserDetailsAdditional userDetails = createUserDetailsAdditional();
        UserDtoForSaving userDtoForSaving = createUserDtoForSaving();
        when(userMapper.userDtoForSavingToUserDetailsAdditional(any(UserDtoForSaving.class))).thenReturn(userDetails);
        doNothing().when(userDetailsManager).createUser(any(UserDetailsAdditional.class));

        mvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .content("{\n" +
                                "  \"username\": \"ivanov\",\n" +
                                "  \"password\": \"123\",\n" +
                                "  \"email\": \"ivanov@mail.ru\",\n" +
                                "  \"age\": 30\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userMapper).userDtoForSavingToUserDetailsAdditional(any(UserDtoForSaving.class));
        verify(userDetailsManager).createUser(any(UserDetailsAdditional.class));
        verifyNoMoreInteractions(userMapper, userDetailsManager);
    }

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void signUpTest_whenException() throws Exception {
        UserDetailsAdditional userDetails = createUserDetailsAdditional();
        UserDtoForSaving userDtoForSaving = createUserDtoForSaving();
        when(userMapper.userDtoForSavingToUserDetailsAdditional(any(UserDtoForSaving.class))).thenReturn(userDetails);
        doThrow(new UserFoundException("В бд найден пользователь"))
                .when(userDetailsManager).createUser(any(UserDetailsAdditional.class));

        mvc.perform(post("/api/users/signup")
                        .with(csrf())
                        .content("{\n" +
                                "  \"username\": \"mokrushin\",\n" +
                                "  \"password\": \"123\",\n" +
                                "  \"email\": \"mokrushin@mail.ru\",\n" +
                                "  \"age\": 33\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"message\":\"В бд найден пользователь\"}"));

        verify(userMapper).userDtoForSavingToUserDetailsAdditional(any(UserDtoForSaving.class));
        verify(userDetailsManager).createUser(any(UserDetailsAdditional.class));
        verifyNoMoreInteractions(userMapper, userDetailsManager);
    }

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void readUserTest_whenSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        UserDtoResponse userDtoResponse = createUserDtoResponse(id);
        when(userDetailsManager.readUser(id)).thenReturn(userDtoResponse);

        mvc.perform(post("/api/users/read/" + id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{" +
                        "\"id\":\"" + userDtoResponse.getId() + "\"," +
                        "\"username\":\"" + userDtoResponse.getUsername() + "\"," +
                        "\"email\":\"" + userDtoResponse.getEmail() + "\"," +
                        "\"age\":" + userDtoResponse.getAge() + "," +
                        "\"createdAt\":\"" + userDtoResponse.getCreatedAt() + "\"," +
                        "\"userRoleSet\":[\"ROLE_USER\"]}"));

        verify(userDetailsManager).readUser(id);
        verifyNoMoreInteractions(userDetailsManager);
        verifyNoInteractions(userMapper);
    }

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void readUserTest_whenException() throws Exception {
        UUID id = UUID.randomUUID();
        UserDtoResponse userDtoResponse = createUserDtoResponse(id);
        when(userDetailsManager.readUser(id)).thenThrow(new UserNotFoundException("Пользователь не найден"));

        mvc.perform(post("/api/users/read/" + id)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"message\":\"Пользователь не найден\"}"));

        verify(userDetailsManager).readUser(id);
        verifyNoMoreInteractions(userDetailsManager);
        verifyNoInteractions(userMapper);
    }

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void updateUserTest_whenSuccess() throws Exception {
        UserDetailsAdditional userDetails = createUserDetailsAdditional();
        UserDtoForUpdating userDtoForUpdating = createUserDtoForUpdating();
        when(userMapper.userDtoForUpdatingToUserDetailsAdditional(any(UserDtoForUpdating.class))).thenReturn(userDetails);
        doNothing().when(userDetailsManager).updateUser(any(UserDetailsAdditional.class));

        mvc.perform(post("/api/users/update")
                        .with(csrf())
                        .content("{\n" +
                                "  \"\": \"" + userDtoForUpdating.getId() +"\",\n" +
                                "  \"username\": \"ivanov\",\n" +
                                "  \"password\": \"123\",\n" +
                                "  \"email\": \"ivanov@mail.ru\",\n" +
                                "  \"age\": 30\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userMapper).userDtoForUpdatingToUserDetailsAdditional(any(UserDtoForUpdating.class));
        verify(userDetailsManager).updateUser(any(UserDetailsAdditional.class));
        verifyNoMoreInteractions(userMapper, userDetailsManager);
    }

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void updateUserTest_whenException() throws Exception {
        UserDetailsAdditional userDetails = createUserDetailsAdditional();
        UserDtoForUpdating userDtoForUpdating = createUserDtoForUpdating();
        when(userMapper.userDtoForUpdatingToUserDetailsAdditional(any(UserDtoForUpdating.class)))
                .thenReturn(userDetails);
        doThrow(new UserNotFoundException("В бд не найден пользователь"))
                .when(userDetailsManager).updateUser(any(UserDetailsAdditional.class));

        mvc.perform(post("/api/users/update")
                        .with(csrf())
                        .content("{\n" +
                                "  \"\": \"" + userDtoForUpdating.getId() +"\",\n" +
                                "  \"username\": \"ivanov\",\n" +
                                "  \"password\": \"123\",\n" +
                                "  \"email\": \"ivanov@mail.ru\",\n" +
                                "  \"age\": 30\n" +
                                "}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"message\":\"В бд не найден пользователь\"}"));

        verify(userMapper).userDtoForUpdatingToUserDetailsAdditional(any(UserDtoForUpdating.class));
        verify(userDetailsManager).updateUser(any(UserDetailsAdditional.class));
        verifyNoMoreInteractions(userMapper, userDetailsManager);
    }

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void deleteUserTest_whenSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(userDetailsManager).deleteUser(id);

        mvc.perform(post("/api/users/delete/" + id)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userDetailsManager).deleteUser(id);
        verifyNoMoreInteractions(userDetailsManager);
        verifyNoInteractions(userMapper);
    }

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void deleteUserTest_whenException() throws Exception {
        UUID id = UUID.randomUUID();
        doThrow(new UserNotFoundException("В бд не найден пользователь")).when(userDetailsManager).deleteUser(id);

        mvc.perform(post("/api/users/delete/" + id)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"message\":\"В бд не найден пользователь\"}"));;

        verify(userDetailsManager).deleteUser(id);
        verifyNoMoreInteractions(userDetailsManager);
        verifyNoInteractions(userMapper);
    }

    @Test
    @WithMockUser(username = "mokrushin", password = "123")
    public void readAllTest_whenSuccess() throws Exception {
        UUID id = UUID.randomUUID();
        UserDtoResponse userDtoResponse = createUserDtoResponse(id);
        when(userDetailsManager.findAll()).thenReturn(List.of(userDtoResponse));

        mvc.perform(post("/api/users/readAll")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("[{" +
                        "\"id\":\"" + userDtoResponse.getId() + "\"," +
                        "\"username\":\"" + userDtoResponse.getUsername() + "\"," +
                        "\"email\":\"" + userDtoResponse.getEmail() + "\"," +
                        "\"age\":" + userDtoResponse.getAge() + "," +
                        "\"createdAt\":\"" + userDtoResponse.getCreatedAt() + "\"," +
                        "\"userRoleSet\":[\"ROLE_USER\"]}]"));

        verify(userDetailsManager).findAll();
        verifyNoMoreInteractions(userDetailsManager);
        verifyNoInteractions(userMapper);
    }

    private UserDetailsAdditional createUserDetailsAdditional() {
        User user = new User.UserBuilder()
                .id(UUID.randomUUID())
                .username("Ivanov")
                .password("123")
                .email("ivanov@mail.ru")
                .age(30)
                .createdAt(Instant.now())
                .userRoleSet(Set.of(UserRole.ROLE_USER))
                .build();
        return new SecurityUser(user);
    }

    private UserDtoForSaving createUserDtoForSaving() {
        return new UserDtoForSaving("Ivanov", "123", "ivanov@mail.ru", 30);
    }

    private UserDtoForUpdating createUserDtoForUpdating() {
        return new UserDtoForUpdating(UUID.randomUUID(), "Ivanov", "123", "ivanov@mail.ru", 30);
    }

    private UserDtoResponse createUserDtoResponse(UUID id) {
        UserDtoResponse userDtoResponse = new UserDtoResponse();
        userDtoResponse.setId(id);
        userDtoResponse.setUsername("Ivanov");
        userDtoResponse.setAge(30);
        userDtoResponse.setCreatedAt(Instant.now());
        userDtoResponse.setEmail("ivanov@mail.ru");
        userDtoResponse.setUserRoleSet(Set.of(UserRole.ROLE_USER));
        return userDtoResponse;
    }
}

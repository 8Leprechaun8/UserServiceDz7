package org.example.security.service;

import org.example.kafka.dto.OperationType;
import org.example.kafka.dto.UserMessage;
import org.example.kafka.producer.UserMessageProducer;
import org.example.security.dto.SecurityUser;
import org.example.security.dto.UserDtoResponse;
import org.example.security.entity.User;
import org.example.security.entity.UserDetailsAdditional;
import org.example.security.entity.UserRole;
import org.example.security.exception.UserFoundException;
import org.example.security.exception.UserNotFoundException;
import org.example.security.mapper.UserMapper;
import org.example.security.repository.UserRepository;
import org.example.security.service.impl.UserDetailsManagerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsManagerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMessageProducer userMessageProducerKafka;

    @InjectMocks
    private UserDetailsManagerImpl userDetailsManager;

    @BeforeEach
    public void setUp() {
        userDetailsManager = new UserDetailsManagerImpl(
                userRepository,
                userMapper,
                passwordEncoder,
                userMessageProducerKafka);
    }

    @Test
    public void createUserTest_whenSuccess() throws UserFoundException {
        User user = createUser();
        UserDetailsAdditional userDetailsAdditional = new SecurityUser(user);
        when(userMapper.userDetailsAdditionalToUser(userDetailsAdditional)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("567");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);
        UserMessage userMessage = new UserMessage(OperationType.CREATE, user.getEmail());
        doNothing().when(userMessageProducerKafka).sendMessage(any(UserMessage.class));

        userDetailsManager.createUser(userDetailsAdditional);

        verify(userMapper, times(1)).userDetailsAdditionalToUser(userDetailsAdditional);
        verify(passwordEncoder, times(1)).encode("123");
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(userRepository, times(1)).save(user);
        verify(userMessageProducerKafka, times(1)).sendMessage(any(UserMessage.class));
        verifyNoMoreInteractions(userMapper, passwordEncoder, userRepository, userMessageProducerKafka);
    }

    @Test
    public void createUserTest_whenUserFoundException() throws UserFoundException {
        User user = createUser();
        UserDetailsAdditional userDetailsAdditional = new SecurityUser(user);
        when(userMapper.userDetailsAdditionalToUser(userDetailsAdditional)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("567");
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(UserFoundException.class, () -> userDetailsManager.createUser(userDetailsAdditional));

        assertEquals("В бд найден пользователь", exception.getMessage());
        verify(userMapper, times(1)).userDetailsAdditionalToUser(userDetailsAdditional);
        verify(passwordEncoder, times(1)).encode("123");
        verify(userRepository, times(1)).findByUsername(user.getUsername());;
        verifyNoMoreInteractions(userMapper, passwordEncoder, userRepository);
        verifyNoInteractions(userMessageProducerKafka);
    }

    @Test
    public void readUserTest_whenUserNotFoundException() throws UserNotFoundException {
        User user = createUser();
        Optional<User> userOptional = Optional.empty();
        when(userRepository.findById(user.getId())).thenReturn(userOptional);

        Exception exception = assertThrows(UserNotFoundException.class,
                () -> userDetailsManager.readUser(user.getId()));

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordEncoder, userMapper);
    }

    @Test
    public void readUserTest_whenSuccess() throws UserNotFoundException {
        User user = createUser();
        Optional<User> userOptional = Optional.of(user);
        when(userRepository.findById(user.getId())).thenReturn(userOptional);
        UserDtoResponse response = createUserDtoResponse();
        when(userMapper.userToUserDtoResponse(user)).thenReturn(response);

        UserDtoResponse result = userDetailsManager.readUser(user.getId());

        assertNotNull(result);
        assertEquals(response.getId(), result.getId());
        assertEquals(response.getUsername(), result.getUsername());
        assertEquals(response.getEmail(), result.getEmail());
        assertEquals(response.getAge(), result.getAge());
        assertEquals(response.getCreatedAt(), result.getCreatedAt());
        assertEquals(1, result.getUserRoleSet().size());
        assertTrue(result.getUserRoleSet().contains(UserRole.ROLE_USER));
        verify(userRepository).findById(user.getId());
        verify(userMapper).userToUserDtoResponse(user);
        verifyNoMoreInteractions(userRepository, userMapper);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    public void updateUserTest_whenSuccess() throws UserNotFoundException {
        User user = createUser();
        UserDetailsAdditional userDetailsAdditional = new SecurityUser(user);
        when(userMapper.userDetailsAdditionalToUser(userDetailsAdditional)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("567");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userDetailsManager.updateUser(userDetailsAdditional);

        verify(userMapper, times(1)).userDetailsAdditionalToUser(userDetailsAdditional);
        verify(passwordEncoder, times(1)).encode("123");
        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).save(user);
        verifyNoMoreInteractions(userMapper, passwordEncoder, userRepository);
    }

    @Test
    public void updateUserTest_whenUserNotFoundException() throws UserNotFoundException {
        User user = createUser();
        UserDetailsAdditional userDetailsAdditional = new SecurityUser(user);
        when(userMapper.userDetailsAdditionalToUser(userDetailsAdditional)).thenReturn(user);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("567");
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userDetailsManager.updateUser(userDetailsAdditional));

        assertEquals("В бд не найден пользователь", exception.getMessage());
        verify(userMapper, times(1)).userDetailsAdditionalToUser(userDetailsAdditional);
        verify(passwordEncoder, times(1)).encode("123");
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userMapper, passwordEncoder, userRepository);
    }

    @Test
    public void deleteUserTest_whenSuccess() throws UserNotFoundException {
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(user.getId());
        UserMessage userMessage = new UserMessage(OperationType.DELETE, user.getEmail());
        doNothing().when(userMessageProducerKafka).sendMessage(any(UserMessage.class));

        userDetailsManager.deleteUser(user.getId());

        verify(userRepository, times(1)).findById(user.getId());
        verify(userRepository, times(1)).deleteById(user.getId());
        verify(userMessageProducerKafka, times(1)).sendMessage(any(UserMessage.class));
        verifyNoMoreInteractions(userRepository, userMessageProducerKafka);
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    public void deleteUserTest_whenUserNotFoundException() throws UserNotFoundException {
        User user = createUser();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> userDetailsManager.deleteUser(user.getId()));

        assertEquals("В бд не найден пользователь", exception.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, passwordEncoder, userMessageProducerKafka);
    }

    @Test
    public void findAllTest_whenSuccess() {
        User user = createUser();
        User userAnother = createUserAnother();
        List<User> userList = List.of(user, userAnother);
        when(userRepository.findAll()).thenReturn(userList);
        UserDtoResponse response = createUserDtoResponse();
        UserDtoResponse responseAnother = createUserDtoResponseAnother();
        when(userMapper.userListToUserDtoResponseList(userList)).thenReturn(List.of(response, responseAnother));

        List<UserDtoResponse> resultList = userDetailsManager.findAll();

        assertNotNull(resultList);
        assertEquals(2, resultList.size());
        assertEquals(response.getId(), resultList.get(0).getId());
        assertEquals(response.getUsername(), resultList.get(0).getUsername());
        assertEquals(response.getEmail(), resultList.get(0).getEmail());
        assertEquals(response.getAge(), resultList.get(0).getAge());
        assertEquals(response.getCreatedAt(), resultList.get(0).getCreatedAt());
        assertEquals(1, resultList.get(0).getUserRoleSet().size());
        assertTrue(resultList.get(0).getUserRoleSet().contains(UserRole.ROLE_USER));
        assertEquals(responseAnother.getId(), resultList.get(1).getId());
        assertEquals(responseAnother.getUsername(), resultList.get(1).getUsername());
        assertEquals(responseAnother.getEmail(), resultList.get(1).getEmail());
        assertEquals(responseAnother.getAge(), resultList.get(1).getAge());
        assertEquals(responseAnother.getCreatedAt(), resultList.get(1).getCreatedAt());
        assertEquals(1, resultList.get(1).getUserRoleSet().size());
        assertTrue(resultList.get(1).getUserRoleSet().contains(UserRole.ROLE_USER));
        verify(userRepository).findAll();
        verify(userMapper).userListToUserDtoResponseList(userList);
        verifyNoMoreInteractions(userRepository, userMapper);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    public void loadUserByUsernameTest_whenSuccess() throws UsernameNotFoundException {
        User user = createUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsManager.loadUserByUsername(user.getUsername());

        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toSet()).contains("ROLE_USER"));
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    @Test
    public void loadUserByUsernameTest_whenUsernameNotFoundException() throws UsernameNotFoundException {
        User user = createUser();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        Exception exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsManager.loadUserByUsername(user.getUsername()));

        assertEquals("Пользователь по имени не найден", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(userMapper, passwordEncoder);
    }

    private UserDtoResponse createUserDtoResponse() {
        UserDtoResponse response = new UserDtoResponse();
        User user = createUser();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAge(user.getAge());
        response.setCreatedAt(user.getCreatedAt());
        response.setUserRoleSet(user.getUserRoleSet());
        return response;
    }

    private UserDtoResponse createUserDtoResponseAnother() {
        UserDtoResponse response = new UserDtoResponse();
        User user = createUserAnother();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setAge(user.getAge());
        response.setCreatedAt(user.getCreatedAt());
        response.setUserRoleSet(user.getUserRoleSet());
        return response;
    }

    private User createUser() {
        return new User.UserBuilder()
                .id(UUID.randomUUID())
                .username("Ivanov")
                .password("123")
                .isActive(true)
                .email("ivanov@mail.ru")
                .age(33)
                .createdAt(Instant.now())
                .userRoleSet(Set.of(UserRole.ROLE_USER))
                .build();
    }

    private User createUserAnother() {
        return new User.UserBuilder()
                .id(UUID.randomUUID())
                .username("Petrov")
                .password("234")
                .isActive(true)
                .email("petrov@mail.ru")
                .age(34)
                .createdAt(Instant.now())
                .userRoleSet(Set.of(UserRole.ROLE_USER))
                .build();
    }
}

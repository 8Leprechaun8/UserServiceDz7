package org.example.security.service.impl;

import org.example.kafka.dto.OperationType;
import org.example.kafka.dto.UserMessage;
import org.example.kafka.producer.UserMessageProducer;
import org.example.security.dto.SecurityUser;
import org.example.security.dto.UserDtoResponse;
import org.example.security.entity.User;
import org.example.security.entity.UserDetailsAdditional;
import org.example.security.exception.UserFoundException;
import org.example.security.exception.UserNotFoundException;
import org.example.security.mapper.UserMapper;
import org.example.security.repository.UserRepository;
import org.example.security.service.UserDetailsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserDetailsManagerImpl implements UserDetailsManager {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserMessageProducer userMessageProducerKafka;

    @Autowired
    public UserDetailsManagerImpl(UserRepository userRepository, UserMapper userMapper,
                                  @Qualifier("bCryptPasswordEncode") PasswordEncoder passwordEncoder,
                                  UserMessageProducer userMessageProducerKafka) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.userMessageProducerKafka = userMessageProducerKafka;
    }

    @Override
    @Transactional
    public void createUser(UserDetailsAdditional userDetailsAdditional) throws UserFoundException {
        User user = userMapper.userDetailsAdditionalToUser(userDetailsAdditional);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isPresent()) {
            throw new UserFoundException("В бд найден пользователь");
        }
        userRepository.save(user);
        UserMessage userMessage = new UserMessage(OperationType.CREATE, user.getEmail());
        userMessageProducerKafka.sendMessage(userMessage);
    }

    @Override
    public UserDtoResponse readUser(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return userMapper.userToUserDtoResponse(optionalUser.get());
    }

    @Override
    public void updateUser(UserDetailsAdditional userDetailsAdditional) throws UserNotFoundException {
        User user = userMapper.userDetailsAdditionalToUser(userDetailsAdditional);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("В бд не найден пользователь");
        }
        userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (!optionalUser.isPresent()) {
            throw new UserNotFoundException("В бд не найден пользователь");
        }
        String email = optionalUser.get().getEmail();
        userRepository.deleteById(id);
        UserMessage userMessage = new UserMessage(OperationType.DELETE, email);
        userMessageProducerKafka.sendMessage(userMessage);
    }

    @Override
    public List<UserDtoResponse> findAll() {
        List<User> userList = userRepository.findAll();
        return userMapper.userListToUserDtoResponseList(userList);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException("Пользователь по имени не найден");
        }
        return new SecurityUser(optionalUser.get());
    }
}

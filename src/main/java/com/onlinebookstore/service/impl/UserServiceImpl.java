package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.onlinebookstore.dto.user.UserResponseDto;
import com.onlinebookstore.exception.RegistrationException;
import com.onlinebookstore.mapper.UserMapper;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.user.UserRepository;
import com.onlinebookstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user '" + requestDto.getEmail()
                    + "' (already exists)");
        }
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User newUser = userMapper.toUserEntity(requestDto);
        newUser = userRepository.save(newUser);
        return userMapper.toUserResponseDto(newUser);
    }
}

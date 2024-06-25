package com.onlinebookstore.service.impl;

import com.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.onlinebookstore.dto.user.UserResponseDto;
import com.onlinebookstore.exception.RegistrationException;
import com.onlinebookstore.mapper.UserMapper;
import com.onlinebookstore.model.RoleName;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.role.RoleRepository;
import com.onlinebookstore.repository.user.UserRepository;
import com.onlinebookstore.service.ShoppingCartService;
import com.onlinebookstore.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final ShoppingCartService shoppingCartService;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto)
            throws RegistrationException {
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RegistrationException("Can't register user '" + requestDto.getEmail()
                    + "' (already exists)");
        }
        User newUser = userMapper.toUserEntity(requestDto);
        newUser.setRoles(Set.of(roleRepository.findByName(RoleName.USER)
                .orElseThrow(() -> new EntityNotFoundException("Can't find user role by name: "
                        + RoleName.USER))));
        newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        userRepository.save(newUser);
        shoppingCartService.registerShoppingCart(newUser);
        return userMapper.toUserResponseDto(newUser);
    }
}

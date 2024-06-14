package com.onlinebookstore.security;

import com.onlinebookstore.dto.user.UserLoginRequestDto;
import com.onlinebookstore.dto.user.UserLoginResponseDto;
import com.onlinebookstore.model.User;
import com.onlinebookstore.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserLoginResponseDto authenticate(UserLoginRequestDto requestDto) {
        Optional<User> user = userRepository.findByEmail(requestDto.email());
        if (user.isEmpty()) {
            throw new EntityNotFoundException("Can't login. Wrong username or password");
        }
        String hashedPassword = user.get().getPassword();
        if (!passwordEncoder.matches(requestDto.password(), hashedPassword)) {
            throw new EntityNotFoundException("Can't login. Wrong username or password");
        }
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );
        String token = jwtUtil.generateToken(authentication.getName());
        return new UserLoginResponseDto(token);
    }
}

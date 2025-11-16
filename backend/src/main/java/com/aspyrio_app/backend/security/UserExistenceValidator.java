package com.aspyrio_app.backend.security;

import com.aspyrio_app.backend.dto.UserRegisterRequest;
import com.aspyrio_app.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserExistenceValidator {
    private final UserRepository userRepository;

    public void validateUniqueUser(UserRegisterRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
    }
}

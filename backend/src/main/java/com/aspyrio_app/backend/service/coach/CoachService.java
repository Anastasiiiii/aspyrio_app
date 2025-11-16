package com.aspyrio_app.backend.service.coach;

import com.aspyrio_app.backend.dto.UserRegisterRequest;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CoachService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createCoach(UserRegisterRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FITNESS_ADMIN"))) {
            throw new RuntimeException("User does not have FITNESS_ADMIN role");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("tempPassword: " + tempPassword);
        String encodedPassword = passwordEncoder.encode(tempPassword);

        User coach = new User();
        coach.setUsername(request.getUsername());
        coach.setEmail(request.getEmail());
        coach.setPassword(encodedPassword);
        coach.setRole(Role.COACH);

        return userRepository.save(coach);
    }
}

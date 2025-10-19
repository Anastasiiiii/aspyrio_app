package com.aspyrio_app.backend.service;

import com.aspyrio_app.backend.dto.AuthResponse;
import com.aspyrio_app.backend.dto.RegisterRequest;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse registerNetworkAdmin(RegisterRequest request){
        if (userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.NETWORK_ADMIN);

        userRepository.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }
}

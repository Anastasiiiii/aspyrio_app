package com.aspyrio_app.backend.service.regularUser;

import com.aspyrio_app.backend.dto.UserCreateResponse;
import com.aspyrio_app.backend.dto.UserRegisterRequest;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import com.aspyrio_app.backend.security.TemporaryPasswordGenerator;
import com.aspyrio_app.backend.security.UserExistenceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateRegularUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserExistenceValidator userExistenceValidator;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;

    public UserCreateResponse registerRegularUser(UserRegisterRequest request) {
        authValidator.ensureAuthenticated();
        userExistenceValidator.validateUniqueUser(request);
        roleValidator.ensureHasRole("ROLE_FITNESS_ADMIN");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenter center = currentAdmin.getCenter();
        if (center == null) {
            throw new RuntimeException("Fitness admin is not associated with any center");
        }

        String tempPassword = temporaryPasswordGenerator.generateTemporaryPassword();
        System.out.println("tempPassword: " + tempPassword);

        User regularUser = new User();
        regularUser.setUsername(request.getUsername());
        regularUser.setEmail(request.getEmail());
        regularUser.setPassword(passwordEncoder.encode(tempPassword));
        regularUser.setRole(Role.USER);
        regularUser.setCenter(center);

        userRepository.save(regularUser);

        return new UserCreateResponse(request.getUsername(), tempPassword);
    }
}

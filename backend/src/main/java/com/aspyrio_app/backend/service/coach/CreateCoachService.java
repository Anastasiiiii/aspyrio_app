package com.aspyrio_app.backend.service.coach;

import com.aspyrio_app.backend.dto.UserRegisterRequest;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import com.aspyrio_app.backend.security.TemporaryPasswordGenerator;
import com.aspyrio_app.backend.security.UserExistenceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCoachService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserExistenceValidator userExistenceValidator;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    private final TemporaryPasswordGenerator temporaryPasswordGenerator;

    public User registerCoach(UserRegisterRequest request) {
        authValidator.ensureAuthenticated();
        userExistenceValidator.validateUniqueUser(request);
        roleValidator.ensureHasRole("ROLE_FITNESS_ADMIN");

        String tempPassword = temporaryPasswordGenerator.generateTemporaryPassword();
        System.out.println("tempPassword: " + tempPassword);

        User coach = new User();
        coach.setUsername(request.getUsername());
        coach.setEmail(request.getEmail());
        coach.setPassword(passwordEncoder.encode(tempPassword));
        coach.setRole(Role.COACH);

        return userRepository.save(coach);
    }
}

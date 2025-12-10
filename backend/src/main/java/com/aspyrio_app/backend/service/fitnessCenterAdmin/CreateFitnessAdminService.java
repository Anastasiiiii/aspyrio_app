package com.aspyrio_app.backend.service.fitnessCenterAdmin;

import com.aspyrio_app.backend.dto.FitnessAdminCreateResponse;
import com.aspyrio_app.backend.dto.FitnessAdminRegisterRequest;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.FitnessCenterRepository;
import com.aspyrio_app.backend.repository.FitnessCenterNetworkRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import com.aspyrio_app.backend.security.UserExistenceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateFitnessAdminService {
    private final UserRepository userRepository;
    private final FitnessCenterRepository fitnessCenterRepository;
    private final FitnessCenterNetworkRepository fitnessCenterNetworkRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    public FitnessAdminCreateResponse createFitnessAdmin(FitnessAdminRegisterRequest request) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_NETWORK_ADMIN");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenterNetwork network = fitnessCenterNetworkRepository.findByNetworkAdminId(currentAdmin)
                .orElseThrow(() -> new RuntimeException("Network not found for current admin"));

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        FitnessCenter fitnessCenter = fitnessCenterRepository.findById(request.getFitnessCenterId())
                .orElseThrow(() -> new RuntimeException("Fitness center not found"));

        if (!fitnessCenter.getNetwork().getId().equals(network.getId())) {
            throw new RuntimeException("Fitness center does not belong to your network");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        System.out.println("tempPassword: " + tempPassword);
        String encodedPassword = passwordEncoder.encode(tempPassword);

        User fitnessAdmin = new User();
        fitnessAdmin.setUsername(request.getUsername());
        fitnessAdmin.setEmail(request.getEmail());
        fitnessAdmin.setPassword(encodedPassword);
        fitnessAdmin.setRole(Role.FITNESS_ADMIN);
        fitnessAdmin.setCenter(fitnessCenter);

        userRepository.save(fitnessAdmin);

        return new FitnessAdminCreateResponse(request.getUsername(), tempPassword);
    }
}

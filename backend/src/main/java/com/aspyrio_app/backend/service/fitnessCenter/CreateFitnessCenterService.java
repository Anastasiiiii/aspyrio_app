package com.aspyrio_app.backend.service.fitnessCenter;

import com.aspyrio_app.backend.dto.FitnessCenterRegisterRequest;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.FitnessCenterRepository;
import com.aspyrio_app.backend.repository.FitnessCenterNetworkRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateFitnessCenterService {
    private final FitnessCenterRepository fitnessCenterRepository;
    private final FitnessCenterNetworkRepository fitnessCenterNetworkRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    public FitnessCenter createFitnessCenter(FitnessCenterRegisterRequest request) {
        if (fitnessCenterRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Fitness center already exists");
        }

        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_NETWORK_ADMIN");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenterNetwork network = fitnessCenterNetworkRepository.findByNetworkAdminId(currentAdmin)
                .orElseThrow(() -> new RuntimeException("Network not found for current admin"));

        FitnessCenter fitnessCenter = new FitnessCenter();
        fitnessCenter.setName(request.getName());
        fitnessCenter.setAddress(request.getAddress());
        fitnessCenter.setCity(request.getCity());
        fitnessCenter.setCountry(request.getCountry());
        fitnessCenter.setPostalCode(request.getPostalCode() != null ? request.getPostalCode() : "");
        fitnessCenter.setNetwork(network);

        return fitnessCenterRepository.save(fitnessCenter);
    }

    public List<FitnessCenter> getFitnessCenters() {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_NETWORK_ADMIN");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenterNetwork network = fitnessCenterNetworkRepository.findByNetworkAdminId(currentAdmin)
                .orElseThrow(() -> new RuntimeException("Network not found for current admin"));

        return fitnessCenterRepository.findAllByNetwork(network);
    }
}

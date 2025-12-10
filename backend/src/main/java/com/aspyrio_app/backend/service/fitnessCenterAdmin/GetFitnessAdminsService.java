package com.aspyrio_app.backend.service.fitnessCenterAdmin;

import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.FitnessCenterNetworkRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetFitnessAdminsService {
    private final UserRepository userRepository;
    private final FitnessCenterNetworkRepository fitnessCenterNetworkRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional(readOnly = true)
    public List<User> getAllFitnessAdmins() {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_NETWORK_ADMIN");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenterNetwork network = fitnessCenterNetworkRepository.findByNetworkAdminId(currentAdmin)
                .orElseThrow(() -> new RuntimeException("Network not found for current admin"));

        List<User> allAdmins = userRepository.findByRole(Role.FITNESS_ADMIN);
        
        return allAdmins.stream()
                .filter(admin -> admin.getCenter() != null 
                        && admin.getCenter().getNetwork() != null
                        && admin.getCenter().getNetwork().getId().equals(network.getId()))
                .collect(Collectors.toList());
    }
}


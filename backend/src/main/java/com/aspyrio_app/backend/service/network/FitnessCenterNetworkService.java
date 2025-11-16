package com.aspyrio_app.backend.service.network;

import com.aspyrio_app.backend.dto.FitnessCenterRegisterRequest;
import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.FitnessCenterNetworkRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FitnessCenterNetworkService {
    private final FitnessCenterNetworkRepository fitnessCenterNetworkRepository;
    private final UserRepository userRepository;

    public FitnessCenterNetwork createNetwork(FitnessCenterRegisterRequest request) {
        if (fitnessCenterNetworkRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("Network already exists");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_NETWORK_ADMIN"))) {
            throw new RuntimeException("User does not have NETWORK_ADMIN role");
        }

        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenterNetwork network = new FitnessCenterNetwork();
        network.setName(request.getName());
        network.setNetworkAdminId(currentAdmin);

        return fitnessCenterNetworkRepository.save(network);
    }

}

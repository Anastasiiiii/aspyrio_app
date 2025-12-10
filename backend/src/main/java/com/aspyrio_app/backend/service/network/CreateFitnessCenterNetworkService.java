package com.aspyrio_app.backend.service.network;

import com.aspyrio_app.backend.dto.FitnessCenterNetworkRegisterRequest;
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
public class CreateFitnessCenterNetworkService {
    private final FitnessCenterNetworkRepository fitnessCenterNetworkRepository;
    private final UserRepository userRepository;

    public FitnessCenterNetwork createNetwork(FitnessCenterNetworkRegisterRequest request) {
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

        if (fitnessCenterNetworkRepository.findByNetworkAdminId(currentAdmin).isPresent()) {
            throw new RuntimeException("Network admin can only create one network");
        }

        FitnessCenterNetwork network = new FitnessCenterNetwork();
        network.setName(request.getName());
        network.setNetworkAdminId(currentAdmin);

        return fitnessCenterNetworkRepository.save(network);
    }

    public boolean hasNetwork() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_NETWORK_ADMIN"))) {
            return false;
        }

        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElse(null);

        if (currentAdmin == null) {
            return false;
        }

        return fitnessCenterNetworkRepository.findByNetworkAdminId(currentAdmin).isPresent();
    }

}

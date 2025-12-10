package com.aspyrio_app.backend.service.studio;

import com.aspyrio_app.backend.dto.StudioResponse;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Studio;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.StudioRepository;
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
public class GetStudiosService {
    private final StudioRepository studioRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional(readOnly = true)
    public List<StudioResponse> getStudios() {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_FITNESS_ADMIN");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenter center = currentAdmin.getCenter();
        if (center == null) {
            throw new RuntimeException("Fitness admin is not associated with any center");
        }

        List<Studio> studios = studioRepository.findByFitnessCenter(center);
        return studios.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private StudioResponse mapToResponse(Studio studio) {
        StudioResponse response = new StudioResponse();
        response.setId(studio.getId());
        response.setFitnessCenterId(studio.getFitnessCenter().getId());
        response.setFitnessCenterName(studio.getFitnessCenter().getName());
        response.setName(studio.getName());
        response.setCapacity(studio.getCapacity());
        response.setDescription(studio.getDescription());
        response.setCreatedAt(studio.getCreatedAt());
        response.setUpdatedAt(studio.getUpdatedAt());
        return response;
    }
}



package com.aspyrio_app.backend.service.studio;

import com.aspyrio_app.backend.dto.CreateStudioRequest;
import com.aspyrio_app.backend.dto.StudioResponse;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Studio;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.FitnessCenterRepository;
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

@Service
@RequiredArgsConstructor
public class CreateStudioService {
    private final StudioRepository studioRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional
    public StudioResponse createStudio(CreateStudioRequest request) {
        try {
            authValidator.ensureAuthenticated();
            roleValidator.ensureHasRole("ROLE_FITNESS_ADMIN");
        } catch (Exception e) {
            System.err.println("Authentication/Authorization error in CreateStudioService: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentAdmin = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenter center = currentAdmin.getCenter();
        if (center == null) {
            throw new RuntimeException("Fitness admin is not associated with any center");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Studio name is required");
        }

        if (request.getCapacity() == null || request.getCapacity() <= 0) {
            throw new RuntimeException("Studio capacity must be greater than 0");
        }

        Studio studio = new Studio();
        studio.setFitnessCenter(center);
        studio.setName(request.getName().trim());
        studio.setCapacity(request.getCapacity());
        studio.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);

        studio = studioRepository.save(studio);

        return mapToResponse(studio);
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


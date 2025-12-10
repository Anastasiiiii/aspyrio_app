package com.aspyrio_app.backend.service.groupTrainingSlot;

import com.aspyrio_app.backend.dto.CoachWithSportsResponse;
import com.aspyrio_app.backend.model.CoachProfile;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.CoachProfileRepository;
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
public class GetCoachesWithSportsService {
    private final UserRepository userRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional(readOnly = true)
    public List<CoachWithSportsResponse> getCoachesWithSports() {
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

        List<User> coaches = userRepository.findByRoleAndCenter(Role.COACH, center);

        return coaches.stream()
                .map(coach -> {
                    CoachProfile profile = coachProfileRepository.findByUser(coach).orElse(null);
                    if (profile == null) {
                        return null;
                    }

                    List<CoachWithSportsResponse.SportInfo> sports = profile.getSports().stream()
                            .map(sport -> new CoachWithSportsResponse.SportInfo(sport.getId(), sport.getName()))
                            .collect(Collectors.toList());

                    return new CoachWithSportsResponse(
                            coach.getId(),
                            coach.getUsername(),
                            coach.getEmail(),
                            profile.getFirstName(),
                            profile.getLastName(),
                            sports
                    );
                })
                .filter(coach -> coach != null && !coach.getSports().isEmpty())
                .collect(Collectors.toList());
    }
}


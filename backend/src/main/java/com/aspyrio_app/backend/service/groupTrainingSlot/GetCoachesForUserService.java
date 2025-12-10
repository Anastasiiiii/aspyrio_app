package com.aspyrio_app.backend.service.groupTrainingSlot;

import com.aspyrio_app.backend.dto.CoachWithSportsResponse;
import com.aspyrio_app.backend.model.CoachProfile;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Role;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.CoachProfileRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
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
public class GetCoachesForUserService {
    private final UserRepository userRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final AuthValidator authValidator;

    @Transactional(readOnly = true)
    public List<CoachWithSportsResponse> getCoachesForUser() {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenter center = currentUser.getCenter();
        if (center == null) {
            throw new RuntimeException("User is not associated with any center");
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



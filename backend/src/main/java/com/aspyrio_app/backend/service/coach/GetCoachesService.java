package com.aspyrio_app.backend.service.coach;

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
public class GetCoachesService {
    private final UserRepository userRepository;
    private final CoachProfileRepository coachProfileRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional(readOnly = true)
    public List<User> getAllCoaches() {
        return getAllCoachesBySport(null);
    }

    @Transactional(readOnly = true)
    public List<User> getAllCoachesBySport(Long sportId) {
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

        // Отримуємо всіх тренерів центру
        List<User> allCoaches = userRepository.findByRoleAndCenter(Role.COACH, center);

        // Якщо вказано sportId, фільтруємо тренерів по спорту
        if (sportId != null) {
            List<CoachProfile> profilesWithSport = coachProfileRepository.findBySportId(sportId);
            List<Long> coachIdsFromCenter = allCoaches.stream()
                    .map(User::getId)
                    .collect(Collectors.toList());
            
            return profilesWithSport.stream()
                    .map(CoachProfile::getUser)
                    .filter(user -> coachIdsFromCenter.contains(user.getId()))
                    .collect(Collectors.toList());
        }

        return allCoaches;
    }
}


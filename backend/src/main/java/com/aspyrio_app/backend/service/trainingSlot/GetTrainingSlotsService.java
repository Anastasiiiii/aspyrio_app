package com.aspyrio_app.backend.service.trainingSlot;

import com.aspyrio_app.backend.model.TrainingSlot;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.TrainingSlotRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTrainingSlotsService {
    private final TrainingSlotRepository trainingSlotRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional(readOnly = true)
    public List<TrainingSlot> getTrainingSlots(LocalDateTime startDate, LocalDateTime endDate) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (startDate != null && endDate != null) {
            return trainingSlotRepository.findByCoachAndDateRange(currentUser, startDate, endDate);
        } else if (startDate != null) {
            return trainingSlotRepository.findByCoachAndStartTimeAfter(currentUser, startDate);
        } else {
            return trainingSlotRepository.findByCoach(currentUser);
        }
    }
}



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

@Service
@RequiredArgsConstructor
public class DeleteTrainingSlotService {
    private final TrainingSlotRepository trainingSlotRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional
    public void deleteTrainingSlot(Long slotId) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        TrainingSlot slot = trainingSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Training slot not found"));

        if (!slot.getCoach().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You can only delete your own training slots");
        }

        trainingSlotRepository.delete(slot);
    }
}



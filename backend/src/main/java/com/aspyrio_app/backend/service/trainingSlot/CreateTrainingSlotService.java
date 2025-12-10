package com.aspyrio_app.backend.service.trainingSlot;

import com.aspyrio_app.backend.dto.CreateTrainingSlotRequest;
import com.aspyrio_app.backend.model.Sports;
import com.aspyrio_app.backend.model.TrainingSlot;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.SportsRepository;
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
public class CreateTrainingSlotService {
    private final TrainingSlotRepository trainingSlotRepository;
    private final UserRepository userRepository;
    private final SportsRepository sportsRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;

    @Transactional
    public TrainingSlot createTrainingSlot(CreateTrainingSlotRequest request) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Sports sport = sportsRepository.findById(request.getSportId())
                .orElseThrow(() -> new RuntimeException("Sport not found"));

        if (request.getStartTime() == null || request.getEndTime() == null) {
            throw new RuntimeException("Start time and end time are required");
        }

        if (request.getEndTime().isBefore(request.getStartTime()) || 
            request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cannot create training slot in the past");
        }

        if (request.getStartTime().getHour() < 6) {
            throw new RuntimeException("Training slots can only be created from 6:00 AM");
        }

        if (request.getEndTime().getHour() < 6) {
            throw new RuntimeException("Training slots must end at 6:00 AM or later");
        }

        List<TrainingSlot> overlappingSlots = trainingSlotRepository.findByCoachAndDateRange(
            currentUser,
            request.getStartTime(),
            request.getEndTime()
        );
        
        for (TrainingSlot existingSlot : overlappingSlots) {
            if (isOverlapping(request.getStartTime(), request.getEndTime(), 
                            existingSlot.getStartTime(), existingSlot.getEndTime())) {
                throw new RuntimeException("Training slot overlaps with existing slot");
            }
        }

        TrainingSlot slot = new TrainingSlot();
        slot.setCoach(currentUser);
        slot.setSport(sport);
        slot.setStartTime(request.getStartTime());
        slot.setEndTime(request.getEndTime());

        return trainingSlotRepository.save(slot);
    }

    private boolean isOverlapping(LocalDateTime start1, LocalDateTime end1,
                                 LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && start2.isBefore(end1);
    }
}


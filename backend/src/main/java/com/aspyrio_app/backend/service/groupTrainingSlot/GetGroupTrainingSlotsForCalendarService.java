package com.aspyrio_app.backend.service.groupTrainingSlot;

import com.aspyrio_app.backend.dto.GroupTrainingSlotResponse;
import com.aspyrio_app.backend.model.*;
import com.aspyrio_app.backend.repository.GroupTrainingSlotRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetGroupTrainingSlotsForCalendarService {
    private final GroupTrainingSlotRepository groupTrainingSlotRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;

    @Transactional(readOnly = true)
    public List<GroupTrainingSlotResponse> getGroupTrainingSlotsForCalendar(
            LocalDateTime startDate, LocalDateTime endDate) {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Role userRole = currentUser.getRole();
        List<GroupTrainingSlot> slots;

        if (userRole == Role.COACH) {
            if (startDate != null && endDate != null) {
                slots = groupTrainingSlotRepository.findByCoachAndStatusAndStartTimeBetween(
                        currentUser, TrainingSlotStatus.APPROVED, startDate, endDate);
            } else {
                slots = groupTrainingSlotRepository.findByCoachAndStatus(
                        currentUser, TrainingSlotStatus.APPROVED);
            }
        } else if (userRole == Role.FITNESS_ADMIN) {
            if (startDate != null && endDate != null) {
                slots = groupTrainingSlotRepository.findByCreatedByCenterAndStatusAndStartTimeBetween(
                        currentUser.getCenter(), TrainingSlotStatus.APPROVED, startDate, endDate);
            } else {
                slots = groupTrainingSlotRepository.findByCreatedByCenterAndStatus(
                        currentUser.getCenter(), TrainingSlotStatus.APPROVED);
            }
        } else {
            throw new RuntimeException("Access denied. Only COACH and FITNESS_ADMIN can view calendar slots.");
        }

        return slots.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private GroupTrainingSlotResponse mapToResponse(GroupTrainingSlot slot) {
        GroupTrainingSlotResponse response = new GroupTrainingSlotResponse();
        response.setId(slot.getId());
        response.setCoachId(slot.getCoach().getId());
        response.setCoachName(slot.getCoach().getUsername());
        response.setSportId(slot.getSport().getId());
        response.setSportName(slot.getSport().getName());
        if (slot.getStudio() != null) {
            response.setStudioId(slot.getStudio().getId());
            response.setStudioName(slot.getStudio().getName());
        }
        response.setTrainingCategory(slot.getTrainingCategory().name());
        response.setTrainingType(slot.getTrainingType().name());
        response.setStartTime(slot.getStartTime());
        response.setEndTime(slot.getEndTime());
        response.setMaxParticipants(slot.getMaxParticipants());
        response.setStatus(slot.getStatus());
        response.setCreatedAt(slot.getCreatedAt());
        return response;
    }
}



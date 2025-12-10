package com.aspyrio_app.backend.service.groupTrainingSlot;

import com.aspyrio_app.backend.dto.GroupTrainingSlotResponse;
import com.aspyrio_app.backend.model.*;
import com.aspyrio_app.backend.repository.GroupTrainingBookingRepository;
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
public class GetAvailableGroupTrainingSlotsService {
    private final GroupTrainingSlotRepository groupTrainingSlotRepository;
    private final GroupTrainingBookingRepository groupTrainingBookingRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;

    @Transactional(readOnly = true)
    public List<GroupTrainingSlotResponse> getAvailableGroupTrainingSlots(
            LocalDateTime startDate, LocalDateTime endDate) {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GroupTrainingSlot> approvedSlots;
        if (startDate != null && endDate != null) {
            approvedSlots = groupTrainingSlotRepository.findByStatusAndStartTimeBetween(
                    TrainingSlotStatus.APPROVED, startDate, endDate);
        } else {
            approvedSlots = groupTrainingSlotRepository.findByStatus(TrainingSlotStatus.APPROVED);
        }

        return approvedSlots.stream()
                .filter(slot -> {
                    long currentBookings = groupTrainingBookingRepository.findByGroupTrainingSlot(slot)
                            .stream()
                            .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                            .count();
                    
                    return currentBookings < slot.getMaxParticipants();
                })
                .map(slot -> mapToResponse(slot, currentUser))
                .collect(Collectors.toList());
    }

    private GroupTrainingSlotResponse mapToResponse(GroupTrainingSlot slot, User currentUser) {
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

        long currentBookings = groupTrainingBookingRepository.findByGroupTrainingSlot(slot)
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();
        response.setAvailableSpots((int) (slot.getMaxParticipants() - currentBookings));

        boolean isBooked = groupTrainingBookingRepository.findByGroupTrainingSlotAndUser(slot, currentUser)
                .map(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .orElse(false);
        response.setIsBooked(isBooked);

        return response;
    }
}



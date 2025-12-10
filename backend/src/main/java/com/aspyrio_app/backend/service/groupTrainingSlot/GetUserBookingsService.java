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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetUserBookingsService {
    private final GroupTrainingBookingRepository groupTrainingBookingRepository;
    private final GroupTrainingSlotRepository groupTrainingSlotRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;

    @Transactional(readOnly = true)
    public List<GroupTrainingSlotResponse> getUserBookings(
            LocalDateTime startDate, LocalDateTime endDate) {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GroupTrainingSlotResponse> allSlots = new ArrayList<>();

        List<GroupTrainingBooking> bookings = groupTrainingBookingRepository.findByUser(currentUser)
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .collect(Collectors.toList());

        if (startDate != null && endDate != null) {
            bookings = bookings.stream()
                    .filter(booking -> {
                        LocalDateTime slotStart = booking.getGroupTrainingSlot().getStartTime();
                        return !slotStart.isBefore(startDate) && !slotStart.isAfter(endDate);
                    })
                    .collect(Collectors.toList());
        }

        allSlots.addAll(bookings.stream()
                .map(booking -> mapToResponse(booking.getGroupTrainingSlot()))
                .collect(Collectors.toList()));

        List<GroupTrainingSlot> allIndividualSlots;
        if (startDate != null && endDate != null) {
            allIndividualSlots = groupTrainingSlotRepository.findByUserAndStatus(currentUser, TrainingSlotStatus.APPROVED)
                    .stream()
                    .filter(slot -> slot.getTrainingCategory() == TrainingCategory.INDIVIDUAL)
                    .filter(slot -> {
                        LocalDateTime slotStart = slot.getStartTime();
                        return !slotStart.isBefore(startDate) && !slotStart.isAfter(endDate);
                    })
                    .collect(Collectors.toList());
        } else {
            allIndividualSlots = groupTrainingSlotRepository.findByUserAndStatus(currentUser, TrainingSlotStatus.APPROVED)
                    .stream()
                    .filter(slot -> slot.getTrainingCategory() == TrainingCategory.INDIVIDUAL)
                    .collect(Collectors.toList());
        }

        final List<GroupTrainingSlot> individualSlots = allIndividualSlots;

        allSlots.addAll(individualSlots.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()));

        return allSlots;
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
        response.setIsBooked(true);
        return response;
    }
}


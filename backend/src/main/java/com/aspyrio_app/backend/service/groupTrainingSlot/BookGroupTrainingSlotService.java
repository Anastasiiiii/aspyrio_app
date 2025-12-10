package com.aspyrio_app.backend.service.groupTrainingSlot;

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

@Service
@RequiredArgsConstructor
public class BookGroupTrainingSlotService {
    private final GroupTrainingSlotRepository groupTrainingSlotRepository;
    private final GroupTrainingBookingRepository groupTrainingBookingRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;

    @Transactional
    public void bookGroupTrainingSlot(Long slotId) {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        GroupTrainingSlot slot = groupTrainingSlotRepository.findById(slotId)
                .orElseThrow(() -> new RuntimeException("Training slot not found"));

        if (slot.getStatus() != TrainingSlotStatus.APPROVED) {
            throw new RuntimeException("Cannot book a slot that is not approved");
        }

        groupTrainingBookingRepository.findByGroupTrainingSlotAndUser(slot, currentUser)
                .ifPresent(booking -> {
                    if (booking.getBookingStatus() == BookingStatus.CONFIRMED) {
                        throw new RuntimeException("You have already booked this training slot");
                    }
                });

        long currentBookings = groupTrainingBookingRepository.findByGroupTrainingSlot(slot)
                .stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        if (currentBookings >= slot.getMaxParticipants()) {
            throw new RuntimeException("No available spots for this training slot");
        }

        GroupTrainingBooking booking = new GroupTrainingBooking();
        booking.setGroupTrainingSlot(slot);
        booking.setUser(currentUser);
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        groupTrainingBookingRepository.save(booking);
    }
}



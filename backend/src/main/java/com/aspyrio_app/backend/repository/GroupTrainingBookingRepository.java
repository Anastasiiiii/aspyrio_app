package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.GroupTrainingBooking;
import com.aspyrio_app.backend.model.GroupTrainingSlot;
import com.aspyrio_app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupTrainingBookingRepository extends JpaRepository<GroupTrainingBooking, Long> {
    Optional<GroupTrainingBooking> findByGroupTrainingSlotAndUser(GroupTrainingSlot slot, User user);
    List<GroupTrainingBooking> findByUser(User user);
    List<GroupTrainingBooking> findByGroupTrainingSlot(GroupTrainingSlot slot);
}



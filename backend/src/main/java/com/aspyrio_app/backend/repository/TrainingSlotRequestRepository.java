package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.GroupTrainingSlot;
import com.aspyrio_app.backend.model.RequestStatus;
import com.aspyrio_app.backend.model.TrainingSlotRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingSlotRequestRepository extends JpaRepository<TrainingSlotRequest, Long> {
    Optional<TrainingSlotRequest> findByGroupTrainingSlot(GroupTrainingSlot slot);
    List<TrainingSlotRequest> findByStatus(RequestStatus status);
}



package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.GroupTrainingSlot;
import com.aspyrio_app.backend.model.TrainingSlotStatus;
import com.aspyrio_app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface GroupTrainingSlotRepository extends JpaRepository<GroupTrainingSlot, Long> {
    List<GroupTrainingSlot> findByCoach(User coach);
    List<GroupTrainingSlot> findByCoachAndStatus(User coach, TrainingSlotStatus status);
    List<GroupTrainingSlot> findByCoachAndStatusAndStartTimeBetween(
            User coach, TrainingSlotStatus status, LocalDateTime start, LocalDateTime end);
    List<GroupTrainingSlot> findByStatus(TrainingSlotStatus status);
    List<GroupTrainingSlot> findByStatusAndStartTimeBetween(
            TrainingSlotStatus status, LocalDateTime start, LocalDateTime end);
    List<GroupTrainingSlot> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT gts FROM GroupTrainingSlot gts WHERE gts.createdBy.center = :center AND gts.status = :status")
    List<GroupTrainingSlot> findByCreatedByCenterAndStatus(
            @Param("center") FitnessCenter center, @Param("status") TrainingSlotStatus status);
    
    @Query("SELECT gts FROM GroupTrainingSlot gts WHERE gts.createdBy.center = :center AND gts.status = :status AND gts.startTime BETWEEN :start AND :end")
    List<GroupTrainingSlot> findByCreatedByCenterAndStatusAndStartTimeBetween(
            @Param("center") FitnessCenter center, 
            @Param("status") TrainingSlotStatus status, 
            @Param("start") LocalDateTime start, 
            @Param("end") LocalDateTime end);
    
    List<GroupTrainingSlot> findByUser(User user);
    List<GroupTrainingSlot> findByUserAndStatus(User user, TrainingSlotStatus status);
}


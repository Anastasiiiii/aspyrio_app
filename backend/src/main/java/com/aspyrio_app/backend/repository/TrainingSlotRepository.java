package com.aspyrio_app.backend.repository;

import com.aspyrio_app.backend.model.TrainingSlot;
import com.aspyrio_app.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TrainingSlotRepository extends JpaRepository<TrainingSlot, Long> {
    List<TrainingSlot> findByCoach(User coach);
    
    @Query("SELECT ts FROM TrainingSlot ts WHERE ts.coach = :coach AND ts.startTime >= :startDate AND ts.startTime < :endDate ORDER BY ts.startTime ASC")
    List<TrainingSlot> findByCoachAndDateRange(
        @Param("coach") User coach,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT ts FROM TrainingSlot ts WHERE ts.coach = :coach AND ts.startTime >= :startDate ORDER BY ts.startTime ASC")
    List<TrainingSlot> findByCoachAndStartTimeAfter(
        @Param("coach") User coach,
        @Param("startDate") LocalDateTime startDate
    );
}



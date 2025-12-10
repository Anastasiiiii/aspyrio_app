package com.aspyrio_app.backend.dto;

import com.aspyrio_app.backend.model.TrainingSlotStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupTrainingSlotResponse {
    private Long id;
    private Long coachId;
    private String coachName;
    private Long sportId;
    private String sportName;
    private Long studioId;
    private String studioName;
    private String trainingCategory;
    private String trainingType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
    private Integer availableSpots;
    private Boolean isBooked;
    private TrainingSlotStatus status;
    private LocalDateTime createdAt;
    private Long requestId;
}


package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupTrainingSlotRequest {
    private Long coachId;
    private Long sportId;
    private Long studioId;
    private String trainingCategory;
    private String trainingType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer maxParticipants;
}



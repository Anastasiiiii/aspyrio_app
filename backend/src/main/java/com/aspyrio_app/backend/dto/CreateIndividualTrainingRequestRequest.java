package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateIndividualTrainingRequestRequest {
    private Long coachId;
    private Long sportId;
    private LocalDateTime requestedStartTime;
    private LocalDateTime requestedEndTime;
    private String trainingType;
    private String message;
}



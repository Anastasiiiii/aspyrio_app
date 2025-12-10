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
    private String trainingType; // ONLINE, OFFLINE, BOTH_ONLINE_OFFLINE
    private String message; // Optional message
}



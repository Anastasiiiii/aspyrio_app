package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndividualTrainingRequestResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long coachId;
    private String coachName;
    private Long sportId;
    private String sportName;
    private LocalDateTime requestedStartTime;
    private LocalDateTime requestedEndTime;
    private String trainingType;
    private String message;
    private String status;
    private LocalDateTime coachResponseAt;
    private LocalDateTime createdAt;
}



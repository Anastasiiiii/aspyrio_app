package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingSlotRequestResponse {
    private Long id;
    private Long groupTrainingSlotId;
    private String status;
    private LocalDateTime coachResponseAt;
    private LocalDateTime createdAt;
    private GroupTrainingSlotResponse slotDetails;
}



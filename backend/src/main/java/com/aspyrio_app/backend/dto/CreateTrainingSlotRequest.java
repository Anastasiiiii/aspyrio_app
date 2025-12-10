package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTrainingSlotRequest {
    private Long sportId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}



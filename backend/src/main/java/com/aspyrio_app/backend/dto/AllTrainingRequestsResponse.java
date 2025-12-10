package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllTrainingRequestsResponse {
    private List<TrainingSlotRequestResponse> groupTrainingRequests;
    private List<IndividualTrainingRequestResponse> individualTrainingRequests;
}



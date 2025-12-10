package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.ApproveRejectRequest;
import com.aspyrio_app.backend.dto.CreateIndividualTrainingRequestRequest;
import com.aspyrio_app.backend.dto.IndividualTrainingRequestResponse;
import com.aspyrio_app.backend.service.individualTrainingRequest.ApproveRejectIndividualTrainingRequestService;
import com.aspyrio_app.backend.service.individualTrainingRequest.CreateIndividualTrainingRequestService;
import com.aspyrio_app.backend.service.individualTrainingRequest.GetIndividualTrainingRequestsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/individual-training-requests")
public class IndividualTrainingRequestController {
    private final CreateIndividualTrainingRequestService createIndividualTrainingRequestService;
    private final GetIndividualTrainingRequestsService getIndividualTrainingRequestsService;
    private final ApproveRejectIndividualTrainingRequestService approveRejectIndividualTrainingRequestService;

    @PostMapping
    public ResponseEntity<IndividualTrainingRequestResponse> createIndividualTrainingRequest(
            @RequestBody CreateIndividualTrainingRequestRequest request) {
        return ResponseEntity.ok(createIndividualTrainingRequestService.createIndividualTrainingRequest(request));
    }

    @GetMapping
    public ResponseEntity<List<IndividualTrainingRequestResponse>> getIndividualTrainingRequests() {
        return ResponseEntity.ok(getIndividualTrainingRequestsService.getIndividualTrainingRequests());
    }

    @PostMapping("/approve-reject")
    public ResponseEntity<Void> approveOrRejectIndividualTrainingRequest(
            @RequestBody ApproveRejectRequest request) {
        approveRejectIndividualTrainingRequestService.approveOrRejectIndividualTrainingRequest(request);
        return ResponseEntity.ok().build();
    }
}



package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.FitnessCenterRegisterRequest;
import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import com.aspyrio_app.backend.service.network.CreateFitnessCenterNetworkService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class FitnessCenterNetworkController {
    private final CreateFitnessCenterNetworkService fitnessCenterNetworkService;

    @PostMapping("/register-fitness-network")
    public ResponseEntity<FitnessCenterNetwork> registerFitnessNetwork(@RequestBody FitnessCenterRegisterRequest request) {
        return ResponseEntity.ok(fitnessCenterNetworkService.createNetwork(request));
    }

}

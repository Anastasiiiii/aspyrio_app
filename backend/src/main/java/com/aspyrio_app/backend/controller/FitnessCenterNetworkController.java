package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.FitnessCenterNetworkRegisterRequest;
import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import com.aspyrio_app.backend.service.network.CreateFitnessCenterNetworkService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@RestController
public class FitnessCenterNetworkController {
    private final CreateFitnessCenterNetworkService fitnessCenterNetworkService;

    @PostMapping("/api/auth/register-fitness-network")
    public ResponseEntity<FitnessCenterNetwork> registerFitnessNetwork(@RequestBody FitnessCenterNetworkRegisterRequest request) {
        return ResponseEntity.ok(fitnessCenterNetworkService.createNetwork(request));
    }

    @GetMapping("/api/network/check-network")
    public ResponseEntity<Map<String, Boolean>> checkNetwork() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasNetwork", fitnessCenterNetworkService.hasNetwork());
        return ResponseEntity.ok(response);
    }

}

package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.FitnessCenterRegisterRequest;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.service.fitnessCenter.CreateFitnessCenterService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class FitnessCenterController {
    private final CreateFitnessCenterService createFitnessCenterService;

    @PostMapping("/fitness-center/create")
    public ResponseEntity<FitnessCenter> createFitnessCenter(@RequestBody FitnessCenterRegisterRequest request) {
        return ResponseEntity.ok(createFitnessCenterService.createFitnessCenter(request));
    }

    @GetMapping("/fitness-center/list")
    public ResponseEntity<List<FitnessCenter>> getFitnessCenters() {
        return ResponseEntity.ok(createFitnessCenterService.getFitnessCenters());
    }
}

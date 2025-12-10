package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.CreateTrainingSlotRequest;
import com.aspyrio_app.backend.model.TrainingSlot;
import com.aspyrio_app.backend.service.trainingSlot.CreateTrainingSlotService;
import com.aspyrio_app.backend.service.trainingSlot.DeleteTrainingSlotService;
import com.aspyrio_app.backend.service.trainingSlot.GetTrainingSlotsService;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/training-slots")
public class TrainingSlotController {
    private final CreateTrainingSlotService createTrainingSlotService;
    private final GetTrainingSlotsService getTrainingSlotsService;
    private final DeleteTrainingSlotService deleteTrainingSlotService;

    @PostMapping
    public ResponseEntity<?> createTrainingSlot(@RequestBody CreateTrainingSlotRequest request) {
        try {
            TrainingSlot slot = createTrainingSlotService.createTrainingSlot(request);
            return ResponseEntity.ok(slot);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> getTrainingSlots(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<TrainingSlot> slots = getTrainingSlotsService.getTrainingSlots(startDate, endDate);
            return ResponseEntity.ok(slots);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Unexpected error: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrainingSlot(@PathVariable Long id) {
        try {
            deleteTrainingSlotService.deleteTrainingSlot(id);
            return ResponseEntity.ok().build();
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(Map.of("message", "Access denied: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Unexpected error: " + e.getMessage()));
        }
    }
}


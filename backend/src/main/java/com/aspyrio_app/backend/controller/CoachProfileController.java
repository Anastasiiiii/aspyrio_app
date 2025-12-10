package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.model.CoachProfile;
import com.aspyrio_app.backend.service.coachProfile.CoachProfileService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Data
class ErrorResponse {
    private final String message;
}

@AllArgsConstructor
@RestController
@RequestMapping("/api/coach-profile")
public class CoachProfileController {
    private final CoachProfileService coachProfileService;

    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            CoachProfile profile = coachProfileService.uploadPhoto(file);
            return ResponseEntity.ok(profile);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ErrorResponse("Access denied: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(new ErrorResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Failed to upload photo: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<CoachProfile> getProfile() {
        try {
            CoachProfile profile = coachProfileService.getProfile();
            if (profile == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(profile);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping
    public ResponseEntity<?> updateProfile(@RequestBody CoachProfile profile) {
        try {
            CoachProfile updatedProfile = coachProfileService.updateProfile(profile);
            return ResponseEntity.ok(updatedProfile);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ErrorResponse("Access denied: " + e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }
}


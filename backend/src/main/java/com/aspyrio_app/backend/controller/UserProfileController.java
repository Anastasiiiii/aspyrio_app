package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.UpdateUserProfileRequest;
import com.aspyrio_app.backend.dto.UserProfileResponse;
import com.aspyrio_app.backend.service.userProfile.UserProfileService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Data
class UserProfileErrorResponse {
    private final String message;
}

@AllArgsConstructor
@RestController
@RequestMapping("/api/user-profile")
public class UserProfileController {
    private final UserProfileService userProfileService;

    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadPhoto(@RequestParam("file") MultipartFile file) {
        try {
            UserProfileResponse profile = userProfileService.uploadPhoto(file);
            return ResponseEntity.ok(profile);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(new UserProfileErrorResponse("Access denied: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(new UserProfileErrorResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new UserProfileErrorResponse("Failed to upload photo: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new UserProfileErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<UserProfileResponse> getProfile() {
        UserProfileResponse profile = userProfileService.getProfile();
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfileResponse> updateProfile(@RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateProfile(request));
    }
}


package com.aspyrio_app.backend.service.coachProfile;

import com.aspyrio_app.backend.model.CoachProfile;
import com.aspyrio_app.backend.model.Sports;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.CoachProfileRepository;
import com.aspyrio_app.backend.repository.SportsRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import com.aspyrio_app.backend.service.s3.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CoachProfileService {
    private final CoachProfileRepository coachProfileRepository;
    private final UserRepository userRepository;
    private final SportsRepository sportsRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    
    @Autowired(required = false)
    private S3FileUploadService s3FileUploadService;

    @Transactional
    public CoachProfile uploadPhoto(MultipartFile file) throws IOException {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CoachProfile profile = coachProfileRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    CoachProfile newProfile = new CoachProfile();
                    newProfile.setUser(currentUser);
                    newProfile.setFirstName("Coach");
                    newProfile.setLastName(currentUser.getUsername());
                    newProfile.setTrainingFormat(com.aspyrio_app.backend.model.TrainingType.OFFLINE);
                    return newProfile;
                });

        if (s3FileUploadService == null) {
            throw new IllegalStateException(
                "File upload is not available. Please configure AWS S3 credentials. " +
                "See AWS_SETUP.md for instructions."
            );
        }

        if (profile.getPhotoUrl() != null && !profile.getPhotoUrl().isEmpty()) {
            s3FileUploadService.deleteFile(profile.getPhotoUrl());
        }

        String photoKey = s3FileUploadService.uploadFile(file, "coach-photos");
        profile.setPhotoUrl(photoKey);
        
        CoachProfile savedProfile = coachProfileRepository.save(profile);

        if (s3FileUploadService != null) {
            try {
                String presignedUrl = s3FileUploadService.getPresignedUrl(photoKey);
                if (presignedUrl != null) {
                    CoachProfile responseProfile = new CoachProfile();
                    responseProfile.setId(savedProfile.getId());
                    responseProfile.setUser(savedProfile.getUser());
                    responseProfile.setFirstName(savedProfile.getFirstName());
                    responseProfile.setLastName(savedProfile.getLastName());
                    responseProfile.setBirthDate(savedProfile.getBirthDate());
                    responseProfile.setCity(savedProfile.getCity());
                    responseProfile.setTrainingFormat(savedProfile.getTrainingFormat());
                    responseProfile.setDescription(savedProfile.getDescription());
                    responseProfile.setAchievements(savedProfile.getAchievements());
                    responseProfile.setSports(new java.util.HashSet<>(savedProfile.getSports()));
                    responseProfile.setPhotoUrl(presignedUrl);
                    responseProfile.setCreatedAt(savedProfile.getCreatedAt());
                    responseProfile.setUpdatedAt(savedProfile.getUpdatedAt());
                    return responseProfile;
                }
            } catch (Exception e) {
                System.err.println("Failed to generate presigned URL for uploaded photo: " + e.getMessage());
            }
        }

        return savedProfile;
    }

    @Transactional(readOnly = true)
    public CoachProfile getProfile() {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CoachProfile profile = coachProfileRepository.findByUser(currentUser)
                .orElse(null);
        
        if (profile != null && profile.getPhotoUrl() != null && !profile.getPhotoUrl().isEmpty() && s3FileUploadService != null) {
            try {
                String photoKey = profile.getPhotoUrl();
                
                if (photoKey.startsWith("http")) {
                    String extractedKey = s3FileUploadService.extractKeyFromUrl(photoKey);
                    if (extractedKey != null && !extractedKey.equals(photoKey)) {
                        profile.setPhotoUrl(extractedKey);
                        coachProfileRepository.save(profile);
                        photoKey = extractedKey;
                    } else {
                        System.err.println("Warning: Could not extract S3 key from photoUrl, skipping presigned URL generation");
                        return profile;
                    }
                }
                
                String presignedUrl = s3FileUploadService.getPresignedUrl(photoKey);
                if (presignedUrl != null) {
                    CoachProfile responseProfile = new CoachProfile();
                    responseProfile.setId(profile.getId());
                    responseProfile.setUser(profile.getUser());
                    responseProfile.setFirstName(profile.getFirstName());
                    responseProfile.setLastName(profile.getLastName());
                    responseProfile.setBirthDate(profile.getBirthDate());
                    responseProfile.setCity(profile.getCity());
                    responseProfile.setTrainingFormat(profile.getTrainingFormat());
                    responseProfile.setDescription(profile.getDescription());
                    responseProfile.setAchievements(profile.getAchievements());
                    responseProfile.setSports(new java.util.HashSet<>(profile.getSports()));
                    responseProfile.setPhotoUrl(presignedUrl);
                    responseProfile.setCreatedAt(profile.getCreatedAt());
                    responseProfile.setUpdatedAt(profile.getUpdatedAt());
                    return responseProfile;
                }
            } catch (Exception e) {
                System.err.println("Failed to generate presigned URL for profile photo: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        return profile;
    }

    @Transactional
    public CoachProfile updateProfile(CoachProfile updatedProfile) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_COACH");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CoachProfile profile = coachProfileRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    CoachProfile newProfile = new CoachProfile();
                    newProfile.setUser(currentUser);
                    return newProfile;
                });

        boolean updatingFirstName = updatedProfile.getFirstName() != null && !updatedProfile.getFirstName().trim().isEmpty();
        boolean updatingLastName = updatedProfile.getLastName() != null && !updatedProfile.getLastName().trim().isEmpty();
        boolean updatingTrainingFormat = updatedProfile.getTrainingFormat() != null;

        if (updatingFirstName) {
            profile.setFirstName(updatedProfile.getFirstName().trim());
        }
        if (updatingLastName) {
            profile.setLastName(updatedProfile.getLastName().trim());
        }
        if (updatedProfile.getBirthDate() != null) {
            profile.setBirthDate(updatedProfile.getBirthDate());
        }
        if (updatedProfile.getCity() != null) {
            profile.setCity(updatedProfile.getCity().trim());
        }
        if (updatingTrainingFormat) {
            profile.setTrainingFormat(updatedProfile.getTrainingFormat());
        }
        if (updatedProfile.getDescription() != null) {
            profile.setDescription(updatedProfile.getDescription().trim());
        }
        if (updatedProfile.getAchievements() != null) {
            profile.setAchievements(updatedProfile.getAchievements().trim());
        }
        if (updatedProfile.getSports() != null && !updatedProfile.getSports().isEmpty()) {
            java.util.Set<Sports> sportsSet = new java.util.HashSet<>();
            for (Sports sport : updatedProfile.getSports()) {
                if (sport != null && sport.getId() != null) {
                    Sports loadedSport = sportsRepository.findById(sport.getId())
                            .orElseThrow(() -> new RuntimeException("Sport not found with id: " + sport.getId()));
                    sportsSet.add(loadedSport);
                }
            }
            profile.setSports(sportsSet);
        } else if (updatedProfile.getSports() != null && updatedProfile.getSports().isEmpty()) {
            profile.setSports(new java.util.HashSet<>());
        }

        if (profile.getId() == null) {
            if (profile.getFirstName() == null || profile.getFirstName().trim().isEmpty()) {
                throw new RuntimeException("First name is required");
            }
            if (profile.getLastName() == null || profile.getLastName().trim().isEmpty()) {
                throw new RuntimeException("Last name is required");
            }
            if (profile.getTrainingFormat() == null) {
                throw new RuntimeException("Training format is required");
            }
        } else {
            if (updatingFirstName && (profile.getFirstName() == null || profile.getFirstName().trim().isEmpty())) {
                throw new RuntimeException("First name cannot be empty");
            }
            if (updatingLastName && (profile.getLastName() == null || profile.getLastName().trim().isEmpty())) {
                throw new RuntimeException("Last name cannot be empty");
            }
            if (updatingTrainingFormat && profile.getTrainingFormat() == null) {
                throw new RuntimeException("Training format cannot be null");
            }
        }

        CoachProfile savedProfile = coachProfileRepository.save(profile);
        
        if (savedProfile.getPhotoUrl() != null && !savedProfile.getPhotoUrl().isEmpty() && s3FileUploadService != null) {
            try {
                String photoKey = savedProfile.getPhotoUrl();
                
                if (photoKey.startsWith("http")) {
                    String extractedKey = s3FileUploadService.extractKeyFromUrl(photoKey);
                    if (extractedKey != null && !extractedKey.equals(photoKey)) {
                        savedProfile.setPhotoUrl(extractedKey);
                        savedProfile = coachProfileRepository.save(savedProfile);
                        photoKey = extractedKey;
                    } else {
                        System.err.println("Warning: Could not extract S3 key from photoUrl, skipping presigned URL generation");
                        return savedProfile;
                    }
                }
                
                String presignedUrl = s3FileUploadService.getPresignedUrl(photoKey);
                if (presignedUrl != null) {
                    CoachProfile responseProfile = new CoachProfile();
                    responseProfile.setId(savedProfile.getId());
                    responseProfile.setUser(savedProfile.getUser());
                    responseProfile.setFirstName(savedProfile.getFirstName());
                    responseProfile.setLastName(savedProfile.getLastName());
                    responseProfile.setBirthDate(savedProfile.getBirthDate());
                    responseProfile.setCity(savedProfile.getCity());
                    responseProfile.setTrainingFormat(savedProfile.getTrainingFormat());
                    responseProfile.setDescription(savedProfile.getDescription());
                    responseProfile.setAchievements(savedProfile.getAchievements());
                    responseProfile.setSports(new java.util.HashSet<>(savedProfile.getSports()));
                    responseProfile.setPhotoUrl(presignedUrl);
                    responseProfile.setCreatedAt(savedProfile.getCreatedAt());
                    responseProfile.setUpdatedAt(savedProfile.getUpdatedAt());
                    return responseProfile;
                }
            } catch (Exception e) {
                System.err.println("Failed to generate presigned URL for profile photo: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return savedProfile;
    }
}


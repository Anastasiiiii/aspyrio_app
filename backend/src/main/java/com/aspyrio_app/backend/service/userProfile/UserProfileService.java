package com.aspyrio_app.backend.service.userProfile;

import com.aspyrio_app.backend.dto.UpdateUserProfileRequest;
import com.aspyrio_app.backend.dto.UserProfileResponse;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.model.UserProfile;
import com.aspyrio_app.backend.repository.UserProfileRepository;
import com.aspyrio_app.backend.repository.UserRepository;
import com.aspyrio_app.backend.security.AuthValidator;
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
public class UserProfileService {
    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    
    @Autowired(required = false)
    private S3FileUploadService s3FileUploadService;

    @Transactional
    public UserProfileResponse uploadPhoto(MultipartFile file) throws IOException {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(currentUser)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUser(currentUser);
                    newProfile.setFirstName("User");
                    newProfile.setLastName(currentUser.getUsername());
                    return newProfile;
                });

        if (s3FileUploadService == null) {
            throw new IllegalStateException(
                "File upload is not available. Please configure AWS S3 credentials. " +
                "See AWS_SETUP.md for instructions."
            );
        }

        if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
            s3FileUploadService.deleteFile(profile.getImageUrl());
        }

        String photoKey = s3FileUploadService.uploadFile(file, "user-photos");
        profile.setImageUrl(photoKey);
        
        UserProfile savedProfile = userProfileRepository.save(profile);

        if (s3FileUploadService != null) {
            try {
                String presignedUrl = s3FileUploadService.getPresignedUrl(photoKey);
                if (presignedUrl != null) {
                    UserProfileResponse response = mapToResponse(savedProfile);
                    response.setImageUrl(presignedUrl);
                    return response;
                }
            } catch (Exception e) {
                System.err.println("Failed to generate presigned URL for uploaded photo: " + e.getMessage());
            }
        }

        return mapToResponse(savedProfile);
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile() {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(currentUser)
                .orElse(null);

        if (profile == null) {
            return null;
        }

        UserProfileResponse response = mapToResponse(profile);
        
        if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty() && s3FileUploadService != null) {
            try {
                String photoKey = profile.getImageUrl();
                
                if (photoKey.startsWith("http")) {
                    String extractedKey = s3FileUploadService.extractKeyFromUrl(photoKey);
                    if (extractedKey != null && !extractedKey.equals(photoKey)) {
                        profile.setImageUrl(extractedKey);
                        userProfileRepository.save(profile);
                        photoKey = extractedKey;
                    } else {
                        System.err.println("Warning: Could not extract S3 key from imageUrl, skipping presigned URL generation");
                        return response;
                    }
                }
                
                String presignedUrl = s3FileUploadService.getPresignedUrl(photoKey);
                if (presignedUrl != null) {
                    response.setImageUrl(presignedUrl);
                }
            } catch (Exception e) {
                System.err.println("Failed to generate presigned URL for photo: " + e.getMessage());
            }
        }
        
        return response;
    }

    @Transactional
    public UserProfileResponse updateProfile(UpdateUserProfileRequest request) {
        authValidator.ensureAuthenticated();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        UserProfile profile = userProfileRepository.findByUser(currentUser)
                .orElse(null);

        if (profile == null) {
            profile = new UserProfile();
            profile.setUser(currentUser);
        }

        if (request.getFirstName() != null) {
            profile.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            profile.setLastName(request.getLastName());
        }
        if (request.getBirthDate() != null) {
            profile.setBirthDate(request.getBirthDate());
        }
        if (request.getCity() != null) {
            profile.setCity(request.getCity());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }
        if (request.getWeight() != null) {
            profile.setWeight(request.getWeight());
        }
        if (request.getHeight() != null) {
            profile.setHeight(request.getHeight());
        }
        if (request.getGoal() != null) {
            profile.setGoal(request.getGoal());
        }
        if (request.getTargetWeight() != null) {
            profile.setTargetWeight(request.getTargetWeight());
        }
        if (request.getImageUrl() != null) {
            profile.setImageUrl(request.getImageUrl());
        }

        UserProfile savedProfile = userProfileRepository.save(profile);
        UserProfileResponse response = mapToResponse(savedProfile);
        
        if (savedProfile.getImageUrl() != null && !savedProfile.getImageUrl().isEmpty() && s3FileUploadService != null) {
            try {
                String photoKey = savedProfile.getImageUrl();
                
                if (photoKey.startsWith("http")) {
                    String extractedKey = s3FileUploadService.extractKeyFromUrl(photoKey);
                    if (extractedKey != null && !extractedKey.equals(photoKey)) {
                        savedProfile.setImageUrl(extractedKey);
                        userProfileRepository.save(savedProfile);
                        photoKey = extractedKey;
                    } else {
                        System.err.println("Warning: Could not extract S3 key from imageUrl, skipping presigned URL generation");
                        return response;
                    }
                }
                
                String presignedUrl = s3FileUploadService.getPresignedUrl(photoKey);
                if (presignedUrl != null) {
                    response.setImageUrl(presignedUrl);
                }
            } catch (Exception e) {
                System.err.println("Failed to generate presigned URL for photo: " + e.getMessage());
            }
        }
        
        return response;
    }

    private UserProfileResponse mapToResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUser().getId());
        response.setFirstName(profile.getFirstName());
        response.setLastName(profile.getLastName());
        response.setBirthDate(profile.getBirthDate());
        response.setCity(profile.getCity());
        response.setCountry(profile.getCountry());
        response.setWeight(profile.getWeight());
        response.setHeight(profile.getHeight());
        response.setGoal(profile.getGoal());
        response.setTargetWeight(profile.getTargetWeight());
        response.setImageUrl(profile.getImageUrl());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
}


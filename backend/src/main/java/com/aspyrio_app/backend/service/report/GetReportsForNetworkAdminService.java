package com.aspyrio_app.backend.service.report;

import com.aspyrio_app.backend.dto.ReportResponse;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.FitnessCenterNetwork;
import com.aspyrio_app.backend.model.Report;
import com.aspyrio_app.backend.model.User;
import com.aspyrio_app.backend.repository.FitnessCenterNetworkRepository;
import com.aspyrio_app.backend.repository.ReportRepository;
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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetReportsForNetworkAdminService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FitnessCenterNetworkRepository fitnessCenterNetworkRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    
    @Autowired(required = false)
    private S3FileUploadService s3FileUploadService;

    @Transactional(readOnly = true)
    public List<ReportResponse> getReports() {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_NETWORK_ADMIN");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenterNetwork network = fitnessCenterNetworkRepository.findByNetworkAdminId(currentUser)
                .orElseThrow(() -> new RuntimeException("Network not found for this admin"));

        List<FitnessCenter> fitnessCenters = network.getCenters();
        
        if (fitnessCenters == null || fitnessCenters.isEmpty()) {
            return List.of();
        }

        List<Report> reports = fitnessCenters.stream()
                .flatMap(center -> reportRepository.findByFitnessCenter(center).stream())
                .collect(Collectors.toList());

        return reports.stream()
                .map(report -> {
                    ReportResponse response = mapToResponse(report);
                    
                    if (s3FileUploadService != null && report.getFileUrl() != null && !report.getFileUrl().isEmpty()) {
                        try {
                            String fileKey = report.getFileUrl();
                            
                            if (fileKey.startsWith("http")) {
                                String extractedKey = s3FileUploadService.extractKeyFromUrl(fileKey);
                                if (extractedKey != null && !extractedKey.equals(fileKey)) {
                                    fileKey = extractedKey;
                                } else {
                                    return response;
                                }
                            }
                            
                            String presignedUrl = s3FileUploadService.getPresignedUrl(fileKey);
                            if (presignedUrl != null) {
                                response.setFileUrl(presignedUrl);
                            }
                        } catch (Exception e) {
                            System.err.println("Failed to generate presigned URL for report: " + e.getMessage());
                        }
                    }
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    private ReportResponse mapToResponse(Report report) {
        ReportResponse response = new ReportResponse();
        response.setId(report.getId());
        response.setUserId(report.getUser().getId());
        response.setUserName(report.getUser().getUsername());
        response.setUserEmail(report.getUser().getEmail());
        response.setFitnessCenterId(report.getFitnessCenter().getId());
        response.setFitnessCenterName(report.getFitnessCenter().getName());
        response.setFileUrl(report.getFileUrl());
        response.setFileName(report.getFileName());
        response.setCreatedAt(report.getCreatedAt());
        response.setUpdatedAt(report.getUpdatedAt());
        return response;
    }
}


package com.aspyrio_app.backend.service.report;

import com.aspyrio_app.backend.dto.ReportResponse;
import com.aspyrio_app.backend.model.FitnessCenter;
import com.aspyrio_app.backend.model.Report;
import com.aspyrio_app.backend.model.User;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UploadReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    
    @Autowired(required = false)
    private S3FileUploadService s3FileUploadService;

    @Transactional
    public ReportResponse uploadReport(MultipartFile file) throws IOException {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_FITNESS_ADMIN");

        if (s3FileUploadService == null) {
            throw new IllegalStateException(
                "File upload is not available. Please configure AWS S3 credentials. " +
                "See AWS_SETUP.md for instructions."
            );
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".txt")) {
            throw new IllegalArgumentException("Only .txt files are allowed");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        FitnessCenter fitnessCenter = currentUser.getCenter();
        if (fitnessCenter == null) {
            throw new RuntimeException("User is not associated with any fitness center");
        }

        String fileKey = s3FileUploadService.uploadFile(file, "reports", false);

        Report report = new Report();
        report.setUser(currentUser);
        report.setFitnessCenter(fitnessCenter);
        report.setFileUrl(fileKey);
        report.setFileName(originalFilename);

        Report savedReport = reportRepository.save(report);

        String presignedUrl = null;
        if (s3FileUploadService != null) {
            try {
                presignedUrl = s3FileUploadService.getPresignedUrl(fileKey);
            } catch (Exception e) {
                System.err.println("Failed to generate presigned URL for report: " + e.getMessage());
            }
        }

        ReportResponse response = mapToResponse(savedReport);
        if (presignedUrl != null) {
            response.setFileUrl(presignedUrl);
        }

        return response;
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


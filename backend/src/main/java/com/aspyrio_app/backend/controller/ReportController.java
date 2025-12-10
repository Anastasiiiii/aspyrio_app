package com.aspyrio_app.backend.controller;

import com.aspyrio_app.backend.dto.ReportResponse;
import com.aspyrio_app.backend.service.report.GetReportsService;
import com.aspyrio_app.backend.service.report.UploadReportService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Data
class ReportErrorResponse {
    private final String message;
}

@AllArgsConstructor
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final UploadReportService uploadReportService;
    private final GetReportsService getReportsService;
    private final com.aspyrio_app.backend.service.report.GetReportsForNetworkAdminService getReportsForNetworkAdminService;
    private final com.aspyrio_app.backend.service.report.SummarizeReportService summarizeReportService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadReport(@RequestParam("file") MultipartFile file) {
        try {
            ReportResponse report = uploadReportService.uploadReport(file);
            return ResponseEntity.ok(report);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ReportErrorResponse("Access denied: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(new ReportErrorResponse(e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new ReportErrorResponse(e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ReportErrorResponse("Failed to upload report: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ReportErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> getReports() {
        List<ReportResponse> reports = getReportsService.getReports();
        return ResponseEntity.ok(reports);
    }

    @GetMapping("/network-admin")
    public ResponseEntity<List<ReportResponse>> getReportsForNetworkAdmin() {
        List<ReportResponse> reports = getReportsForNetworkAdminService.getReports();
        return ResponseEntity.ok(reports);
    }

    @PostMapping("/summarize")
    public ResponseEntity<?> summarizeReport(@RequestBody com.aspyrio_app.backend.dto.SummarizeRequest request) {
        try {
            com.aspyrio_app.backend.dto.SummarizeResponse response = summarizeReportService.summarizeReport(request.getReportId());
            return ResponseEntity.ok(response);
        } catch (org.springframework.security.access.AccessDeniedException e) {
            return ResponseEntity.status(403).body(new ReportErrorResponse("Access denied: " + e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(503).body(new ReportErrorResponse(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body(new ReportErrorResponse("Failed to summarize report: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ReportErrorResponse("Unexpected error: " + e.getMessage()));
        }
    }
}


package com.aspyrio_app.backend.service.report;

import com.aspyrio_app.backend.dto.SummarizeResponse;
import com.aspyrio_app.backend.model.Report;
import com.aspyrio_app.backend.repository.ReportRepository;
import com.aspyrio_app.backend.security.AuthValidator;
import com.aspyrio_app.backend.security.RoleValidator;
import com.aspyrio_app.backend.service.s3.S3FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SummarizeReportService {
    private final ReportRepository reportRepository;
    private final AuthValidator authValidator;
    private final RoleValidator roleValidator;
    
    @Autowired(required = false)
    private S3FileUploadService s3FileUploadService;
    
    @Value("${summarization.api.url:}")
    private String summarizationApiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional(readOnly = true)
    public SummarizeResponse summarizeReport(Long reportId) {
        authValidator.ensureAuthenticated();
        roleValidator.ensureHasRole("ROLE_NETWORK_ADMIN");

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (s3FileUploadService == null) {
            throw new IllegalStateException("S3 service is not available");
        }

        try {
            String fileKey = report.getFileUrl();
            if (fileKey.startsWith("http")) {
                String extractedKey = s3FileUploadService.extractKeyFromUrl(fileKey);
                if (extractedKey != null && !extractedKey.equals(fileKey)) {
                    fileKey = extractedKey;
                }
            }
            
            String presignedUrl = s3FileUploadService.getPresignedUrl(fileKey);

            String fileContent = readFileFromUrl(presignedUrl);
            String summary = callSummarizationAPI(fileContent);
            
            return new SummarizeResponse(summary, fileContent);
        } catch (Exception e) {
            throw new RuntimeException("Failed to summarize report: " + e.getMessage(), e);
        }
    }

    private String readFileFromUrl(String url) throws Exception {
        try (InputStream inputStream = new URL(url).openStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString().trim();
        }
    }

    private String callSummarizationAPI(String text) {
        if (summarizationApiUrl == null || summarizationApiUrl.isEmpty()) {
            return text.length() > 500
                ? text.substring(0, 500) + "..."
                : text;
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("text", text);
            
            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    summarizationApiUrl,
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                if (body.containsKey("summary")) {
                    return (String) body.get("summary");
                } else if (body.containsKey("result")) {
                    return (String) body.get("result");
                } else if (body.containsKey("text")) {
                    return (String) body.get("text");
                } else {
                    return body.toString();
                }
            }
        } catch (Exception e) {
            System.err.println("Error calling summarization API: " + e.getMessage());
        }
        
        return text.length() > 500
            ? text.substring(0, 500) + "..."
            : text;
    }
}


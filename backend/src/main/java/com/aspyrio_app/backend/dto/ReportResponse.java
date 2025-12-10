package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long fitnessCenterId;
    private String fitnessCenterName;
    private String fileUrl;
    private String fileName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


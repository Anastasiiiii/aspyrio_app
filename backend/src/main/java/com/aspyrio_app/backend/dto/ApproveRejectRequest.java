package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApproveRejectRequest {
    private Long requestId;
    private String action; // "APPROVE" or "REJECT"
}



package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassResponse {
    private UUID id;
    private Long userId;
    private String token;
    private LocalDateTime expiresAt;
    private Boolean used;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}



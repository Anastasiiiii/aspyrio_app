package com.aspyrio_app.backend.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class ChangePasswordRequest {
    private String currentPassword;
    private String newPassword;
}

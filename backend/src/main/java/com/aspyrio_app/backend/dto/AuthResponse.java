package com.aspyrio_app.backend.dto;
import com.aspyrio_app.backend.model.Role;
import lombok.*;

@AllArgsConstructor
@Getter
public class AuthResponse {
    private String token;
    private Role role;
}

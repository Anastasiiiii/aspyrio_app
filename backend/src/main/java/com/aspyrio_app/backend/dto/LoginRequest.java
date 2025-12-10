package com.aspyrio_app.backend.dto;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
}

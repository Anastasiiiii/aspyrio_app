package com.aspyrio_app.backend.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}

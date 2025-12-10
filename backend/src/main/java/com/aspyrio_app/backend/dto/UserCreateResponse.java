package com.aspyrio_app.backend.dto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserCreateResponse {
    private String username;
    private String password;
}


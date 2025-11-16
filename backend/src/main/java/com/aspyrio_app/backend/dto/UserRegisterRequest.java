package com.aspyrio_app.backend.dto;
import com.aspyrio_app.backend.model.Role;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class UserRegisterRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
}
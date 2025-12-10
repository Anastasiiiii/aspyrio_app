package com.aspyrio_app.backend.dto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class FitnessAdminRegisterRequest {
    private String username;
    private String email;
    private Long fitnessCenterId;
}


package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachWithSportsResponse {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<SportInfo> sports;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SportInfo {
        private Long id;
        private String name;
    }
}



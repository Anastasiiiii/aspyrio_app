package com.aspyrio_app.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequest {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String city;
    private String country;
    private BigDecimal weight;
    private BigDecimal height;
    private String goal;
    private BigDecimal targetWeight;
    private String imageUrl;
}



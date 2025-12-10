package com.aspyrio_app.backend.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public class FitnessCenterRegisterRequest {
    private String name;
    private String address;
    private String city;
    private String country;
    private String postalCode;
}

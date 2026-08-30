package com.skillbridge.college.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCollegeProfileRequest {

    @NotBlank(message = "College name is required")
    private String name;

    private String address;
    private String website;
    private String contactEmail;
    private String contactPhone;
}

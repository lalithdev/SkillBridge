package com.skillbridge.company.dto;

import com.skillbridge.common.entity.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProfileDto {

    private Long id;
    private Long userId;
    private String name;
    private String industry;
    private String description;
    private String location;
    private String website;
    private String contactEmail;
    private String contactPhone;
    private VerificationStatus verificationStatus;
}

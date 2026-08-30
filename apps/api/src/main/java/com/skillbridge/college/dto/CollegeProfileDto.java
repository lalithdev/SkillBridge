package com.skillbridge.college.dto;

import com.skillbridge.college.entity.College;
import com.skillbridge.common.entity.VerificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollegeProfileDto {

    private Long id;
    private Long userId;
    private String name;
    private String address;
    private String website;
    private String contactEmail;
    private String contactPhone;
    private VerificationStatus verificationStatus;

    public static CollegeProfileDto from(College college) {
        if (college == null) {
            return null;
        }
        return CollegeProfileDto.builder()
                .id(college.getId())
                .userId(college.getUserId())
                .name(college.getName())
                .address(college.getAddress())
                .website(college.getWebsite())
                .contactEmail(college.getContactEmail())
                .contactPhone(college.getContactPhone())
                .verificationStatus(college.getVerificationStatus())
                .build();
    }
}

package com.skillbridge.application.dto;

import com.skillbridge.application.entity.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyApplicantDto {

    private Long applicationId;
    private Long studentProfileId;
    private String studentName;
    private String departmentName;
    private Short yearOfStudy;
    private BigDecimal cgpa;
    private ApplicationStatus status;
    private BigDecimal matchPercentAtApply;
    private Boolean hasResume;
    private Instant appliedAt;
}

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
public class ApplicationDetailDto {

    private Long id;
    private Long studentProfileId;
    private String studentName;
    private String studentEmail;
    private String departmentName;
    private BigDecimal cgpa;
    private Long opportunityId;
    private String opportunityTitle;
    private String companyName;
    private ApplicationStatus status;
    private BigDecimal matchPercentAtApply;
    private Instant appliedAt;
    private String resumeDownloadUrl;
}

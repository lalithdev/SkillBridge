package com.skillbridge.application.dto;

import com.skillbridge.application.entity.ApplicationStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
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
public class StudentApplicationDto {

    private Long id;
    private Long opportunityId;
    private String opportunityTitle;
    private OpportunityType opportunityType;
    private String companyName;
    private ApplicationStatus status;
    private BigDecimal matchPercentAtApply;
    private Instant appliedAt;
}

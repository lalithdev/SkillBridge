package com.skillbridge.application.dto;

import com.skillbridge.application.entity.Application;
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
public class ApplicationDto {

    private Long id;
    private Long studentProfileId;
    private Long opportunityId;
    private ApplicationStatus status;
    private BigDecimal matchPercentAtApply;
    private Instant appliedAt;
    private Instant updatedAt;

    public static ApplicationDto from(Application application) {
        if (application == null) {
            return null;
        }
        return ApplicationDto.builder()
                .id(application.getId())
                .studentProfileId(application.getStudentProfileId())
                .opportunityId(application.getOpportunityId())
                .status(application.getStatus())
                .matchPercentAtApply(application.getMatchPercentAtApply())
                .appliedAt(application.getAppliedAt())
                .updatedAt(application.getUpdatedAt())
                .build();
    }
}

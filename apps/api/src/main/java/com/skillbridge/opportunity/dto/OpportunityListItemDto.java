package com.skillbridge.opportunity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.skill.dto.SkillDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Summary DTO for paginated opportunity listings (GET /opportunities, GET /opportunities/company/my).
 * When returned to a student session, matchPercent and isEligible are populated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityListItemDto {

    private Long id;
    private Long companyId;
    private String companyName;
    private String companyLocation;
    private VerificationStatus companyVerificationStatus;
    private String title;
    private OpportunityType type;
    private OpportunityMode mode;
    private BigDecimal stipendAmount;
    private LocalDate applicationDeadline;
    private OpportunityStatus status;
    private List<SkillDto> requiredSkills;

    /** Populated only for STUDENT sessions; null for other roles. */
    private Double matchPercent;

    /** Populated only for STUDENT sessions; null for other roles. */
    @JsonProperty("isEligible")
    private Boolean eligible;
}

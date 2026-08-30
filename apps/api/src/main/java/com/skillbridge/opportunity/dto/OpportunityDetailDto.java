package com.skillbridge.opportunity.dto;

import com.skillbridge.college.dto.DepartmentDto;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.matching.dto.MatchResultDto;
import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.skill.dto.SkillDto;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Full detail DTO for GET /opportunities/{id}.
 * Includes required skills, eligible departments/years.
 * When returned to a student session, matchBreakdown is populated.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityDetailDto {

    private Long id;
    private Long companyId;
    private String companyName;
    private String companyWebsite;
    private VerificationStatus companyVerificationStatus;
    private String title;
    private String description;
    private OpportunityType type;
    private String location;
    private OpportunityMode mode;
    private Integer durationWeeks;
    private BigDecimal stipendAmount;
    private String stipendCurrency;
    private BigDecimal minCgpa;
    private LocalDate applicationDeadline;
    private OpportunityStatus status;
    private List<SkillDto> requiredSkills;
    private List<DepartmentDto> eligibleDepartments;
    private List<Integer> eligibleYears;

    /** Populated only for STUDENT sessions; null for other roles. */
    private MatchResultDto matchBreakdown;
}

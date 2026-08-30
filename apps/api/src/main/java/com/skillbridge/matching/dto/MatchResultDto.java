package com.skillbridge.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skillbridge.skill.dto.SkillDto;
import lombok.*;

import java.util.List;

/**
 * Result of a skill match computation.
 * Per OpenAPI and architecture doc:
 *   matchPercent = (matchedSkills.size / requiredSkills.size) * 100
 *   If requiredSkills is empty, matchPercent = 0.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultDto {

    private List<SkillDto> matchedSkills;
    private List<SkillDto> missingSkills;

    /** Coverage percentage: matched / total-required * 100, 0 if no required skills. */
    private double matchPercent;

    @JsonProperty("isEligible")
    private boolean eligible;

    /** Reasons why student is not eligible (empty if eligible). */
    private List<String> ineligibilityReasons;
}

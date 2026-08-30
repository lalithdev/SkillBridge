package com.skillbridge.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDemandDto {

    private Long skillId;
    private String skillName;
    private String category;
    private BigDecimal demandPercent;
    private Integer opportunityCount;
}

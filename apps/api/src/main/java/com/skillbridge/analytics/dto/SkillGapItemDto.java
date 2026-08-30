package com.skillbridge.analytics.dto;

import com.skillbridge.analytics.entity.GapSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillGapItemDto {

    private Long skillId;
    private String skillName;
    private String category;
    private BigDecimal demandPercent;
    private BigDecimal availabilityPercent;
    private BigDecimal gapPercent;
    private GapSeverity severity;
}

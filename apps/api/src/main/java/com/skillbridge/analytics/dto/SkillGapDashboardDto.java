package com.skillbridge.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillGapDashboardDto {

    private Long collegeId;
    private Integer totalStudents;
    private Integer totalOpenOpportunities;
    private List<SkillGapItemDto> gaps;
}

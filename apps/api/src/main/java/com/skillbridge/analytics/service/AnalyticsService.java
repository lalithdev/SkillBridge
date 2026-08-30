package com.skillbridge.analytics.service;

import com.skillbridge.analytics.dto.PlacementFunnelDto;
import com.skillbridge.analytics.dto.SkillAvailabilityDto;
import com.skillbridge.analytics.dto.SkillDemandDto;
import com.skillbridge.analytics.dto.SkillGapDashboardDto;
import com.skillbridge.analytics.entity.GapSeverity;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.opportunity.entity.OpportunityType;

import java.util.List;

public interface AnalyticsService {

    List<SkillAvailabilityDto> getSkillAvailability(
            Long collegeIdOverride,
            Long departmentId,
            CustomUserDetails currentUser
    );

    List<SkillDemandDto> getSkillDemand(
            OpportunityType type,
            CustomUserDetails currentUser
    );

    SkillGapDashboardDto getSkillGapDashboard(
            Long collegeIdOverride,
            GapSeverity severity,
            CustomUserDetails currentUser
    );

    PlacementFunnelDto getPlacementFunnel(
            Long collegeIdOverride,
            Long departmentId,
            CustomUserDetails currentUser
    );
}

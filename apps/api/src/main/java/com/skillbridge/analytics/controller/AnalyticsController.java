package com.skillbridge.analytics.controller;

import com.skillbridge.analytics.dto.PlacementFunnelDto;
import com.skillbridge.analytics.dto.SkillAvailabilityDto;
import com.skillbridge.analytics.dto.SkillDemandDto;
import com.skillbridge.analytics.dto.SkillGapDashboardDto;
import com.skillbridge.analytics.entity.GapSeverity;
import com.skillbridge.analytics.service.AnalyticsService;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.opportunity.entity.OpportunityType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@PreAuthorize("hasAnyRole('COLLEGE', 'ADMIN')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    /**
     * GET /api/v1/analytics/skills/availability
     * College student skill availability %.
     */
    @GetMapping("/skills/availability")
    public ResponseEntity<List<SkillAvailabilityDto>> getSkillAvailability(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long departmentId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<SkillAvailabilityDto> response = analyticsService.getSkillAvailability(
                collegeId, departmentId, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/analytics/skills/demand
     * Industry skill demand % platform-wide.
     */
    @GetMapping("/skills/demand")
    public ResponseEntity<List<SkillDemandDto>> getSkillDemand(
            @RequestParam(required = false) OpportunityType type,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<SkillDemandDto> response = analyticsService.getSkillDemand(type, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/analytics/skills/gap
     * College skill gap dashboard.
     */
    @GetMapping("/skills/gap")
    public ResponseEntity<SkillGapDashboardDto> getSkillGapDashboard(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) GapSeverity severity,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        SkillGapDashboardDto response = analyticsService.getSkillGapDashboard(
                collegeId, severity, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/analytics/placement-funnel
     * College recruitment placement funnel.
     */
    @GetMapping("/placement-funnel")
    public ResponseEntity<PlacementFunnelDto> getPlacementFunnel(
            @RequestParam(required = false) Long collegeId,
            @RequestParam(required = false) Long departmentId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PlacementFunnelDto response = analyticsService.getPlacementFunnel(
                collegeId, departmentId, currentUser);
        return ResponseEntity.ok(response);
    }
}

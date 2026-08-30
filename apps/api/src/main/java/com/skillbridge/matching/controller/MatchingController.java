package com.skillbridge.matching.controller;

import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.matching.dto.MatchResultDto;
import com.skillbridge.matching.service.MatchingService;
import com.skillbridge.opportunity.dto.OpportunityListItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Matching endpoints (Role: STUDENT only).
 * - GET /api/v1/matching/opportunities/{opportunityId}
 * - GET /api/v1/matching/recommendations
 */
@RestController
@RequestMapping("/api/v1/matching")
@PreAuthorize("hasRole('STUDENT')")
public class MatchingController {

    private final MatchingService matchingService;

    public MatchingController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    /**
     * GET /api/v1/matching/opportunities/{opportunityId}
     * Dynamic match evaluation for opportunity for the authenticated student.
     */
    @GetMapping("/opportunities/{opportunityId}")
    public ResponseEntity<MatchResultDto> evaluateOpportunityMatch(
            @PathVariable Long opportunityId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        MatchResultDto result = matchingService.evaluateOpportunityMatch(opportunityId, currentUser);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/v1/matching/recommendations
     * Returns open opportunities ranked by skill match % descending.
     */
    @GetMapping("/recommendations")
    public ResponseEntity<PageResponse<OpportunityListItemDto>> getRecommendations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageResponse<OpportunityListItemDto> response = matchingService.getRecommendations(page, size, currentUser);
        return ResponseEntity.ok(response);
    }
}

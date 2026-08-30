package com.skillbridge.opportunity.controller;

import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.opportunity.dto.*;
import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.opportunity.service.OpportunityService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the /api/v1/opportunities resource.
 *
 * RBAC summary:
 * - POST, PUT, PATCH /company, GET /company/my  → COMPANY only
 * - GET, GET /{id}                               → Any authenticated user
 * - Fine-grained ownership for PUT/PATCH         → @PreAuthorize via SecurityService
 */
@RestController
@RequestMapping("/api/v1/opportunities")
public class OpportunityController {

    private final OpportunityService opportunityService;

    public OpportunityController(OpportunityService opportunityService) {
        this.opportunityService = opportunityService;
    }

    /**
     * POST /api/v1/opportunities
     * Create a new opportunity. Only COMPANY role.
     */
    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<OpportunityDetailDto> createOpportunity(
            @Valid @RequestBody CreateOpportunityRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        OpportunityDetailDto created = opportunityService.createOpportunity(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PUT /api/v1/opportunities/{id}
     * Full update of an opportunity. Only the owning company.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY') and @securityService.isOpportunityOwner(#id, authentication)")
    public ResponseEntity<OpportunityDetailDto> updateOpportunity(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOpportunityRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        OpportunityDetailDto updated = opportunityService.updateOpportunity(id, request, currentUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * PATCH /api/v1/opportunities/{id}/status
     * Toggle opportunity status (OPEN/CLOSED). Only the owning company.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('COMPANY') and @securityService.isOpportunityOwner(#id, authentication)")
    public ResponseEntity<OpportunityDetailDto> updateOpportunityStatus(
            @PathVariable Long id,
            @Valid @RequestBody OpportunityStatusUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        OpportunityDetailDto updated = opportunityService.updateOpportunityStatus(id, request, currentUser);
        return ResponseEntity.ok(updated);
    }

    /**
     * GET /api/v1/opportunities/{id}
     * Get opportunity detail. Any authenticated user.
     * STUDENT session: matchBreakdown populated.
     */
    @GetMapping("/{id}")
    public ResponseEntity<OpportunityDetailDto> getOpportunityById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        OpportunityDetailDto detail = opportunityService.getOpportunityById(id, currentUser);
        return ResponseEntity.ok(detail);
    }

    /**
     * GET /api/v1/opportunities
     * Browse/search all opportunities (defaults to OPEN).
     * STUDENT session: matchPercent and isEligible populated.
     */
    @GetMapping
    public ResponseEntity<PageResponse<OpportunityListItemDto>> searchOpportunities(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) OpportunityType type,
            @RequestParam(required = false) OpportunityMode mode,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Double minCgpa,
            @RequestParam(required = false) OpportunityStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageResponse<OpportunityListItemDto> response = opportunityService.searchOpportunities(
                search, type, mode, departmentId, minCgpa, status, page, size, currentUser);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/opportunities/company/my
     * Company's own opportunities. Only COMPANY role.
     */
    @GetMapping("/company/my")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PageResponse<OpportunityListItemDto>> getCompanyOpportunities(
            @RequestParam(required = false) OpportunityStatus status,
            @RequestParam(required = false) OpportunityType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        PageResponse<OpportunityListItemDto> response = opportunityService.getCompanyOpportunities(
                status, type, page, size, currentUser);
        return ResponseEntity.ok(response);
    }
}

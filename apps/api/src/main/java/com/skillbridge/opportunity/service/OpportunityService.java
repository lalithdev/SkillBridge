package com.skillbridge.opportunity.service;

import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.opportunity.dto.*;
import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;

public interface OpportunityService {

    /**
     * POST /opportunities — Create opportunity. Caller must be COMPANY.
     */
    OpportunityDetailDto createOpportunity(CreateOpportunityRequest request, CustomUserDetails currentUser);

    /**
     * PUT /opportunities/{id} — Full update. Caller must own the opportunity.
     */
    OpportunityDetailDto updateOpportunity(Long id, UpdateOpportunityRequest request, CustomUserDetails currentUser);

    /**
     * PATCH /opportunities/{id}/status — Change status. Caller must own the opportunity.
     */
    OpportunityDetailDto updateOpportunityStatus(Long id, OpportunityStatusUpdateRequest request, CustomUserDetails currentUser);

    /**
     * GET /opportunities/{id} — Get detail. Any authenticated user.
     * If STUDENT, matchBreakdown is populated.
     */
    OpportunityDetailDto getOpportunityById(Long id, CustomUserDetails currentUser);

    /**
     * GET /opportunities — Search/filter/paginate open opportunities.
     * If STUDENT, match % and eligibility flag are populated on each item.
     */
    PageResponse<OpportunityListItemDto> searchOpportunities(
            String search,
            OpportunityType type,
            OpportunityMode mode,
            Long departmentId,
            Double minCgpa,
            OpportunityStatus status,
            int page,
            int size,
            CustomUserDetails currentUser);

    /**
     * GET /opportunities/company/my — Company's own postings.
     * Caller must be COMPANY.
     */
    PageResponse<OpportunityListItemDto> getCompanyOpportunities(
            OpportunityStatus status,
            OpportunityType type,
            int page,
            int size,
            CustomUserDetails currentUser);
}

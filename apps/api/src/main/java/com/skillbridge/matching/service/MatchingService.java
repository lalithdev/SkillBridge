package com.skillbridge.matching.service;

import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.matching.dto.MatchResultDto;
import com.skillbridge.opportunity.dto.OpportunityDetailDto;
import com.skillbridge.opportunity.dto.OpportunityListItemDto;
import com.skillbridge.student.entity.StudentProfile;

import java.util.Set;

/**
 * Pure computation service for skill matching and eligibility evaluation.
 * No database writes. No owned repository.
 * Called by OpportunityService, MatchingController, and ApplicationService.
 */
public interface MatchingService {

    /**
     * Computes match result: matched skills, missing skills, match%, eligibility.
     *
     * @param studentProfileId the student profile ID
     * @param opportunityId    the opportunity ID
     * @return full MatchResultDto
     */
    MatchResultDto computeMatch(Long studentProfileId, Long opportunityId);

    /**
     * Dynamic match evaluation for an opportunity for the authenticated student.
     */
    MatchResultDto evaluateOpportunityMatch(Long opportunityId, CustomUserDetails currentUser);

    /**
     * Returns open opportunities ranked by skill match % descending for the authenticated student.
     */
    PageResponse<OpportunityListItemDto> getRecommendations(int page, int size, CustomUserDetails currentUser);

    /**
     * Populates matchPercent and isEligible on an OpportunityListItemDto
     * for the given student profile (used when browsing opportunity listings).
     */
    void enrichListItem(OpportunityListItemDto item, StudentProfile studentProfile);

    /**
     * Populates matchBreakdown on an OpportunityDetailDto for the given student.
     */
    void enrichDetailItem(OpportunityDetailDto detail, StudentProfile studentProfile);

    /**
     * Computes match percent only (for application snapshot).
     *
     * @param studentSkillIds  set of skill IDs the student has
     * @param requiredSkillIds set of required skill IDs for the opportunity
     * @return match percent (0-100), 0 if requiredSkillIds is empty
     */
    double computeMatchPercent(Set<Long> studentSkillIds, Set<Long> requiredSkillIds);
}

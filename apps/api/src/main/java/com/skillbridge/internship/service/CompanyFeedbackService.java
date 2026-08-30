package com.skillbridge.internship.service;

import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.internship.dto.CollegeFeedbackItemDto;
import com.skillbridge.internship.dto.FeedbackDto;
import com.skillbridge.internship.dto.SubmitFeedbackRequest;

public interface CompanyFeedbackService {

    FeedbackDto submitCompanyFeedback(
            Long internshipId,
            SubmitFeedbackRequest request,
            CustomUserDetails currentUser
    );

    FeedbackDto getInternshipFeedback(
            Long internshipId,
            CustomUserDetails currentUser
    );

    PageResponse<CollegeFeedbackItemDto> getCollegeAggregatedFeedback(
            Long collegeIdOverride,
            int page,
            int size,
            CustomUserDetails currentUser
    );
}

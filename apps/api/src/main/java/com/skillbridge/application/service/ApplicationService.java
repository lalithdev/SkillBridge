package com.skillbridge.application.service;

import com.skillbridge.application.dto.*;
import com.skillbridge.application.entity.ApplicationStatus;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;

public interface ApplicationService {

    ApplicationDto submitApplication(SubmitApplicationRequest request, CustomUserDetails currentUser);

    PageResponse<StudentApplicationDto> getMyApplications(
            ApplicationStatus status,
            int page,
            int size,
            CustomUserDetails currentUser
    );

    ApplicationDetailDto getApplicationById(Long id, CustomUserDetails currentUser);

    PageResponse<CompanyApplicantDto> getOpportunityApplications(
            Long opportunityId,
            ApplicationStatus status,
            Boolean eligibleOnly,
            int page,
            int size,
            CustomUserDetails currentUser
    );

    ApplicationDto updateApplicationStatus(
            Long id,
            UpdateApplicationStatusRequest request,
            CustomUserDetails currentUser
    );
}

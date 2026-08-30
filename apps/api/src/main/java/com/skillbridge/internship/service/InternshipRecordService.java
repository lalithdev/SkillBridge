package com.skillbridge.internship.service;

import com.skillbridge.application.entity.Application;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.internship.dto.InternshipRecordDto;
import com.skillbridge.internship.dto.UpdateInternshipStatusRequest;
import com.skillbridge.internship.entity.InternshipRecord;
import com.skillbridge.internship.entity.InternshipStatus;

import java.util.List;

public interface InternshipRecordService {

    InternshipRecord createInternshipRecord(Application application);

    List<InternshipRecordDto> getMyInternships(CustomUserDetails currentUser);

    PageResponse<InternshipRecordDto> getCompanyInternships(
            InternshipStatus status,
            int page,
            int size,
            CustomUserDetails currentUser
    );

    InternshipRecordDto getInternshipById(Long id, CustomUserDetails currentUser);

    InternshipRecordDto updateInternshipStatus(
            Long id,
            UpdateInternshipStatusRequest request,
            CustomUserDetails currentUser
    );
}

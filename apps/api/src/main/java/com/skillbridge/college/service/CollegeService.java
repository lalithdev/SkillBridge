package com.skillbridge.college.service;

import com.skillbridge.college.dto.CollegeDepartmentSummaryDto;
import com.skillbridge.college.dto.CollegeProfileDto;
import com.skillbridge.college.dto.StudentSummaryDto;
import com.skillbridge.college.dto.UpdateCollegeProfileRequest;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.security.CustomUserDetails;

import java.util.List;

public interface CollegeService {

    CollegeProfileDto getCollegeProfile(CustomUserDetails currentUser);

    CollegeProfileDto updateCollegeProfile(UpdateCollegeProfileRequest request, CustomUserDetails currentUser);

    PageResponse<StudentSummaryDto> getCollegeStudents(
            Long collegeIdOverride,
            Long departmentId,
            Short yearOfStudy,
            String search,
            int page,
            int size,
            CustomUserDetails currentUser
    );

    List<CollegeDepartmentSummaryDto> getCollegeDepartmentBreakdown(
            Long collegeIdOverride,
            CustomUserDetails currentUser
    );
}

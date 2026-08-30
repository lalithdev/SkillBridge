package com.skillbridge.admin.service;

import com.skillbridge.admin.dto.*;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.opportunity.dto.OpportunityDetailDto;
import com.skillbridge.user.entity.Role;

import java.util.List;

public interface AdminService {

    PageResponse<AdminUserDto> listUsers(Role role, Boolean isActive, String search, int page, int size);

    AdminUserDto updateUserStatus(Long id, UpdateUserStatusRequest request);

    List<VerificationItemDto> getPendingVerifications(String type, VerificationStatus status);

    VerificationItemDto updateVerificationStatus(String type, Long id, UpdateVerificationStatusRequest request);

    OpportunityDetailDto moderateOpportunity(Long id, ModerateOpportunityRequest request);
}

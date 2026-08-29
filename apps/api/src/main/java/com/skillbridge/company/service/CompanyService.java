package com.skillbridge.company.service;

import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.dto.CompanyProfileDto;
import com.skillbridge.company.dto.UpdateCompanyProfileRequest;

public interface CompanyService {

    CompanyProfileDto getCompanyProfile(CustomUserDetails user);

    CompanyProfileDto updateCompanyProfile(CustomUserDetails user, UpdateCompanyProfileRequest request);

    CompanyProfileDto getCompanyById(Long companyId);
}

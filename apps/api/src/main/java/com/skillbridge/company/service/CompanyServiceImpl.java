package com.skillbridge.company.service;

import com.skillbridge.common.exception.ForbiddenException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.dto.CompanyProfileDto;
import com.skillbridge.company.dto.UpdateCompanyProfileRequest;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyProfileRepository companyProfileRepository;

    public CompanyServiceImpl(CompanyProfileRepository companyProfileRepository) {
        this.companyProfileRepository = companyProfileRepository;
    }

    private CompanyProfile getCompanyProfileFromUser(CustomUserDetails user) {
        if (user == null || user.getUserId() == null) {
            throw new ForbiddenException("Authentication required");
        }
        return companyProfileRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found for user: " + user.getEmail()));
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileDto getCompanyProfile(CustomUserDetails user) {
        CompanyProfile profile = getCompanyProfileFromUser(user);
        return mapToDto(profile);
    }

    @Override
    @Transactional
    public CompanyProfileDto updateCompanyProfile(CustomUserDetails user, UpdateCompanyProfileRequest request) {
        CompanyProfile profile = getCompanyProfileFromUser(user);

        profile.setName(request.getName().trim());
        profile.setIndustry(request.getIndustry() != null ? request.getIndustry().trim() : null);
        profile.setDescription(request.getDescription());
        profile.setLocation(request.getLocation() != null ? request.getLocation().trim() : null);
        profile.setWebsite(request.getWebsite() != null ? request.getWebsite().trim() : null);
        profile.setContactEmail(request.getContactEmail() != null ? request.getContactEmail().trim() : null);
        profile.setContactPhone(request.getContactPhone() != null ? request.getContactPhone().trim() : null);

        CompanyProfile updated = companyProfileRepository.save(profile);
        return mapToDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyProfileDto getCompanyById(Long companyId) {
        CompanyProfile profile = companyProfileRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found with id: " + companyId));
        return mapToDto(profile);
    }

    private CompanyProfileDto mapToDto(CompanyProfile profile) {
        return CompanyProfileDto.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .name(profile.getName())
                .industry(profile.getIndustry())
                .description(profile.getDescription())
                .location(profile.getLocation())
                .website(profile.getWebsite())
                .contactEmail(profile.getContactEmail())
                .contactPhone(profile.getContactPhone())
                .verificationStatus(profile.getVerificationStatus())
                .build();
    }
}

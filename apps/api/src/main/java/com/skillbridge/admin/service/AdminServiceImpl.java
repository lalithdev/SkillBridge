package com.skillbridge.admin.service;

import com.skillbridge.admin.dto.*;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.common.dto.PageMetadata;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.opportunity.dto.OpportunityDetailDto;
import com.skillbridge.opportunity.entity.Opportunity;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.opportunity.service.OpportunityService;
import com.skillbridge.user.entity.Role;
import com.skillbridge.user.entity.User;
import com.skillbridge.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final OpportunityService opportunityService;

    public AdminServiceImpl(
            UserRepository userRepository,
            CollegeRepository collegeRepository,
            CompanyProfileRepository companyProfileRepository,
            OpportunityRepository opportunityRepository,
            OpportunityService opportunityService) {
        this.userRepository = userRepository;
        this.collegeRepository = collegeRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.opportunityRepository = opportunityRepository;
        this.opportunityService = opportunityService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AdminUserDto> listUsers(
            Role role,
            Boolean isActive,
            String search,
            int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        String searchTrimmed = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        Page<User> usersPage = userRepository.findUsers(role, isActive, searchTrimmed, pageable);

        List<AdminUserDto> content = usersPage.getContent().stream()
                .map(AdminUserDto::from)
                .collect(Collectors.toList());

        return PageResponse.of(content, PageMetadata.from(usersPage));
    }

    @Override
    public AdminUserDto updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.setActive(request.getActive());
        User saved = userRepository.save(user);
        return AdminUserDto.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VerificationItemDto> getPendingVerifications(String type, VerificationStatus status) {
        List<VerificationItemDto> items = new ArrayList<>();

        boolean includeCollege = type == null || type.equalsIgnoreCase("COLLEGE");
        boolean includeCompany = type == null || type.equalsIgnoreCase("COMPANY");

        if (!includeCollege && !includeCompany) {
            throw new BadRequestException("Invalid organization type: " + type + ". Must be COLLEGE or COMPANY");
        }

        if (includeCollege) {
            List<College> colleges = (status != null)
                    ? collegeRepository.findByVerificationStatus(status)
                    : collegeRepository.findAll();

            for (College college : colleges) {
                items.add(VerificationItemDto.builder()
                        .id(college.getId())
                        .type("COLLEGE")
                        .name(college.getName())
                        .contactEmail(college.getContactEmail())
                        .website(college.getWebsite())
                        .verificationStatus(college.getVerificationStatus())
                        .submittedAt(college.getCreatedAt())
                        .build());
            }
        }

        if (includeCompany) {
            List<CompanyProfile> companies = (status != null)
                    ? companyProfileRepository.findByVerificationStatus(status)
                    : companyProfileRepository.findAll();

            for (CompanyProfile company : companies) {
                items.add(VerificationItemDto.builder()
                        .id(company.getId())
                        .type("COMPANY")
                        .name(company.getName())
                        .contactEmail(company.getContactEmail())
                        .website(company.getWebsite())
                        .verificationStatus(company.getVerificationStatus())
                        .submittedAt(company.getCreatedAt())
                        .build());
            }
        }

        items.sort(Comparator.comparing(VerificationItemDto::getSubmittedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        return items;
    }

    @Override
    public VerificationItemDto updateVerificationStatus(
            String type,
            Long id,
            UpdateVerificationStatusRequest request) {
        if ("COLLEGE".equalsIgnoreCase(type)) {
            College college = collegeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("College not found with id: " + id));

            college.setVerificationStatus(request.getStatus());
            college.setVerifiedAt(Instant.now());
            College saved = collegeRepository.save(college);

            return VerificationItemDto.builder()
                    .id(saved.getId())
                    .type("COLLEGE")
                    .name(saved.getName())
                    .contactEmail(saved.getContactEmail())
                    .website(saved.getWebsite())
                    .verificationStatus(saved.getVerificationStatus())
                    .submittedAt(saved.getCreatedAt())
                    .build();
        } else if ("COMPANY".equalsIgnoreCase(type)) {
            CompanyProfile company = companyProfileRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

            company.setVerificationStatus(request.getStatus());
            company.setVerifiedAt(Instant.now());
            CompanyProfile saved = companyProfileRepository.save(company);

            return VerificationItemDto.builder()
                    .id(saved.getId())
                    .type("COMPANY")
                    .name(saved.getName())
                    .contactEmail(saved.getContactEmail())
                    .website(saved.getWebsite())
                    .verificationStatus(saved.getVerificationStatus())
                    .submittedAt(saved.getCreatedAt())
                    .build();
        } else {
            throw new BadRequestException("Invalid organization type: " + type + ". Must be COLLEGE or COMPANY");
        }
    }

    @Override
    public OpportunityDetailDto moderateOpportunity(Long id, ModerateOpportunityRequest request) {
        Opportunity opportunity = opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + id));

        if (request.getStatus() != OpportunityStatus.CLOSED) {
            throw new BadRequestException("Moderation action can only set opportunity status to CLOSED");
        }

        opportunity.setStatus(OpportunityStatus.CLOSED);
        opportunityRepository.save(opportunity);

        return opportunityService.getOpportunityById(id, null);
    }
}

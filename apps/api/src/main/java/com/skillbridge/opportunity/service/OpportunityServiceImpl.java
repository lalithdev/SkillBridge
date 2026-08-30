package com.skillbridge.opportunity.service;

import com.skillbridge.college.dto.DepartmentDto;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.dto.PageMetadata;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ForbiddenException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.matching.service.MatchingServiceImpl;
import com.skillbridge.opportunity.dto.*;
import com.skillbridge.opportunity.entity.*;
import com.skillbridge.opportunity.repository.*;
import com.skillbridge.skill.dto.SkillDto;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.user.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class OpportunityServiceImpl implements OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final RequiredSkillRepository requiredSkillRepository;
    private final OpportunityRequiredBranchRepository requiredBranchRepository;
    private final OpportunityRequiredYearRepository requiredYearRepository;
    private final SkillRepository skillRepository;
    private final DepartmentRepository departmentRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final MatchingServiceImpl matchingService;

    public OpportunityServiceImpl(
            OpportunityRepository opportunityRepository,
            RequiredSkillRepository requiredSkillRepository,
            OpportunityRequiredBranchRepository requiredBranchRepository,
            OpportunityRequiredYearRepository requiredYearRepository,
            SkillRepository skillRepository,
            DepartmentRepository departmentRepository,
            CompanyProfileRepository companyProfileRepository,
            StudentProfileRepository studentProfileRepository,
            MatchingServiceImpl matchingService) {
        this.opportunityRepository = opportunityRepository;
        this.requiredSkillRepository = requiredSkillRepository;
        this.requiredBranchRepository = requiredBranchRepository;
        this.requiredYearRepository = requiredYearRepository;
        this.skillRepository = skillRepository;
        this.departmentRepository = departmentRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.matchingService = matchingService;
    }

    // =========================================================================
    // CREATE
    // =========================================================================

    @Override
    public OpportunityDetailDto createOpportunity(CreateOpportunityRequest request, CustomUserDetails currentUser) {
        Long companyProfileId = currentUser.getCompanyProfileId();
        if (companyProfileId == null) {
            throw new ForbiddenException("Only company users can create opportunities");
        }

        CompanyProfile company = companyProfileRepository.findById(companyProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));

        // Validate required skills
        List<Skill> requiredSkills = validateSkillIds(request.getRequiredSkillIds());

        // Validate required departments (if provided)
        List<Department> requiredDepartments = Collections.emptyList();
        if (request.getRequiredDepartmentIds() != null && !request.getRequiredDepartmentIds().isEmpty()) {
            requiredDepartments = validateDepartmentIds(request.getRequiredDepartmentIds());
        }

        // Validate required years (if provided)
        List<Integer> requiredYears = Collections.emptyList();
        if (request.getRequiredYearsOfStudy() != null && !request.getRequiredYearsOfStudy().isEmpty()) {
            requiredYears = validateYears(request.getRequiredYearsOfStudy());
        }

        // Build and save opportunity - status OPEN per API contract
        Opportunity opportunity = Opportunity.builder()
                .companyProfileId(companyProfileId)
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .location(request.getLocation())
                .mode(request.getMode() != null ? request.getMode() : OpportunityMode.ONSITE)
                .durationWeeks(request.getDurationWeeks() != null ? request.getDurationWeeks().shortValue() : null)
                .stipendAmount(request.getStipendAmount())
                .stipendCurrency(request.getStipendCurrency() != null ? request.getStipendCurrency() : "INR")
                .minCgpa(request.getMinCgpa())
                .applicationDeadline(request.getApplicationDeadline())
                .status(OpportunityStatus.OPEN)
                .build();

        opportunity = opportunityRepository.save(opportunity);
        final Long opportunityId = opportunity.getId();

        // Save required skills
        saveRequiredSkills(opportunityId, requiredSkills);

        // Save required branches
        List<Department> finalRequiredDepartments = requiredDepartments;
        saveRequiredBranches(opportunityId, finalRequiredDepartments);

        // Save required years
        List<Integer> finalRequiredYears = requiredYears;
        saveRequiredYears(opportunityId, finalRequiredYears);

        return buildDetailDto(opportunity, company, requiredSkills, finalRequiredDepartments, finalRequiredYears, null);
    }

    // =========================================================================
    // UPDATE
    // =========================================================================

    @Override
    public OpportunityDetailDto updateOpportunity(Long id, UpdateOpportunityRequest request, CustomUserDetails currentUser) {
        Opportunity opportunity = getOpportunityOrThrow(id);
        validateOwnership(opportunity, currentUser);

        CompanyProfile company = companyProfileRepository.findById(opportunity.getCompanyProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found"));

        // Validate and replace required skills
        List<Skill> requiredSkills = validateSkillIds(request.getRequiredSkillIds());

        // Validate and replace required departments
        List<Department> requiredDepartments = Collections.emptyList();
        if (request.getRequiredDepartmentIds() != null && !request.getRequiredDepartmentIds().isEmpty()) {
            requiredDepartments = validateDepartmentIds(request.getRequiredDepartmentIds());
        }

        // Validate and replace required years
        List<Integer> requiredYears = Collections.emptyList();
        if (request.getRequiredYearsOfStudy() != null && !request.getRequiredYearsOfStudy().isEmpty()) {
            requiredYears = validateYears(request.getRequiredYearsOfStudy());
        }

        // Update opportunity fields
        opportunity.setTitle(request.getTitle());
        opportunity.setDescription(request.getDescription());
        opportunity.setType(request.getType());
        opportunity.setLocation(request.getLocation());
        opportunity.setMode(request.getMode() != null ? request.getMode() : OpportunityMode.ONSITE);
        opportunity.setDurationWeeks(request.getDurationWeeks() != null ? request.getDurationWeeks().shortValue() : null);
        opportunity.setStipendAmount(request.getStipendAmount());
        opportunity.setStipendCurrency(request.getStipendCurrency() != null ? request.getStipendCurrency() : "INR");
        opportunity.setMinCgpa(request.getMinCgpa());
        opportunity.setApplicationDeadline(request.getApplicationDeadline());

        opportunity = opportunityRepository.save(opportunity);

        // Replace required skills, branches, years
        requiredSkillRepository.deleteByOpportunityId(id);
        requiredSkillRepository.flush();
        saveRequiredSkills(id, requiredSkills);

        requiredBranchRepository.deleteByOpportunityId(id);
        requiredBranchRepository.flush();
        List<Department> finalRequiredDepartments = requiredDepartments;
        saveRequiredBranches(id, finalRequiredDepartments);

        requiredYearRepository.deleteByOpportunityId(id);
        requiredYearRepository.flush();
        List<Integer> finalRequiredYears = requiredYears;
        saveRequiredYears(id, finalRequiredYears);

        return buildDetailDto(opportunity, company, requiredSkills, finalRequiredDepartments, finalRequiredYears, null);
    }

    // =========================================================================
    // STATUS UPDATE
    // =========================================================================

    @Override
    public OpportunityDetailDto updateOpportunityStatus(Long id, OpportunityStatusUpdateRequest request, CustomUserDetails currentUser) {
        Opportunity opportunity = getOpportunityOrThrow(id);
        validateOwnership(opportunity, currentUser);

        // Per API contract: only OPEN/CLOSED are valid via this endpoint
        if (request.getStatus() == OpportunityStatus.DRAFT) {
            throw new BadRequestException("Cannot set status to DRAFT via API");
        }

        opportunity.setStatus(request.getStatus());
        opportunity = opportunityRepository.save(opportunity);

        return buildDetailDtoFromEntity(opportunity, null);
    }

    // =========================================================================
    // GET BY ID
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public OpportunityDetailDto getOpportunityById(Long id, CustomUserDetails currentUser) {
        Opportunity opportunity = getOpportunityOrThrow(id);

        StudentProfile studentProfile = null;
        if (currentUser != null && currentUser.getRole() == Role.STUDENT && currentUser.getStudentProfileId() != null) {
            studentProfile = studentProfileRepository.findById(currentUser.getStudentProfileId()).orElse(null);
        }

        return buildDetailDtoFromEntity(opportunity, studentProfile);
    }

    // =========================================================================
    // SEARCH
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OpportunityListItemDto> searchOpportunities(
            String search,
            OpportunityType type,
            OpportunityMode mode,
            Long departmentId,
            Double minCgpa,
            OpportunityStatus status,
            int page,
            int size,
            CustomUserDetails currentUser) {

        // Default to OPEN if no status filter
        OpportunityStatus effectiveStatus = (status != null) ? status : OpportunityStatus.OPEN;

        BigDecimal minCgpaBd = (minCgpa != null) ? BigDecimal.valueOf(minCgpa) : null;

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Opportunity> opportunityPage = opportunityRepository.searchOpportunities(
                search, type, mode, effectiveStatus, minCgpaBd, departmentId, pageable);

        StudentProfile studentProfile = null;
        if (currentUser != null && currentUser.getRole() == Role.STUDENT && currentUser.getStudentProfileId() != null) {
            studentProfile = studentProfileRepository.findById(currentUser.getStudentProfileId()).orElse(null);
        }

        final StudentProfile finalStudentProfile = studentProfile;

        List<OpportunityListItemDto> items = opportunityPage.getContent().stream()
                .map(opp -> buildListItemDto(opp, finalStudentProfile))
                .collect(Collectors.toList());

        return PageResponse.of(items, PageMetadata.from(opportunityPage));
    }

    // =========================================================================
    // COMPANY OWN OPPORTUNITIES
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OpportunityListItemDto> getCompanyOpportunities(
            OpportunityStatus status,
            OpportunityType type,
            int page,
            int size,
            CustomUserDetails currentUser) {

        Long companyProfileId = currentUser.getCompanyProfileId();
        if (companyProfileId == null) {
            throw new ForbiddenException("Only company users can view their opportunities");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Opportunity> opportunityPage;

        if (status != null && type != null) {
            opportunityPage = opportunityRepository.findByCompanyProfileIdAndTypeAndStatus(
                    companyProfileId, type, status, pageable);
        } else if (status != null) {
            opportunityPage = opportunityRepository.findByCompanyProfileIdAndStatus(companyProfileId, status, pageable);
        } else if (type != null) {
            opportunityPage = opportunityRepository.findByCompanyProfileIdAndType(companyProfileId, type, pageable);
        } else {
            opportunityPage = opportunityRepository.findByCompanyProfileId(companyProfileId, pageable);
        }

        List<OpportunityListItemDto> items = opportunityPage.getContent().stream()
                .map(opp -> buildListItemDto(opp, null))
                .collect(Collectors.toList());

        return PageResponse.of(items, PageMetadata.from(opportunityPage));
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    private Opportunity getOpportunityOrThrow(Long id) {
        return opportunityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found: " + id));
    }

    private void validateOwnership(Opportunity opportunity, CustomUserDetails currentUser) {
        Long companyProfileId = currentUser.getCompanyProfileId();
        if (companyProfileId == null || !companyProfileId.equals(opportunity.getCompanyProfileId())) {
            throw new ForbiddenException("Access denied: you do not own this opportunity");
        }
    }

    private List<Skill> validateSkillIds(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            throw new BadRequestException("At least one required skill must be specified");
        }
        List<Skill> skills = skillRepository.findAllById(skillIds);
        if (skills.size() != new HashSet<>(skillIds).size()) {
            throw new BadRequestException("One or more skill IDs are invalid or not found in the taxonomy");
        }
        // Check all are active
        List<String> inactiveSkills = skills.stream()
                .filter(s -> !s.isActive())
                .map(Skill::getName)
                .collect(Collectors.toList());
        if (!inactiveSkills.isEmpty()) {
            throw new BadRequestException("The following skills are inactive and cannot be required: " + inactiveSkills);
        }
        return skills;
    }

    private List<Department> validateDepartmentIds(List<Long> departmentIds) {
        List<Department> departments = departmentRepository.findAllById(departmentIds);
        if (departments.size() != new HashSet<>(departmentIds).size()) {
            throw new BadRequestException("One or more department IDs are invalid or not found");
        }
        return departments;
    }

    private List<Integer> validateYears(List<Integer> years) {
        for (Integer year : years) {
            if (year < 1 || year > 8) {
                throw new BadRequestException("Year of study must be between 1 and 8, got: " + year);
            }
        }
        return years;
    }

    private void saveRequiredSkills(Long opportunityId, List<Skill> skills) {
        List<RequiredSkill> links = skills.stream()
                .map(skill -> RequiredSkill.builder()
                        .opportunityId(opportunityId)
                        .skillId(skill.getId())
                        .build())
                .collect(Collectors.toList());
        requiredSkillRepository.saveAll(links);
    }

    private void saveRequiredBranches(Long opportunityId, List<Department> departments) {
        List<OpportunityRequiredBranch> links = departments.stream()
                .map(dept -> OpportunityRequiredBranch.builder()
                        .opportunityId(opportunityId)
                        .departmentId(dept.getId())
                        .build())
                .collect(Collectors.toList());
        requiredBranchRepository.saveAll(links);
    }

    private void saveRequiredYears(Long opportunityId, List<Integer> years) {
        List<OpportunityRequiredYear> links = years.stream()
                .map(year -> OpportunityRequiredYear.builder()
                        .opportunityId(opportunityId)
                        .yearOfStudy(year.shortValue())
                        .build())
                .collect(Collectors.toList());
        requiredYearRepository.saveAll(links);
    }

    private OpportunityListItemDto buildListItemDto(Opportunity opp, StudentProfile studentProfile) {
        CompanyProfile company = companyProfileRepository.findById(opp.getCompanyProfileId()).orElse(null);

        List<RequiredSkill> requiredSkillLinks = requiredSkillRepository.findByOpportunityId(opp.getId());
        List<Long> skillIds = requiredSkillLinks.stream().map(RequiredSkill::getSkillId).collect(Collectors.toList());
        List<SkillDto> skillDtos = skillIds.isEmpty() ? Collections.emptyList()
                : skillRepository.findAllById(skillIds).stream().map(this::toSkillDto).collect(Collectors.toList());

        OpportunityListItemDto item = OpportunityListItemDto.builder()
                .id(opp.getId())
                .companyId(company != null ? company.getId() : opp.getCompanyProfileId())
                .companyName(company != null ? company.getName() : null)
                .companyLocation(company != null ? company.getLocation() : null)
                .companyVerificationStatus(company != null ? company.getVerificationStatus() : null)
                .title(opp.getTitle())
                .type(opp.getType())
                .mode(opp.getMode())
                .stipendAmount(opp.getStipendAmount())
                .applicationDeadline(opp.getApplicationDeadline())
                .status(opp.getStatus())
                .requiredSkills(skillDtos)
                .build();

        if (studentProfile != null) {
            matchingService.enrichListItem(item, studentProfile);
        }

        return item;
    }

    private OpportunityDetailDto buildDetailDtoFromEntity(Opportunity opportunity, StudentProfile studentProfile) {
        CompanyProfile company = companyProfileRepository.findById(opportunity.getCompanyProfileId()).orElse(null);

        List<RequiredSkill> requiredSkillLinks = requiredSkillRepository.findByOpportunityId(opportunity.getId());
        List<Skill> skills = requiredSkillLinks.isEmpty() ? Collections.emptyList()
                : skillRepository.findAllById(requiredSkillLinks.stream().map(RequiredSkill::getSkillId).collect(Collectors.toList()));

        List<OpportunityRequiredBranch> branchLinks = requiredBranchRepository.findByOpportunityId(opportunity.getId());
        List<Department> departments = branchLinks.isEmpty() ? Collections.emptyList()
                : departmentRepository.findAllById(branchLinks.stream().map(OpportunityRequiredBranch::getDepartmentId).collect(Collectors.toList()));

        List<OpportunityRequiredYear> yearLinks = requiredYearRepository.findByOpportunityId(opportunity.getId());
        List<Integer> years = yearLinks.stream().map(y -> y.getYearOfStudy().intValue()).collect(Collectors.toList());

        return buildDetailDto(opportunity, company, skills, departments, years, studentProfile);
    }

    private OpportunityDetailDto buildDetailDto(
            Opportunity opportunity,
            CompanyProfile company,
            List<Skill> requiredSkills,
            List<Department> requiredDepartments,
            List<Integer> requiredYears,
            StudentProfile studentProfile) {

        List<SkillDto> skillDtos = requiredSkills.stream().map(this::toSkillDto).collect(Collectors.toList());
        List<DepartmentDto> deptDtos = requiredDepartments.stream().map(this::toDepartmentDto).collect(Collectors.toList());

        OpportunityDetailDto detail = OpportunityDetailDto.builder()
                .id(opportunity.getId())
                .companyId(company != null ? company.getId() : opportunity.getCompanyProfileId())
                .companyName(company != null ? company.getName() : null)
                .companyWebsite(company != null ? company.getWebsite() : null)
                .companyVerificationStatus(company != null ? company.getVerificationStatus() : null)
                .title(opportunity.getTitle())
                .description(opportunity.getDescription())
                .type(opportunity.getType())
                .location(opportunity.getLocation())
                .mode(opportunity.getMode())
                .durationWeeks(opportunity.getDurationWeeks() != null ? opportunity.getDurationWeeks().intValue() : null)
                .stipendAmount(opportunity.getStipendAmount())
                .stipendCurrency(opportunity.getStipendCurrency())
                .minCgpa(opportunity.getMinCgpa())
                .applicationDeadline(opportunity.getApplicationDeadline())
                .status(opportunity.getStatus())
                .requiredSkills(skillDtos)
                .eligibleDepartments(deptDtos)
                .eligibleYears(requiredYears)
                .build();

        if (studentProfile != null) {
            matchingService.enrichDetailItem(detail, studentProfile);
        }

        return detail;
    }

    private SkillDto toSkillDto(Skill skill) {
        return SkillDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .active(skill.isActive())
                .build();
    }

    private DepartmentDto toDepartmentDto(Department dept) {
        return DepartmentDto.builder()
                .id(dept.getId())
                .name(dept.getName())
                .code(dept.getCode())
                .active(dept.isActive())
                .build();
    }
}

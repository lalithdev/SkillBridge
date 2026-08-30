package com.skillbridge.application.service;

import com.skillbridge.application.dto.*;
import com.skillbridge.application.entity.Application;
import com.skillbridge.application.entity.ApplicationStatus;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.dto.PageMetadata;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.DuplicateResourceException;
import com.skillbridge.common.exception.ForbiddenException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.internship.service.InternshipRecordService;
import com.skillbridge.opportunity.entity.*;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.opportunity.repository.OpportunityRequiredBranchRepository;
import com.skillbridge.opportunity.repository.OpportunityRequiredYearRepository;
import com.skillbridge.opportunity.repository.RequiredSkillRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.entity.StudentSkill;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import com.skillbridge.user.entity.Role;
import com.skillbridge.user.entity.User;
import com.skillbridge.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final RequiredSkillRepository requiredSkillRepository;
    private final OpportunityRequiredBranchRepository branchRepository;
    private final OpportunityRequiredYearRepository yearRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final InternshipRecordService internshipRecordService;

    public ApplicationServiceImpl(
            ApplicationRepository applicationRepository,
            StudentProfileRepository studentProfileRepository,
            OpportunityRepository opportunityRepository,
            CompanyProfileRepository companyProfileRepository,
            DepartmentRepository departmentRepository,
            UserRepository userRepository,
            RequiredSkillRepository requiredSkillRepository,
            OpportunityRequiredBranchRepository branchRepository,
            OpportunityRequiredYearRepository yearRepository,
            StudentSkillRepository studentSkillRepository,
            InternshipRecordService internshipRecordService) {
        this.applicationRepository = applicationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.opportunityRepository = opportunityRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.requiredSkillRepository = requiredSkillRepository;
        this.branchRepository = branchRepository;
        this.yearRepository = yearRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.internshipRecordService = internshipRecordService;
    }

    @Override
    public ApplicationDto submitApplication(SubmitApplicationRequest request, CustomUserDetails currentUser) {
        if (currentUser.getStudentProfileId() == null) {
            throw new ForbiddenException("Only students can apply to opportunities");
        }

        Long studentProfileId = currentUser.getStudentProfileId();
        StudentProfile student = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Opportunity opportunity = opportunityRepository.findById(request.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + request.getOpportunityId()));

        if (opportunity.getStatus() != OpportunityStatus.OPEN) {
            throw new BadRequestException("Opportunity is closed for applications");
        }

        if (opportunity.getApplicationDeadline() != null && opportunity.getApplicationDeadline().isBefore(LocalDate.now())) {
            throw new BadRequestException("Application deadline has passed");
        }

        validateStudentEligibility(student, opportunity);

        if (applicationRepository.existsByStudentProfileIdAndOpportunityId(studentProfileId, opportunity.getId())) {
            throw new DuplicateResourceException("Student has already applied to this opportunity");
        }

        BigDecimal matchPercentAtApply = calculateMatchPercent(studentProfileId, opportunity.getId());

        Application application = Application.builder()
                .studentProfileId(studentProfileId)
                .opportunityId(opportunity.getId())
                .status(ApplicationStatus.APPLIED)
                .matchPercentAtApply(matchPercentAtApply)
                .build();

        Application saved = applicationRepository.save(application);
        return ApplicationDto.from(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentApplicationDto> getMyApplications(
            ApplicationStatus status,
            int page,
            int size,
            CustomUserDetails currentUser) {
        if (currentUser.getStudentProfileId() == null) {
            throw new ForbiddenException("Only students can view their applications");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "appliedAt"));
        Page<Application> applicationsPage = (status != null)
                ? applicationRepository.findByStudentProfileIdAndStatus(currentUser.getStudentProfileId(), status, pageable)
                : applicationRepository.findByStudentProfileId(currentUser.getStudentProfileId(), pageable);

        List<StudentApplicationDto> content = applicationsPage.getContent().stream()
                .map(this::mapToStudentApplicationDto)
                .collect(Collectors.toList());

        return PageResponse.of(content, PageMetadata.from(applicationsPage));
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationDetailDto getApplicationById(Long id, CustomUserDetails currentUser) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        StudentProfile student = studentProfileRepository.findById(application.getStudentProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));

        Opportunity opportunity = opportunityRepository.findById(application.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        boolean isStudent = currentUser.getRole() == Role.STUDENT && student.getId().equals(currentUser.getStudentProfileId());
        boolean isCompany = currentUser.getRole() == Role.COMPANY && opportunity.getCompanyProfileId().equals(currentUser.getCompanyProfileId());
        boolean isCollege = currentUser.getRole() == Role.COLLEGE && student.getCollegeId().equals(currentUser.getCollegeId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isStudent && !isCompany && !isCollege && !isAdmin) {
            throw new ForbiddenException("You do not have permission to view this application");
        }

        return mapToApplicationDetailDto(application, student, opportunity);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CompanyApplicantDto> getOpportunityApplications(
            Long opportunityId,
            ApplicationStatus status,
            Boolean eligibleOnly,
            int page,
            int size,
            CustomUserDetails currentUser) {
        Opportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found with id: " + opportunityId));

        if (currentUser.getRole() != Role.ADMIN &&
            (currentUser.getCompanyProfileId() == null || !opportunity.getCompanyProfileId().equals(currentUser.getCompanyProfileId()))) {
            throw new ForbiddenException("You do not have permission to view applicants for this opportunity");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Application> applicationsPage = applicationRepository.findOpportunityApplicants(opportunityId, status, pageable);

        List<CompanyApplicantDto> content = applicationsPage.getContent().stream()
                .map(this::mapToCompanyApplicantDto)
                .collect(Collectors.toList());

        return PageResponse.of(content, PageMetadata.from(applicationsPage));
    }

    @Override
    public ApplicationDto updateApplicationStatus(
            Long id,
            UpdateApplicationStatusRequest request,
            CustomUserDetails currentUser) {
        if (currentUser.getCompanyProfileId() == null) {
            throw new ForbiddenException("Only companies can update recruitment pipeline stages");
        }

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        Opportunity opportunity = opportunityRepository.findById(application.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        if (!opportunity.getCompanyProfileId().equals(currentUser.getCompanyProfileId())) {
            throw new ForbiddenException("You can only update applications for your own opportunities");
        }

        ApplicationStatus current = application.getStatus();
        ApplicationStatus target = request.getStatus();

        validatePipelineTransition(current, target);

        application.setStatus(target);
        Application saved = applicationRepository.save(application);

        if (target == ApplicationStatus.SELECTED) {
            internshipRecordService.createInternshipRecord(saved);
        }

        return ApplicationDto.from(saved);
    }

    private void validatePipelineTransition(ApplicationStatus current, ApplicationStatus target) {
        if (current == ApplicationStatus.SELECTED || current == ApplicationStatus.REJECTED) {
            throw new BadRequestException("Cannot change status from terminal stage: " + current);
        }

        if (target == ApplicationStatus.REJECTED) {
            return;
        }

        boolean valid = switch (current) {
            case APPLIED -> target == ApplicationStatus.UNDER_REVIEW;
            case UNDER_REVIEW -> target == ApplicationStatus.SHORTLISTED;
            case SHORTLISTED -> target == ApplicationStatus.INTERVIEW;
            case INTERVIEW -> target == ApplicationStatus.SELECTED;
            default -> false;
        };

        if (!valid) {
            throw new BadRequestException("Invalid recruitment stage transition from " + current + " to " + target);
        }
    }

    private void validateStudentEligibility(StudentProfile student, Opportunity opportunity) {
        if (opportunity.getMinCgpa() != null) {
            if (student.getCgpa() == null || student.getCgpa().compareTo(opportunity.getMinCgpa()) < 0) {
                throw new BadRequestException("Student CGPA does not meet the minimum requirement");
            }
        }

        List<OpportunityRequiredBranch> requiredBranches = branchRepository.findByOpportunityId(opportunity.getId());
        if (!requiredBranches.isEmpty()) {
            if (student.getDepartmentId() == null) {
                throw new BadRequestException("Student branch is not specified for branch-restricted opportunity");
            }
            boolean branchMatches = requiredBranches.stream()
                    .anyMatch(b -> b.getDepartmentId().equals(student.getDepartmentId()));
            if (!branchMatches) {
                throw new BadRequestException("Student branch is not eligible for this opportunity");
            }
        }

        List<OpportunityRequiredYear> requiredYears = yearRepository.findByOpportunityId(opportunity.getId());
        if (!requiredYears.isEmpty()) {
            if (student.getYearOfStudy() == null) {
                throw new BadRequestException("Student year of study is not specified for year-restricted opportunity");
            }
            boolean yearMatches = requiredYears.stream()
                    .anyMatch(y -> y.getYearOfStudy().equals(student.getYearOfStudy()));
            if (!yearMatches) {
                throw new BadRequestException("Student year of study is not eligible for this opportunity");
            }
        }
    }

    private BigDecimal calculateMatchPercent(Long studentProfileId, Long opportunityId) {
        List<RequiredSkill> reqSkills = requiredSkillRepository.findByOpportunityId(opportunityId);
        if (reqSkills.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        Set<Long> reqSkillIds = reqSkills.stream()
                .map(RequiredSkill::getSkillId)
                .collect(Collectors.toSet());

        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentProfileId(studentProfileId);
        Set<Long> studentSkillIds = studentSkills.stream()
                .map(StudentSkill::getSkillId)
                .collect(Collectors.toSet());

        long matchedCount = reqSkillIds.stream().filter(studentSkillIds::contains).count();
        double percent = ((double) matchedCount / reqSkillIds.size()) * 100.0;
        return BigDecimal.valueOf(percent).setScale(2, RoundingMode.HALF_UP);
    }

    private StudentApplicationDto mapToStudentApplicationDto(Application application) {
        Opportunity opp = opportunityRepository.findById(application.getOpportunityId()).orElse(null);
        String companyName = "Unknown Company";
        if (opp != null) {
            CompanyProfile cp = companyProfileRepository.findById(opp.getCompanyProfileId()).orElse(null);
            if (cp != null) {
                companyName = cp.getName();
            }
        }

        return StudentApplicationDto.builder()
                .id(application.getId())
                .opportunityId(application.getOpportunityId())
                .opportunityTitle(opp != null ? opp.getTitle() : "Unknown Opportunity")
                .opportunityType(opp != null ? opp.getType() : null)
                .companyName(companyName)
                .status(application.getStatus())
                .matchPercentAtApply(application.getMatchPercentAtApply())
                .appliedAt(application.getAppliedAt())
                .build();
    }

    private CompanyApplicantDto mapToCompanyApplicantDto(Application application) {
        StudentProfile student = studentProfileRepository.findById(application.getStudentProfileId()).orElse(null);
        String departmentName = null;
        if (student != null && student.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(student.getDepartmentId()).orElse(null);
            if (dept != null) {
                departmentName = dept.getName();
            }
        }

        return CompanyApplicantDto.builder()
                .applicationId(application.getId())
                .studentProfileId(application.getStudentProfileId())
                .studentName(student != null ? student.getFirstName() + " " + student.getLastName() : "Unknown Student")
                .departmentName(departmentName)
                .yearOfStudy(student != null ? student.getYearOfStudy() : null)
                .cgpa(student != null ? student.getCgpa() : null)
                .status(application.getStatus())
                .matchPercentAtApply(application.getMatchPercentAtApply())
                .hasResume(student != null && student.getResumePath() != null)
                .appliedAt(application.getAppliedAt())
                .build();
    }

    private ApplicationDetailDto mapToApplicationDetailDto(
            Application application,
            StudentProfile student,
            Opportunity opportunity) {
        String studentEmail = null;
        if (student.getUserId() != null) {
            User user = userRepository.findById(student.getUserId()).orElse(null);
            if (user != null) {
                studentEmail = user.getEmail();
            }
        }

        String departmentName = null;
        if (student.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(student.getDepartmentId()).orElse(null);
            if (dept != null) {
                departmentName = dept.getName();
            }
        }

        String companyName = "Unknown Company";
        if (opportunity.getCompanyProfileId() != null) {
            CompanyProfile cp = companyProfileRepository.findById(opportunity.getCompanyProfileId()).orElse(null);
            if (cp != null) {
                companyName = cp.getName();
            }
        }

        String resumeDownloadUrl = null;
        if (student.getResumePath() != null) {
            resumeDownloadUrl = "/api/v1/students/" + student.getId() + "/resume";
        }

        return ApplicationDetailDto.builder()
                .id(application.getId())
                .studentProfileId(student.getId())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .studentEmail(studentEmail)
                .departmentName(departmentName)
                .cgpa(student.getCgpa())
                .opportunityId(opportunity.getId())
                .opportunityTitle(opportunity.getTitle())
                .companyName(companyName)
                .status(application.getStatus())
                .matchPercentAtApply(application.getMatchPercentAtApply())
                .appliedAt(application.getAppliedAt())
                .resumeDownloadUrl(resumeDownloadUrl)
                .build();
    }
}

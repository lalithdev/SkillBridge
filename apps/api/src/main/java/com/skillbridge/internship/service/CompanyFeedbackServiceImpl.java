package com.skillbridge.internship.service;

import com.skillbridge.application.entity.Application;
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
import com.skillbridge.internship.dto.CollegeFeedbackItemDto;
import com.skillbridge.internship.dto.FeedbackDto;
import com.skillbridge.internship.dto.SubmitFeedbackRequest;
import com.skillbridge.internship.entity.CompanyFeedback;
import com.skillbridge.internship.entity.InternshipRecord;
import com.skillbridge.internship.entity.InternshipStatus;
import com.skillbridge.internship.repository.CompanyFeedbackRepository;
import com.skillbridge.internship.repository.InternshipRecordRepository;
import com.skillbridge.opportunity.entity.Opportunity;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.user.entity.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompanyFeedbackServiceImpl implements CompanyFeedbackService {

    private final CompanyFeedbackRepository companyFeedbackRepository;
    private final InternshipRecordRepository internshipRecordRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final DepartmentRepository departmentRepository;

    public CompanyFeedbackServiceImpl(
            CompanyFeedbackRepository companyFeedbackRepository,
            InternshipRecordRepository internshipRecordRepository,
            ApplicationRepository applicationRepository,
            StudentProfileRepository studentProfileRepository,
            OpportunityRepository opportunityRepository,
            CompanyProfileRepository companyProfileRepository,
            DepartmentRepository departmentRepository) {
        this.companyFeedbackRepository = companyFeedbackRepository;
        this.internshipRecordRepository = internshipRecordRepository;
        this.applicationRepository = applicationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.opportunityRepository = opportunityRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public FeedbackDto submitCompanyFeedback(
            Long internshipId,
            SubmitFeedbackRequest request,
            CustomUserDetails currentUser) {
        if (currentUser.getCompanyProfileId() == null) {
            throw new ForbiddenException("Only companies can submit feedback");
        }

        InternshipRecord record = internshipRecordRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship record not found with id: " + internshipId));

        Application application = applicationRepository.findById(record.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        Opportunity opportunity = opportunityRepository.findById(application.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        if (!opportunity.getCompanyProfileId().equals(currentUser.getCompanyProfileId())) {
            throw new ForbiddenException("You can only submit feedback for your own company's internships");
        }

        if (record.getStatus() != InternshipStatus.COMPLETED) {
            throw new BadRequestException("Feedback can only be submitted for COMPLETED internships");
        }

        if (companyFeedbackRepository.existsByInternshipRecordId(internshipId)) {
            throw new DuplicateResourceException("Feedback has already been submitted for this internship");
        }

        CompanyFeedback feedback = CompanyFeedback.builder()
                .internshipRecordId(internshipId)
                .feedbackText(request.getFeedbackText().trim())
                .build();

        CompanyFeedback saved = companyFeedbackRepository.save(feedback);

        StudentProfile student = studentProfileRepository.findById(application.getStudentProfileId()).orElse(null);
        CompanyProfile company = companyProfileRepository.findById(opportunity.getCompanyProfileId()).orElse(null);

        String studentName = student != null ? student.getFirstName() + " " + student.getLastName() : "Unknown Student";
        String companyName = company != null ? company.getName() : "Unknown Company";

        return FeedbackDto.builder()
                .id(saved.getId())
                .internshipRecordId(saved.getInternshipRecordId())
                .studentName(studentName)
                .companyName(companyName)
                .feedbackText(saved.getFeedbackText())
                .submittedAt(saved.getSubmittedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackDto getInternshipFeedback(
            Long internshipId,
            CustomUserDetails currentUser) {
        InternshipRecord record = internshipRecordRepository.findById(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Internship record not found with id: " + internshipId));

        CompanyFeedback feedback = companyFeedbackRepository.findByInternshipRecordId(internshipId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found for internship id: " + internshipId));

        Application application = applicationRepository.findById(record.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        StudentProfile student = studentProfileRepository.findById(application.getStudentProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        Opportunity opportunity = opportunityRepository.findById(application.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));
        CompanyProfile company = companyProfileRepository.findById(opportunity.getCompanyProfileId())
                .orElse(null);

        boolean isStudent = currentUser.getRole() == Role.STUDENT && student.getId().equals(currentUser.getStudentProfileId());
        boolean isCompany = currentUser.getRole() == Role.COMPANY && opportunity.getCompanyProfileId().equals(currentUser.getCompanyProfileId());
        boolean isCollege = currentUser.getRole() == Role.COLLEGE && student.getCollegeId().equals(currentUser.getCollegeId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isStudent && !isCompany && !isCollege && !isAdmin) {
            throw new ForbiddenException("You do not have permission to view this feedback");
        }

        String studentName = student.getFirstName() + " " + student.getLastName();
        String companyName = company != null ? company.getName() : "Unknown Company";

        return FeedbackDto.builder()
                .id(feedback.getId())
                .internshipRecordId(feedback.getInternshipRecordId())
                .studentName(studentName)
                .companyName(companyName)
                .feedbackText(feedback.getFeedbackText())
                .submittedAt(feedback.getSubmittedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CollegeFeedbackItemDto> getCollegeAggregatedFeedback(
            Long collegeIdOverride,
            int page,
            int size,
            CustomUserDetails currentUser) {
        Long effectiveCollegeId;
        if (currentUser.getRole() == Role.ADMIN) {
            if (collegeIdOverride == null) {
                throw new BadRequestException("Admin must provide collegeId query parameter");
            }
            effectiveCollegeId = collegeIdOverride;
        } else if (currentUser.getRole() == Role.COLLEGE) {
            effectiveCollegeId = currentUser.getCollegeId();
            if (effectiveCollegeId == null) {
                throw new ForbiddenException("College profile not found for authenticated user");
            }
        } else {
            throw new ForbiddenException("Only colleges and admins can view aggregated college feedback");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<CompanyFeedback> feedbackPage = companyFeedbackRepository.findByCollegeId(effectiveCollegeId, pageable);

        List<CollegeFeedbackItemDto> content = feedbackPage.getContent().stream()
                .map(this::mapToCollegeFeedbackItemDto)
                .collect(Collectors.toList());

        return PageResponse.of(content, PageMetadata.from(feedbackPage));
    }

    private CollegeFeedbackItemDto mapToCollegeFeedbackItemDto(CompanyFeedback feedback) {
        InternshipRecord record = internshipRecordRepository.findById(feedback.getInternshipRecordId()).orElse(null);
        Application application = record != null ? applicationRepository.findById(record.getApplicationId()).orElse(null) : null;
        StudentProfile student = application != null ? studentProfileRepository.findById(application.getStudentProfileId()).orElse(null) : null;
        Opportunity opportunity = application != null ? opportunityRepository.findById(application.getOpportunityId()).orElse(null) : null;
        CompanyProfile company = opportunity != null ? companyProfileRepository.findById(opportunity.getCompanyProfileId()).orElse(null) : null;

        String departmentName = null;
        if (student != null && student.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(student.getDepartmentId()).orElse(null);
            if (dept != null) {
                departmentName = dept.getName();
            }
        }

        String studentName = student != null ? student.getFirstName() + " " + student.getLastName() : "Unknown Student";
        String companyName = company != null ? company.getName() : "Unknown Company";
        String opportunityTitle = opportunity != null ? opportunity.getTitle() : "Unknown Opportunity";

        return CollegeFeedbackItemDto.builder()
                .id(feedback.getId())
                .studentName(studentName)
                .departmentName(departmentName)
                .companyName(companyName)
                .opportunityTitle(opportunityTitle)
                .feedbackText(feedback.getFeedbackText())
                .submittedAt(feedback.getSubmittedAt())
                .build();
    }
}

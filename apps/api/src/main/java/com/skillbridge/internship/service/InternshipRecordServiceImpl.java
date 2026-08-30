package com.skillbridge.internship.service;

import com.skillbridge.application.entity.Application;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.common.dto.PageMetadata;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.ForbiddenException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.internship.dto.InternshipRecordDto;
import com.skillbridge.internship.dto.UpdateInternshipStatusRequest;
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
public class InternshipRecordServiceImpl implements InternshipRecordService {

    private final InternshipRecordRepository internshipRecordRepository;
    private final CompanyFeedbackRepository companyFeedbackRepository;
    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final OpportunityRepository opportunityRepository;
    private final CompanyProfileRepository companyProfileRepository;

    public InternshipRecordServiceImpl(
            InternshipRecordRepository internshipRecordRepository,
            CompanyFeedbackRepository companyFeedbackRepository,
            ApplicationRepository applicationRepository,
            StudentProfileRepository studentProfileRepository,
            OpportunityRepository opportunityRepository,
            CompanyProfileRepository companyProfileRepository) {
        this.internshipRecordRepository = internshipRecordRepository;
        this.companyFeedbackRepository = companyFeedbackRepository;
        this.applicationRepository = applicationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.opportunityRepository = opportunityRepository;
        this.companyProfileRepository = companyProfileRepository;
    }

    @Override
    public InternshipRecord createInternshipRecord(Application application) {
        return internshipRecordRepository.findByApplicationId(application.getId())
                .orElseGet(() -> {
                    InternshipRecord record = InternshipRecord.builder()
                            .applicationId(application.getId())
                            .status(InternshipStatus.UPCOMING)
                            .build();
                    return internshipRecordRepository.save(record);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<InternshipRecordDto> getMyInternships(CustomUserDetails currentUser) {
        if (currentUser.getStudentProfileId() == null) {
            throw new ForbiddenException("Only students can view their internships");
        }

        List<InternshipRecord> records = internshipRecordRepository.findByStudentProfileId(currentUser.getStudentProfileId());
        return records.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<InternshipRecordDto> getCompanyInternships(
            InternshipStatus status,
            int page,
            int size,
            CustomUserDetails currentUser) {
        if (currentUser.getCompanyProfileId() == null) {
            throw new ForbiddenException("Only companies can view their intern roster");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<InternshipRecord> records = internshipRecordRepository.findByCompanyProfileId(
                currentUser.getCompanyProfileId(), status, pageable);

        List<InternshipRecordDto> content = records.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return PageResponse.of(content, PageMetadata.from(records));
    }

    @Override
    @Transactional(readOnly = true)
    public InternshipRecordDto getInternshipById(Long id, CustomUserDetails currentUser) {
        InternshipRecord record = internshipRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship record not found with id: " + id));

        Application application = applicationRepository.findById(record.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found for record"));
        StudentProfile student = studentProfileRepository.findById(application.getStudentProfileId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        Opportunity opportunity = opportunityRepository.findById(application.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        boolean isStudent = currentUser.getRole() == Role.STUDENT && student.getId().equals(currentUser.getStudentProfileId());
        boolean isCompany = currentUser.getRole() == Role.COMPANY && opportunity.getCompanyProfileId().equals(currentUser.getCompanyProfileId());
        boolean isCollege = currentUser.getRole() == Role.COLLEGE && student.getCollegeId().equals(currentUser.getCollegeId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isStudent && !isCompany && !isCollege && !isAdmin) {
            throw new ForbiddenException("You do not have permission to view this internship record");
        }

        return mapToDto(record, application, student, opportunity);
    }

    @Override
    public InternshipRecordDto updateInternshipStatus(
            Long id,
            UpdateInternshipStatusRequest request,
            CustomUserDetails currentUser) {
        if (currentUser.getCompanyProfileId() == null) {
            throw new ForbiddenException("Only companies can update internship status");
        }

        InternshipRecord record = internshipRecordRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Internship record not found with id: " + id));

        Application application = applicationRepository.findById(record.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));
        Opportunity opportunity = opportunityRepository.findById(application.getOpportunityId())
                .orElseThrow(() -> new ResourceNotFoundException("Opportunity not found"));

        if (!opportunity.getCompanyProfileId().equals(currentUser.getCompanyProfileId())) {
            throw new ForbiddenException("You can only update internship records for your own company");
        }

        InternshipStatus current = record.getStatus();
        InternshipStatus target = request.getStatus();

        if (current != target) {
            validateStatusTransition(current, target);
            record.setStatus(target);
        }

        if (request.getStartDate() != null) {
            record.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            record.setEndDate(request.getEndDate());
        }

        if (record.getStartDate() != null && record.getEndDate() != null) {
            if (record.getEndDate().isBefore(record.getStartDate())) {
                throw new BadRequestException("End date cannot be before start date");
            }
        }

        InternshipRecord saved = internshipRecordRepository.save(record);
        StudentProfile student = studentProfileRepository.findById(application.getStudentProfileId()).orElse(null);

        return mapToDto(saved, application, student, opportunity);
    }

    private void validateStatusTransition(InternshipStatus current, InternshipStatus target) {
        if (current == InternshipStatus.COMPLETED) {
            throw new BadRequestException("Cannot update status of an already COMPLETED internship");
        }
        if (current == InternshipStatus.UPCOMING && target != InternshipStatus.ONGOING && target != InternshipStatus.COMPLETED) {
            throw new BadRequestException("Invalid status transition from " + current + " to " + target);
        }
        if (current == InternshipStatus.ONGOING && target != InternshipStatus.COMPLETED) {
            throw new BadRequestException("Invalid status transition from " + current + " to " + target);
        }
    }

    private InternshipRecordDto mapToDto(InternshipRecord record) {
        Application application = applicationRepository.findById(record.getApplicationId()).orElse(null);
        StudentProfile student = application != null ? studentProfileRepository.findById(application.getStudentProfileId()).orElse(null) : null;
        Opportunity opportunity = application != null ? opportunityRepository.findById(application.getOpportunityId()).orElse(null) : null;
        return mapToDto(record, application, student, opportunity);
    }

    private InternshipRecordDto mapToDto(
            InternshipRecord record,
            Application application,
            StudentProfile student,
            Opportunity opportunity) {
        String studentName = student != null ? student.getFirstName() + " " + student.getLastName() : "Unknown Student";
        Long companyId = opportunity != null ? opportunity.getCompanyProfileId() : null;
        String companyName = "Unknown Company";
        if (companyId != null) {
            CompanyProfile cp = companyProfileRepository.findById(companyId).orElse(null);
            if (cp != null) {
                companyName = cp.getName();
            }
        }

        boolean feedbackSubmitted = companyFeedbackRepository.existsByInternshipRecordId(record.getId());

        return InternshipRecordDto.builder()
                .id(record.getId())
                .applicationId(record.getApplicationId())
                .studentId(student != null ? student.getId() : null)
                .studentName(studentName)
                .companyId(companyId)
                .companyName(companyName)
                .opportunityTitle(opportunity != null ? opportunity.getTitle() : "Unknown Opportunity")
                .type(opportunity != null ? opportunity.getType() : null)
                .status(record.getStatus())
                .startDate(record.getStartDate())
                .endDate(record.getEndDate())
                .feedbackSubmitted(feedbackSubmitted)
                .build();
    }
}

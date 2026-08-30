package com.skillbridge.internship.service;

import com.skillbridge.application.entity.Application;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.DuplicateResourceException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyFeedbackServiceTest {

    @Mock
    private CompanyFeedbackRepository companyFeedbackRepository;
    @Mock
    private InternshipRecordRepository internshipRecordRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private OpportunityRepository opportunityRepository;
    @Mock
    private CompanyProfileRepository companyProfileRepository;
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private CompanyFeedbackServiceImpl companyFeedbackService;

    private CustomUserDetails companyUser;
    private InternshipRecord mockRecord;
    private Application mockApp;
    private Opportunity mockOpp;
    private StudentProfile mockStudent;
    private CompanyProfile mockCompany;

    @BeforeEach
    void setUp() {
        companyUser = CustomUserDetails.builder()
                .userId(50L)
                .email("company@test.com")
                .role(Role.COMPANY)
                .active(true)
                .companyProfileId(100L)
                .build();

        mockRecord = InternshipRecord.builder()
                .id(1L)
                .applicationId(10L)
                .status(InternshipStatus.COMPLETED)
                .build();

        mockApp = Application.builder()
                .id(10L)
                .studentProfileId(20L)
                .opportunityId(30L)
                .build();

        mockOpp = Opportunity.builder()
                .id(30L)
                .companyProfileId(100L)
                .title("Backend Intern")
                .build();

        mockStudent = StudentProfile.builder()
                .id(20L)
                .firstName("Alice")
                .lastName("Smith")
                .build();

        mockCompany = CompanyProfile.builder()
                .id(100L)
                .name("Acme Corp")
                .build();
    }

    @Test
    @DisplayName("Submit feedback - success when status is COMPLETED")
    void submitFeedback_Success() {
        when(internshipRecordRepository.findById(1L)).thenReturn(Optional.of(mockRecord));
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(mockApp));
        when(opportunityRepository.findById(30L)).thenReturn(Optional.of(mockOpp));
        when(companyFeedbackRepository.existsByInternshipRecordId(1L)).thenReturn(false);

        when(companyFeedbackRepository.save(any(CompanyFeedback.class))).thenAnswer(i -> {
            CompanyFeedback fb = i.getArgument(0);
            fb.setId(500L);
            fb.setSubmittedAt(Instant.now());
            return fb;
        });

        when(studentProfileRepository.findById(20L)).thenReturn(Optional.of(mockStudent));
        when(companyProfileRepository.findById(100L)).thenReturn(Optional.of(mockCompany));

        SubmitFeedbackRequest request = SubmitFeedbackRequest.builder()
                .feedbackText("Outstanding performance and communication during the internship.")
                .build();

        FeedbackDto result = companyFeedbackService.submitCompanyFeedback(1L, request, companyUser);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(500L);
        assertThat(result.getStudentName()).isEqualTo("Alice Smith");
        assertThat(result.getCompanyName()).isEqualTo("Acme Corp");
    }

    @Test
    @DisplayName("Submit feedback - throws BadRequest when internship is not COMPLETED")
    void submitFeedback_ThrowsWhenNotCompleted() {
        mockRecord.setStatus(InternshipStatus.ONGOING);
        when(internshipRecordRepository.findById(1L)).thenReturn(Optional.of(mockRecord));
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(mockApp));
        when(opportunityRepository.findById(30L)).thenReturn(Optional.of(mockOpp));

        SubmitFeedbackRequest request = SubmitFeedbackRequest.builder()
                .feedbackText("Great job so far!")
                .build();

        assertThatThrownBy(() -> companyFeedbackService.submitCompanyFeedback(1L, request, companyUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("COMPLETED");
    }

    @Test
    @DisplayName("Submit feedback - throws DuplicateResourceException on duplicate feedback")
    void submitFeedback_ThrowsOnDuplicate() {
        when(internshipRecordRepository.findById(1L)).thenReturn(Optional.of(mockRecord));
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(mockApp));
        when(opportunityRepository.findById(30L)).thenReturn(Optional.of(mockOpp));
        when(companyFeedbackRepository.existsByInternshipRecordId(1L)).thenReturn(true);

        SubmitFeedbackRequest request = SubmitFeedbackRequest.builder()
                .feedbackText("Duplicate feedback submission attempt.")
                .build();

        assertThatThrownBy(() -> companyFeedbackService.submitCompanyFeedback(1L, request, companyUser))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already been submitted");
    }
}

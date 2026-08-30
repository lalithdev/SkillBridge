package com.skillbridge.application.service;

import com.skillbridge.application.dto.ApplicationDto;
import com.skillbridge.application.dto.SubmitApplicationRequest;
import com.skillbridge.application.dto.UpdateApplicationStatusRequest;
import com.skillbridge.application.entity.Application;
import com.skillbridge.application.entity.ApplicationStatus;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.DuplicateResourceException;
import com.skillbridge.common.security.CustomUserDetails;
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
import com.skillbridge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

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
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequiredSkillRepository requiredSkillRepository;
    @Mock
    private OpportunityRequiredBranchRepository branchRepository;
    @Mock
    private OpportunityRequiredYearRepository yearRepository;
    @Mock
    private StudentSkillRepository studentSkillRepository;
    @Mock
    private InternshipRecordService internshipRecordService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private StudentProfile mockStudent;
    private Opportunity mockOpportunity;
    private CustomUserDetails studentUser;
    private CustomUserDetails companyUser;

    @BeforeEach
    void setUp() {
        mockStudent = StudentProfile.builder()
                .id(1L)
                .userId(10L)
                .collegeId(100L)
                .firstName("John")
                .lastName("Doe")
                .departmentId(2L)
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.50))
                .build();

        mockOpportunity = Opportunity.builder()
                .id(20L)
                .companyProfileId(50L)
                .title("Software Engineer Intern")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.ONSITE)
                .status(OpportunityStatus.OPEN)
                .minCgpa(BigDecimal.valueOf(7.00))
                .applicationDeadline(LocalDate.now().plusDays(10))
                .build();

        studentUser = CustomUserDetails.builder()
                .userId(10L)
                .email("student@college.edu")
                .role(Role.STUDENT)
                .active(true)
                .studentProfileId(1L)
                .collegeId(100L)
                .build();
        companyUser = CustomUserDetails.builder()
                .userId(500L)
                .email("company@tech.com")
                .role(Role.COMPANY)
                .active(true)
                .companyProfileId(50L)
                .build();
    }

    @Test
    @DisplayName("Submit application - success with match percentage snapshot")
    void submitApplication_Success() {
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(opportunityRepository.findById(20L)).thenReturn(Optional.of(mockOpportunity));
        when(branchRepository.findByOpportunityId(20L)).thenReturn(Collections.emptyList());
        when(yearRepository.findByOpportunityId(20L)).thenReturn(Collections.emptyList());
        when(applicationRepository.existsByStudentProfileIdAndOpportunityId(1L, 20L)).thenReturn(false);

        when(requiredSkillRepository.findByOpportunityId(20L)).thenReturn(List.of(
                RequiredSkill.builder().opportunityId(20L).skillId(101L).build(),
                RequiredSkill.builder().opportunityId(20L).skillId(102L).build()
        ));
        when(studentSkillRepository.findByStudentProfileId(1L)).thenReturn(List.of(
                StudentSkill.builder().studentProfileId(1L).skillId(101L).build()
        ));

        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> {
            Application app = invocation.getArgument(0);
            app.setId(1001L);
            return app;
        });

        SubmitApplicationRequest request = SubmitApplicationRequest.builder().opportunityId(20L).build();
        ApplicationDto dto = applicationService.submitApplication(request, studentUser);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1001L);
        assertThat(dto.getStatus()).isEqualTo(ApplicationStatus.APPLIED);
        assertThat(dto.getMatchPercentAtApply()).isEqualByComparingTo(BigDecimal.valueOf(50.00));
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    @DisplayName("Submit application - throws BadRequest when opportunity closed")
    void submitApplication_ClosedOpportunity() {
        mockOpportunity.setStatus(OpportunityStatus.CLOSED);
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(opportunityRepository.findById(20L)).thenReturn(Optional.of(mockOpportunity));

        SubmitApplicationRequest request = SubmitApplicationRequest.builder().opportunityId(20L).build();
        assertThatThrownBy(() -> applicationService.submitApplication(request, studentUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("closed");
    }

    @Test
    @DisplayName("Submit application - throws BadRequest when student CGPA ineligible")
    void submitApplication_IneligibleCgpa() {
        mockStudent.setCgpa(BigDecimal.valueOf(6.50));
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(opportunityRepository.findById(20L)).thenReturn(Optional.of(mockOpportunity));

        SubmitApplicationRequest request = SubmitApplicationRequest.builder().opportunityId(20L).build();
        assertThatThrownBy(() -> applicationService.submitApplication(request, studentUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("CGPA");
    }

    @Test
    @DisplayName("Submit application - throws DuplicateResourceException on duplicate apply")
    void submitApplication_DuplicateApply() {
        when(studentProfileRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(opportunityRepository.findById(20L)).thenReturn(Optional.of(mockOpportunity));
        when(branchRepository.findByOpportunityId(20L)).thenReturn(Collections.emptyList());
        when(yearRepository.findByOpportunityId(20L)).thenReturn(Collections.emptyList());
        when(applicationRepository.existsByStudentProfileIdAndOpportunityId(1L, 20L)).thenReturn(true);

        SubmitApplicationRequest request = SubmitApplicationRequest.builder().opportunityId(20L).build();
        assertThatThrownBy(() -> applicationService.submitApplication(request, studentUser))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already applied");
    }

    @Test
    @DisplayName("Update status - valid forward pipeline and auto-creates internship on SELECTED")
    void updateApplicationStatus_SelectedAutoCreatesInternship() {
        Application application = Application.builder()
                .id(1001L)
                .studentProfileId(1L)
                .opportunityId(20L)
                .status(ApplicationStatus.INTERVIEW)
                .matchPercentAtApply(BigDecimal.valueOf(80.00))
                .build();

        when(applicationRepository.findById(1001L)).thenReturn(Optional.of(application));
        when(opportunityRepository.findById(20L)).thenReturn(Optional.of(mockOpportunity));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));

        UpdateApplicationStatusRequest request = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.SELECTED)
                .build();

        ApplicationDto result = applicationService.updateApplicationStatus(1001L, request, companyUser);

        assertThat(result.getStatus()).isEqualTo(ApplicationStatus.SELECTED);
        verify(internshipRecordService).createInternshipRecord(application);
    }

    @Test
    @DisplayName("Update status - rejects invalid stage skip (APPLIED -> SELECTED)")
    void updateApplicationStatus_InvalidStageSkip() {
        Application application = Application.builder()
                .id(1001L)
                .studentProfileId(1L)
                .opportunityId(20L)
                .status(ApplicationStatus.APPLIED)
                .matchPercentAtApply(BigDecimal.valueOf(80.00))
                .build();

        when(applicationRepository.findById(1001L)).thenReturn(Optional.of(application));
        when(opportunityRepository.findById(20L)).thenReturn(Optional.of(mockOpportunity));

        UpdateApplicationStatusRequest request = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.SELECTED)
                .build();

        assertThatThrownBy(() -> applicationService.updateApplicationStatus(1001L, request, companyUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid recruitment stage transition");
    }
}

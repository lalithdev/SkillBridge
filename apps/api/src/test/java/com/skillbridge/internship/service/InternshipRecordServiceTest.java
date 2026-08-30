package com.skillbridge.internship.service;

import com.skillbridge.application.entity.Application;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.internship.dto.InternshipRecordDto;
import com.skillbridge.internship.dto.UpdateInternshipStatusRequest;
import com.skillbridge.internship.entity.InternshipRecord;
import com.skillbridge.internship.entity.InternshipStatus;
import com.skillbridge.internship.repository.CompanyFeedbackRepository;
import com.skillbridge.internship.repository.InternshipRecordRepository;
import com.skillbridge.opportunity.entity.Opportunity;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InternshipRecordServiceTest {

    @Mock
    private InternshipRecordRepository internshipRecordRepository;
    @Mock
    private CompanyFeedbackRepository companyFeedbackRepository;
    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private OpportunityRepository opportunityRepository;
    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @InjectMocks
    private InternshipRecordServiceImpl internshipRecordService;

    private CustomUserDetails companyUser;
    private InternshipRecord mockRecord;
    private Application mockApp;
    private Opportunity mockOpp;

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
                .status(InternshipStatus.UPCOMING)
                .build();

        mockApp = Application.builder()
                .id(10L)
                .studentProfileId(20L)
                .opportunityId(30L)
                .build();

        mockOpp = Opportunity.builder()
                .id(30L)
                .companyProfileId(100L)
                .title("Full Stack Intern")
                .build();
    }

    @Test
    @DisplayName("Create internship record - saves with UPCOMING status")
    void createInternshipRecord_Success() {
        when(internshipRecordRepository.findByApplicationId(10L)).thenReturn(Optional.empty());
        when(internshipRecordRepository.save(any(InternshipRecord.class))).thenAnswer(i -> {
            InternshipRecord r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        InternshipRecord result = internshipRecordService.createInternshipRecord(mockApp);

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(InternshipStatus.UPCOMING);
        verify(internshipRecordRepository).save(any(InternshipRecord.class));
    }

    @Test
    @DisplayName("Update internship status - valid transition UPCOMING -> ONGOING with dates")
    void updateInternshipStatus_ValidTransition() {
        when(internshipRecordRepository.findById(1L)).thenReturn(Optional.of(mockRecord));
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(mockApp));
        when(opportunityRepository.findById(30L)).thenReturn(Optional.of(mockOpp));
        when(internshipRecordRepository.save(any(InternshipRecord.class))).thenAnswer(i -> i.getArgument(0));

        UpdateInternshipStatusRequest request = UpdateInternshipStatusRequest.builder()
                .status(InternshipStatus.ONGOING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .build();

        InternshipRecordDto result = internshipRecordService.updateInternshipStatus(1L, request, companyUser);

        assertThat(result.getStatus()).isEqualTo(InternshipStatus.ONGOING);
        assertThat(result.getStartDate()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Update internship status - rejects invalid transition from COMPLETED")
    void updateInternshipStatus_RejectsFromCompleted() {
        mockRecord.setStatus(InternshipStatus.COMPLETED);
        when(internshipRecordRepository.findById(1L)).thenReturn(Optional.of(mockRecord));
        when(applicationRepository.findById(10L)).thenReturn(Optional.of(mockApp));
        when(opportunityRepository.findById(30L)).thenReturn(Optional.of(mockOpp));

        UpdateInternshipStatusRequest request = UpdateInternshipStatusRequest.builder()
                .status(InternshipStatus.ONGOING)
                .build();

        assertThatThrownBy(() -> internshipRecordService.updateInternshipStatus(1L, request, companyUser))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("COMPLETED");
    }
}

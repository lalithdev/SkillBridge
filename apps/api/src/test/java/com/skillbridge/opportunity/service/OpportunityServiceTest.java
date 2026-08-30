package com.skillbridge.opportunity.service;

import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.dto.PageResponse;
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
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpportunityServiceTest {

    @Mock
    private OpportunityRepository opportunityRepository;

    @Mock
    private RequiredSkillRepository requiredSkillRepository;

    @Mock
    private OpportunityRequiredBranchRepository requiredBranchRepository;

    @Mock
    private OpportunityRequiredYearRepository requiredYearRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private MatchingServiceImpl matchingService;

    @InjectMocks
    private OpportunityServiceImpl opportunityService;

    private CustomUserDetails companyUser;
    private CustomUserDetails otherCompanyUser;
    private CustomUserDetails studentUser;
    private CompanyProfile companyProfile;
    private Opportunity opportunity;
    private Skill javaSkill;
    private Department cseDept;

    @BeforeEach
    void setUp() {
        companyUser = CustomUserDetails.builder()
                .userId(1L)
                .email("hr@techcorp.com")
                .role(Role.COMPANY)
                .companyProfileId(10L)
                .build();

        otherCompanyUser = CustomUserDetails.builder()
                .userId(2L)
                .email("hr@othercorp.com")
                .role(Role.COMPANY)
                .companyProfileId(20L)
                .build();

        studentUser = CustomUserDetails.builder()
                .userId(3L)
                .email("student@test.edu")
                .role(Role.STUDENT)
                .studentProfileId(30L)
                .build();

        companyProfile = CompanyProfile.builder()
                .id(10L)
                .userId(1L)
                .name("Tech Corp")
                .build();

        opportunity = Opportunity.builder()
                .id(100L)
                .companyProfileId(10L)
                .title("Software Engineer Intern")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.REMOTE)
                .status(OpportunityStatus.OPEN)
                .build();

        javaSkill = Skill.builder().id(1L).name("Java").active(true).build();
        cseDept = Department.builder().id(1L).name("Computer Science").code("CSE").active(true).build();
    }

    @Nested
    @DisplayName("Create Opportunity")
    class CreateOpportunityTests {

        @Test
        @DisplayName("Should successfully create opportunity with OPEN status")
        void createOpportunitySuccess() {
            CreateOpportunityRequest req = CreateOpportunityRequest.builder()
                    .title("Backend Intern")
                    .type(OpportunityType.INTERNSHIP)
                    .mode(OpportunityMode.REMOTE)
                    .requiredSkillIds(List.of(1L))
                    .requiredDepartmentIds(List.of(1L))
                    .requiredYearsOfStudy(List.of(3, 4))
                    .build();

            when(companyProfileRepository.findById(10L)).thenReturn(Optional.of(companyProfile));
            when(skillRepository.findAllById(List.of(1L))).thenReturn(List.of(javaSkill));
            when(departmentRepository.findAllById(List.of(1L))).thenReturn(List.of(cseDept));
            when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(invocation -> {
                Opportunity opp = invocation.getArgument(0);
                opp.setId(100L);
                return opp;
            });

            OpportunityDetailDto result = opportunityService.createOpportunity(req, companyUser);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(100L);
            assertThat(result.getStatus()).isEqualTo(OpportunityStatus.OPEN);
            assertThat(result.getTitle()).isEqualTo("Backend Intern");

            verify(requiredSkillRepository, times(1)).saveAll(any());
            verify(requiredBranchRepository, times(1)).saveAll(any());
            verify(requiredYearRepository, times(1)).saveAll(any());
        }

        @Test
        @DisplayName("Should throw BadRequestException if required skills list is empty")
        void createOpportunityEmptySkills() {
            CreateOpportunityRequest req = CreateOpportunityRequest.builder()
                    .title("Backend Intern")
                    .type(OpportunityType.INTERNSHIP)
                    .requiredSkillIds(Collections.emptyList())
                    .build();

            when(companyProfileRepository.findById(10L)).thenReturn(Optional.of(companyProfile));

            assertThatThrownBy(() -> opportunityService.createOpportunity(req, companyUser))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("At least one required skill must be specified");
        }

        @Test
        @DisplayName("Should throw BadRequestException if skill is inactive")
        void createOpportunityInactiveSkill() {
            Skill inactiveSkill = Skill.builder().id(2L).name("Cobol").active(false).build();
            CreateOpportunityRequest req = CreateOpportunityRequest.builder()
                    .title("Backend Intern")
                    .type(OpportunityType.INTERNSHIP)
                    .requiredSkillIds(List.of(2L))
                    .build();

            when(companyProfileRepository.findById(10L)).thenReturn(Optional.of(companyProfile));
            when(skillRepository.findAllById(List.of(2L))).thenReturn(List.of(inactiveSkill));

            assertThatThrownBy(() -> opportunityService.createOpportunity(req, companyUser))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("inactive");
        }
    }

    @Nested
    @DisplayName("Update Opportunity & Ownership Enforcement")
    class UpdateOpportunityTests {

        @Test
        @DisplayName("Should throw ForbiddenException when another company tries to update")
        void updateOpportunityWrongOwner() {
            UpdateOpportunityRequest req = UpdateOpportunityRequest.builder()
                    .title("Hacked Title")
                    .type(OpportunityType.INTERNSHIP)
                    .requiredSkillIds(List.of(1L))
                    .build();

            when(opportunityRepository.findById(100L)).thenReturn(Optional.of(opportunity));

            assertThatThrownBy(() -> opportunityService.updateOpportunity(100L, req, otherCompanyUser))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessageContaining("Access denied: you do not own this opportunity");
        }

        @Test
        @DisplayName("Should successfully update opportunity when owner updates")
        void updateOpportunitySuccess() {
            UpdateOpportunityRequest req = UpdateOpportunityRequest.builder()
                    .title("Updated Title")
                    .type(OpportunityType.PLACEMENT)
                    .mode(OpportunityMode.ONSITE)
                    .requiredSkillIds(List.of(1L))
                    .build();

            when(opportunityRepository.findById(100L)).thenReturn(Optional.of(opportunity));
            when(companyProfileRepository.findById(10L)).thenReturn(Optional.of(companyProfile));
            when(skillRepository.findAllById(List.of(1L))).thenReturn(List.of(javaSkill));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);

            OpportunityDetailDto result = opportunityService.updateOpportunity(100L, req, companyUser);

            assertThat(result.getTitle()).isEqualTo("Updated Title");
            verify(requiredSkillRepository).deleteByOpportunityId(100L);
            verify(requiredSkillRepository).saveAll(any());
        }
    }

    @Nested
    @DisplayName("Status Update")
    class StatusUpdateTests {

        @Test
        @DisplayName("Should successfully toggle status to CLOSED")
        void updateStatusClosed() {
            when(opportunityRepository.findById(100L)).thenReturn(Optional.of(opportunity));
            when(companyProfileRepository.findById(10L)).thenReturn(Optional.of(companyProfile));
            when(opportunityRepository.save(any(Opportunity.class))).thenReturn(opportunity);
            when(requiredSkillRepository.findByOpportunityId(100L)).thenReturn(Collections.emptyList());
            when(requiredBranchRepository.findByOpportunityId(100L)).thenReturn(Collections.emptyList());
            when(requiredYearRepository.findByOpportunityId(100L)).thenReturn(Collections.emptyList());

            OpportunityStatusUpdateRequest req = new OpportunityStatusUpdateRequest(OpportunityStatus.CLOSED);
            OpportunityDetailDto result = opportunityService.updateOpportunityStatus(100L, req, companyUser);

            assertThat(opportunity.getStatus()).isEqualTo(OpportunityStatus.CLOSED);
        }

        @Test
        @DisplayName("Should reject DRAFT status update via API")
        void updateStatusDraftRejected() {
            when(opportunityRepository.findById(100L)).thenReturn(Optional.of(opportunity));

            OpportunityStatusUpdateRequest req = new OpportunityStatusUpdateRequest(OpportunityStatus.DRAFT);
            assertThatThrownBy(() -> opportunityService.updateOpportunityStatus(100L, req, companyUser))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessageContaining("Cannot set status to DRAFT via API");
        }
    }
}

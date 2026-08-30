package com.skillbridge.admin.service;

import com.skillbridge.admin.dto.AdminUserDto;
import com.skillbridge.admin.dto.ModerateOpportunityRequest;
import com.skillbridge.admin.dto.UpdateUserStatusRequest;
import com.skillbridge.admin.dto.UpdateVerificationStatusRequest;
import com.skillbridge.admin.dto.VerificationItemDto;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.exception.BadRequestException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private CollegeRepository collegeRepository;
    @Mock
    private CompanyProfileRepository companyProfileRepository;
    @Mock
    private OpportunityRepository opportunityRepository;
    @Mock
    private OpportunityService opportunityService;

    @InjectMocks
    private AdminServiceImpl adminService;

    private User mockUser;
    private College mockCollege;
    private CompanyProfile mockCompany;
    private Opportunity mockOpportunity;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("testuser@skillbridge.org")
                .role(Role.STUDENT)
                .active(true)
                .createdAt(Instant.now())
                .build();

        mockCollege = College.builder()
                .id(10L)
                .name("Apex Engineering College")
                .contactEmail("apex@college.edu")
                .verificationStatus(VerificationStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        mockCompany = CompanyProfile.builder()
                .id(20L)
                .name("Apex Software")
                .contactEmail("hr@apexsoftware.com")
                .verificationStatus(VerificationStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        mockOpportunity = Opportunity.builder()
                .id(100L)
                .title("Inappropriate JD")
                .status(OpportunityStatus.OPEN)
                .build();
    }

    @Test
    @DisplayName("List users - returns paginated users with filters")
    void listUsers_Success() {
        Page<User> page = new PageImpl<>(List.of(mockUser));
        when(userRepository.findUsers(eq(Role.STUDENT), eq(true), any(), any(Pageable.class)))
                .thenReturn(page);

        PageResponse<AdminUserDto> result = adminService.listUsers(Role.STUDENT, true, null, 0, 10);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("testuser@skillbridge.org");
    }

    @Test
    @DisplayName("Update user status - activate or deactivate user")
    void updateUserStatus_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        UpdateUserStatusRequest request = UpdateUserStatusRequest.builder().active(false).build();
        AdminUserDto updated = adminService.updateUserStatus(1L, request);

        assertThat(updated.isActive()).isFalse();
    }

    @Test
    @DisplayName("Get pending verifications - returns both colleges and companies")
    void getPendingVerifications_All() {
        when(collegeRepository.findByVerificationStatus(VerificationStatus.PENDING)).thenReturn(List.of(mockCollege));
        when(companyProfileRepository.findByVerificationStatus(VerificationStatus.PENDING)).thenReturn(List.of(mockCompany));

        List<VerificationItemDto> items = adminService.getPendingVerifications(null, VerificationStatus.PENDING);

        assertThat(items).hasSize(2);
    }

    @Test
    @DisplayName("Update verification status - verify college sets verifiedAt")
    void updateVerificationStatus_College() {
        when(collegeRepository.findById(10L)).thenReturn(Optional.of(mockCollege));
        when(collegeRepository.save(any(College.class))).thenAnswer(i -> i.getArgument(0));

        UpdateVerificationStatusRequest request = UpdateVerificationStatusRequest.builder()
                .status(VerificationStatus.VERIFIED)
                .build();

        VerificationItemDto result = adminService.updateVerificationStatus("COLLEGE", 10L, request);

        assertThat(result.getVerificationStatus()).isEqualTo(VerificationStatus.VERIFIED);
        assertThat(mockCollege.getVerifiedAt()).isNotNull();
    }

    @Test
    @DisplayName("Moderate opportunity - closes posting successfully")
    void moderateOpportunity_Success() {
        when(opportunityRepository.findById(100L)).thenReturn(Optional.of(mockOpportunity));
        when(opportunityRepository.save(any(Opportunity.class))).thenAnswer(i -> i.getArgument(0));
        when(opportunityService.getOpportunityById(100L, null)).thenReturn(
                OpportunityDetailDto.builder().id(100L).status(OpportunityStatus.CLOSED).build()
        );

        ModerateOpportunityRequest request = ModerateOpportunityRequest.builder()
                .status(OpportunityStatus.CLOSED)
                .build();

        OpportunityDetailDto result = adminService.moderateOpportunity(100L, request);

        assertThat(result.getStatus()).isEqualTo(OpportunityStatus.CLOSED);
        assertThat(mockOpportunity.getStatus()).isEqualTo(OpportunityStatus.CLOSED);
    }
}

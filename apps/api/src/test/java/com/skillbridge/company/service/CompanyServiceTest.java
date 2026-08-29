package com.skillbridge.company.service;

import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.company.dto.CompanyProfileDto;
import com.skillbridge.company.dto.UpdateCompanyProfileRequest;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        companyService = new CompanyServiceImpl(companyProfileRepository);
    }

    @Test
    @DisplayName("getCompanyProfile - returns profile for authenticated company user")
    void getCompanyProfileSuccess() {
        CustomUserDetails user = CustomUserDetails.builder()
                .userId(100L)
                .email("recruiter@acme.com")
                .role(Role.COMPANY)
                .companyProfileId(5L)
                .authorities(Collections.emptyList())
                .build();

        CompanyProfile profile = CompanyProfile.builder()
                .id(5L)
                .userId(100L)
                .name("Acme Corp")
                .industry("Tech")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build();

        when(companyProfileRepository.findByUserId(100L)).thenReturn(Optional.of(profile));

        CompanyProfileDto result = companyService.getCompanyProfile(user);

        assertThat(result.getId()).isEqualTo(5L);
        assertThat(result.getName()).isEqualTo("Acme Corp");
        assertThat(result.getIndustry()).isEqualTo("Tech");
        assertThat(result.getVerificationStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    @DisplayName("updateCompanyProfile - updates company details")
    void updateCompanyProfileSuccess() {
        CustomUserDetails user = CustomUserDetails.builder()
                .userId(100L)
                .email("recruiter@acme.com")
                .role(Role.COMPANY)
                .companyProfileId(5L)
                .authorities(Collections.emptyList())
                .build();

        CompanyProfile profile = CompanyProfile.builder()
                .id(5L)
                .userId(100L)
                .name("Acme Corp")
                .verificationStatus(VerificationStatus.PENDING)
                .build();

        when(companyProfileRepository.findByUserId(100L)).thenReturn(Optional.of(profile));
        when(companyProfileRepository.save(any(CompanyProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateCompanyProfileRequest updateReq = UpdateCompanyProfileRequest.builder()
                .name("Acme Corporation International")
                .industry("Fintech")
                .location("Bangalore")
                .website("https://acme.com")
                .build();

        CompanyProfileDto result = companyService.updateCompanyProfile(user, updateReq);

        assertThat(result.getName()).isEqualTo("Acme Corporation International");
        assertThat(result.getIndustry()).isEqualTo("Fintech");
        assertThat(result.getLocation()).isEqualTo("Bangalore");
    }

    @Test
    @DisplayName("getCompanyById - returns public company profile")
    void getCompanyByIdSuccess() {
        CompanyProfile profile = CompanyProfile.builder()
                .id(7L)
                .userId(200L)
                .name("Global Tech")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build();

        when(companyProfileRepository.findById(7L)).thenReturn(Optional.of(profile));

        CompanyProfileDto result = companyService.getCompanyById(7L);

        assertThat(result.getId()).isEqualTo(7L);
        assertThat(result.getName()).isEqualTo("Global Tech");
    }

    @Test
    @DisplayName("getCompanyById - throws ResourceNotFoundException when company ID not found")
    void getCompanyByIdNotFound() {
        when(companyProfileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> companyService.getCompanyById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Company profile not found");
    }
}

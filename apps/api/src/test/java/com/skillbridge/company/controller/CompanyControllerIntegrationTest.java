package com.skillbridge.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.company.dto.UpdateCompanyProfileRequest;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.user.entity.Role;
import com.skillbridge.user.entity.User;
import com.skillbridge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CompanyControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String companyToken;
    private String studentToken;
    private Long companyProfileId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        companyProfileRepository.deleteAll();
        userRepository.deleteAll();

        User companyUser = userRepository.save(User.builder()
                .email("hr@innovate.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COMPANY)
                .active(true)
                .build());

        CompanyProfile profile = companyProfileRepository.save(CompanyProfile.builder()
                .userId(companyUser.getId())
                .name("Innovate Labs")
                .industry("AI & Cloud")
                .verificationStatus(VerificationStatus.PENDING)
                .build());
        companyProfileId = profile.getId();

        companyToken = jwtService.generateToken(companyUser.getId(), companyUser.getEmail(), companyUser.getRole(), null, companyProfileId, null);

        User student = userRepository.save(User.builder()
                .email("student@test.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        studentToken = jwtService.generateToken(student.getId(), student.getEmail(), student.getRole(), null, null, 1L);
    }

    @Test
    @DisplayName("GET /api/v1/companies/profile - Should return authenticated company profile")
    void getCompanyProfileAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/companies/profile")
                        .header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(companyProfileId.intValue())))
                .andExpect(jsonPath("$.name", is("Innovate Labs")))
                .andExpect(jsonPath("$.industry", is("AI & Cloud")))
                .andExpect(jsonPath("$.verificationStatus", is("PENDING")));
    }

    @Test
    @DisplayName("PUT /api/v1/companies/profile - Should update company profile")
    void updateCompanyProfileSuccess() throws Exception {
        UpdateCompanyProfileRequest updateReq = UpdateCompanyProfileRequest.builder()
                .name("Innovate Labs Global")
                .industry("Artificial Intelligence")
                .location("Hyderabad")
                .website("https://innovatelabs.io")
                .contactEmail("contact@innovatelabs.io")
                .build();

        mockMvc.perform(put("/api/v1/companies/profile")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Innovate Labs Global")))
                .andExpect(jsonPath("$.location", is("Hyderabad")))
                .andExpect(jsonPath("$.website", is("https://innovatelabs.io")));
    }

    @Test
    @DisplayName("GET /api/v1/companies/{id} - Should return public company profile to any authenticated user")
    void getCompanyByIdAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/companies/" + companyProfileId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(companyProfileId.intValue())))
                .andExpect(jsonPath("$.name", is("Innovate Labs")));
    }

    @Test
    @DisplayName("PUT /api/v1/companies/profile - Should return 403 Forbidden when STUDENT tries to update company profile")
    void updateCompanyProfileAsStudentForbidden() throws Exception {
        UpdateCompanyProfileRequest updateReq = UpdateCompanyProfileRequest.builder()
                .name("Hacker Corp")
                .build();

        mockMvc.perform(put("/api/v1/companies/profile")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isForbidden());
    }
}

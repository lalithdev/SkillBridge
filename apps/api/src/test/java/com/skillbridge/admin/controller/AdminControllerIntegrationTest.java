package com.skillbridge.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.admin.dto.ModerateOpportunityRequest;
import com.skillbridge.admin.dto.UpdateUserStatusRequest;
import com.skillbridge.admin.dto.UpdateVerificationStatusRequest;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.internship.repository.CompanyFeedbackRepository;
import com.skillbridge.internship.repository.InternshipRecordRepository;
import com.skillbridge.opportunity.entity.Opportunity;
import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.opportunity.repository.OpportunityRequiredBranchRepository;
import com.skillbridge.opportunity.repository.OpportunityRequiredYearRepository;
import com.skillbridge.opportunity.repository.RequiredSkillRepository;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AdminControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private RequiredSkillRepository requiredSkillRepository;

    @Autowired
    private OpportunityRequiredBranchRepository requiredBranchRepository;

    @Autowired
    private OpportunityRequiredYearRepository requiredYearRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InternshipRecordRepository internshipRecordRepository;

    @Autowired
    private CompanyFeedbackRepository companyFeedbackRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String studentToken;
    private Long targetUserId;
    private Long pendingCollegeId;
    private Long pendingCompanyId;
    private Long opportunityId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        companyFeedbackRepository.deleteAll();
        internshipRecordRepository.deleteAll();
        applicationRepository.deleteAll();
        requiredSkillRepository.deleteAll();
        requiredBranchRepository.deleteAll();
        requiredYearRepository.deleteAll();
        opportunityRepository.deleteAll();
        studentSkillRepository.deleteAll();
        studentProfileRepository.deleteAll();
        companyProfileRepository.deleteAll();
        collegeRepository.deleteAll();
        departmentRepository.deleteAll();
        skillRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Admin User
        User admin = userRepository.save(User.builder()
                .email("admin@skillbridge.org")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.ADMIN)
                .active(true)
                .build());
        adminToken = jwtService.generateToken(admin.getId(), admin.getEmail(), admin.getRole(), null, null, null);

        // 2. Regular User
        User student = userRepository.save(User.builder()
                .email("student@college.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        targetUserId = student.getId();
        studentToken = jwtService.generateToken(student.getId(), student.getEmail(), student.getRole(), null, null, null);

        // 3. Pending College
        College col = collegeRepository.save(College.builder()
                .userId(admin.getId())
                .name("Pending University")
                .contactEmail("admin@pendinguniv.edu")
                .website("https://pendinguniv.edu")
                .verificationStatus(VerificationStatus.PENDING)
                .build());
        pendingCollegeId = col.getId();

        // 4. Pending Company
        CompanyProfile comp = companyProfileRepository.save(CompanyProfile.builder()
                .userId(admin.getId())
                .name("Pending Corp")
                .contactEmail("contact@pendingcorp.com")
                .website("https://pendingcorp.com")
                .verificationStatus(VerificationStatus.PENDING)
                .build());
        pendingCompanyId = comp.getId();

        // 5. Open Opportunity
        Opportunity opp = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(comp.getId())
                .title("Spam Job Posting")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.REMOTE)
                .status(OpportunityStatus.OPEN)
                .build());
        opportunityId = opp.getId();
    }

    @Test
    @DisplayName("GET /api/v1/admin/users - Admin lists all users with pagination")
    void listUsers_Success() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    @DisplayName("GET /api/v1/admin/users - Non-admin receives 403 Forbidden")
    void listUsers_Forbidden() throws Exception {
        mockMvc.perform(get("/api/v1/admin/users")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/v1/admin/users/{id}/status - Deactivate user account")
    void updateUserStatus_Deactivate() throws Exception {
        UpdateUserStatusRequest req = UpdateUserStatusRequest.builder().active(false).build();

        mockMvc.perform(patch("/api/v1/admin/users/" + targetUserId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(false)));
    }

    @Test
    @DisplayName("GET & PATCH /api/v1/admin/verifications - Manage verification queue")
    void manageVerifications_ApproveCollege() throws Exception {
        // List pending
        mockMvc.perform(get("/api/v1/admin/verifications?status=PENDING")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Approve college
        UpdateVerificationStatusRequest approveReq = UpdateVerificationStatusRequest.builder()
                .status(VerificationStatus.VERIFIED)
                .build();

        mockMvc.perform(patch("/api/v1/admin/verifications/COLLEGE/" + pendingCollegeId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(approveReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus", is("VERIFIED")));
    }

    @Test
    @DisplayName("PATCH /api/v1/admin/opportunities/{id}/status - Moderate and close opportunity")
    void moderateOpportunity_Close() throws Exception {
        ModerateOpportunityRequest req = ModerateOpportunityRequest.builder()
                .status(OpportunityStatus.CLOSED)
                .build();

        mockMvc.perform(patch("/api/v1/admin/opportunities/" + opportunityId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CLOSED")));
    }
}

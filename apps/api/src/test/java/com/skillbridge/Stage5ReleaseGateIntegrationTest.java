package com.skillbridge;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.admin.dto.ModerateOpportunityRequest;
import com.skillbridge.admin.dto.UpdateUserStatusRequest;
import com.skillbridge.admin.dto.UpdateVerificationStatusRequest;
import com.skillbridge.application.dto.SubmitApplicationRequest;
import com.skillbridge.application.dto.UpdateApplicationStatusRequest;
import com.skillbridge.application.entity.ApplicationStatus;
import com.skillbridge.application.repository.ApplicationRepository;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.internship.dto.SubmitFeedbackRequest;
import com.skillbridge.internship.dto.UpdateInternshipStatusRequest;
import com.skillbridge.internship.entity.InternshipStatus;
import com.skillbridge.internship.repository.CompanyFeedbackRepository;
import com.skillbridge.internship.repository.InternshipRecordRepository;
import com.skillbridge.opportunity.dto.CreateOpportunityRequest;
import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.opportunity.repository.OpportunityRequiredBranchRepository;
import com.skillbridge.opportunity.repository.OpportunityRequiredYearRepository;
import com.skillbridge.opportunity.repository.RequiredSkillRepository;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.entity.StudentSkill;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class Stage5ReleaseGateIntegrationTest {

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
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

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
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

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
    }

    @Test
    @DisplayName("STAGE 5 RELEASE GATE: End-to-End Vertical Slice across all 5 domains")
    void completeStage5EndToEndVerticalSlice() throws Exception {

        // ==========================================
        // 1. SETUP: Create Core Actors and Master Taxonomy
        // ==========================================
        Skill java = skillRepository.save(Skill.builder().name("Java").category("Backend").active(true).build());
        Skill spring = skillRepository.save(Skill.builder().name("Spring Boot").category("Framework").active(true).build());
        Skill react = skillRepository.save(Skill.builder().name("React").category("Frontend").active(true).build());

        Department cse = departmentRepository.save(Department.builder().name("Computer Science").code("CSE").active(true).build());

        // Admin User
        User adminUser = userRepository.save(User.builder().email("superadmin@skillbridge.org").password(passwordEncoder.encode("Password@123")).role(Role.ADMIN).active(true).build());
        String adminToken = jwtService.generateToken(adminUser.getId(), adminUser.getEmail(), adminUser.getRole(), null, null, null);

        // College User
        User colUser = userRepository.save(User.builder().email("dean@mit.edu").password(passwordEncoder.encode("Password@123")).role(Role.COLLEGE).active(true).build());
        College mit = collegeRepository.save(College.builder().userId(colUser.getId()).name("MIT Engineering").verificationStatus(VerificationStatus.PENDING).build());
        String collegeToken = jwtService.generateToken(colUser.getId(), colUser.getEmail(), colUser.getRole(), mit.getId(), null, null);

        // Company User
        User compUser = userRepository.save(User.builder().email("talent@google.com").password(passwordEncoder.encode("Password@123")).role(Role.COMPANY).active(true).build());
        CompanyProfile google = companyProfileRepository.save(CompanyProfile.builder().userId(compUser.getId()).name("Google LLC").verificationStatus(VerificationStatus.PENDING).build());
        String companyToken = jwtService.generateToken(compUser.getId(), compUser.getEmail(), compUser.getRole(), null, google.getId(), null);

        // Student User (has Java and Spring Boot -> 2 of 2 required skills)
        User stuUser = userRepository.save(User.builder().email("sundar@mit.edu").password(passwordEncoder.encode("Password@123")).role(Role.STUDENT).active(true).build());
        StudentProfile sundar = studentProfileRepository.save(StudentProfile.builder()
                .userId(stuUser.getId())
                .collegeId(mit.getId())
                .firstName("Sundar")
                .lastName("Pichai")
                .departmentId(cse.getId())
                .yearOfStudy((short) 4)
                .cgpa(BigDecimal.valueOf(9.50))
                .resumePath("uploads/resumes/sundar.pdf")
                .build());
        String studentToken = jwtService.generateToken(stuUser.getId(), stuUser.getEmail(), stuUser.getRole(), mit.getId(), null, sundar.getId());

        studentSkillRepository.save(StudentSkill.builder().studentProfileId(sundar.getId()).skillId(java.getId()).build());
        studentSkillRepository.save(StudentSkill.builder().studentProfileId(sundar.getId()).skillId(spring.getId()).build());

        // ==========================================
        // 2. DOMAIN 5: Admin Moderation & Organization Verification
        // ==========================================
        // Admin approves College verification
        UpdateVerificationStatusRequest verifyReq = UpdateVerificationStatusRequest.builder().status(VerificationStatus.VERIFIED).build();
        mockMvc.perform(patch("/api/v1/admin/verifications/COLLEGE/" + mit.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus", is("VERIFIED")));

        // Admin approves Company verification
        mockMvc.perform(patch("/api/v1/admin/verifications/COMPANY/" + google.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(verifyReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verificationStatus", is("VERIFIED")));

        // ==========================================
        // 3. STAGE 4 / OPPORTUNITY: Company Posts Opportunity
        // ==========================================
        CreateOpportunityRequest oppReq = CreateOpportunityRequest.builder()
                .title("Software Engineering Summer Intern")
                .description("Build globally scalable backend cloud microservices")
                .type(OpportunityType.INTERNSHIP)
                .location("Mountain View, CA")
                .mode(OpportunityMode.HYBRID)
                .durationWeeks(12)
                .stipendAmount(BigDecimal.valueOf(8000.00))
                .minCgpa(BigDecimal.valueOf(8.00))
                .applicationDeadline(LocalDate.now().plusMonths(2))
                .requiredSkillIds(List.of(java.getId(), spring.getId()))
                .requiredDepartmentIds(List.of(cse.getId()))
                .requiredYearsOfStudy(List.of(4))
                .build();

        String oppResponse = mockMvc.perform(post("/api/v1/opportunities")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oppReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andReturn().getResponse().getContentAsString();

        Long oppId = objectMapper.readTree(oppResponse).get("id").asLong();

        // ==========================================
        // 4. DOMAIN 1: Student Applies & Pipeline Progress
        // ==========================================
        // Student applies
        SubmitApplicationRequest applyReq = SubmitApplicationRequest.builder().opportunityId(oppId).build();
        String appResponse = mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(applyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("APPLIED")))
                .andExpect(jsonPath("$.matchPercentAtApply", is(100.0)))
                .andReturn().getResponse().getContentAsString();

        Long appId = objectMapper.readTree(appResponse).get("id").asLong();

        // Student views their applications
        mockMvc.perform(get("/api/v1/applications/my")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].opportunityTitle", is("Software Engineering Summer Intern")));

        // Company views applicants ranked by match %
        mockMvc.perform(get("/api/v1/opportunities/" + oppId + "/applications")
                        .header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].studentName", is("Sundar Pichai")))
                .andExpect(jsonPath("$.content[0].matchPercentAtApply", is(100.0)));

        // Company advances recruitment pipeline: APPLIED -> UNDER_REVIEW -> SHORTLISTED -> INTERVIEW -> SELECTED
        List<ApplicationStatus> pipeline = List.of(
                ApplicationStatus.UNDER_REVIEW,
                ApplicationStatus.SHORTLISTED,
                ApplicationStatus.INTERVIEW,
                ApplicationStatus.SELECTED
        );

        for (ApplicationStatus nextStage : pipeline) {
            UpdateApplicationStatusRequest stageReq = UpdateApplicationStatusRequest.builder().status(nextStage).build();
            mockMvc.perform(patch("/api/v1/applications/" + appId + "/status")
                            .header("Authorization", "Bearer " + companyToken)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(stageReq)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is(nextStage.name())));
        }

        // ==========================================
        // 5. DOMAIN 2: Internships, Outcomes & Feedback
        // ==========================================
        // Student retrieves confirmed internship (auto-created with UPCOMING status)
        String myInternshipsResponse = mockMvc.perform(get("/api/v1/internships/my")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("UPCOMING")))
                .andExpect(jsonPath("$[0].companyName", is("Google LLC")))
                .andReturn().getResponse().getContentAsString();

        Long internshipId = objectMapper.readTree(myInternshipsResponse).get(0).get("id").asLong();

        // Company moves internship to ONGOING
        UpdateInternshipStatusRequest ongoingReq = UpdateInternshipStatusRequest.builder()
                .status(InternshipStatus.ONGOING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .build();

        mockMvc.perform(patch("/api/v1/internships/" + internshipId + "/status")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ongoingReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ONGOING")));

        // Company moves internship to COMPLETED
        UpdateInternshipStatusRequest completedReq = UpdateInternshipStatusRequest.builder()
                .status(InternshipStatus.COMPLETED)
                .build();

        mockMvc.perform(patch("/api/v1/internships/" + internshipId + "/status")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(completedReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("COMPLETED")));

        // Company submits feedback
        SubmitFeedbackRequest feedbackReq = SubmitFeedbackRequest.builder()
                .feedbackText("Superb architectural intuition and exceptional execution excellence throughout the internship.")
                .build();

        mockMvc.perform(post("/api/v1/internships/" + internshipId + "/feedback")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(feedbackReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentName", is("Sundar Pichai")))
                .andExpect(jsonPath("$.feedbackText", containsString("execution excellence")));

        // ==========================================
        // 6. DOMAIN 3: College Oversight & Roster
        // ==========================================
        // College views student roster
        mockMvc.perform(get("/api/v1/colleges/students")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName", is("Sundar")))
                .andExpect(jsonPath("$.content[0].skillCount", is(2)));

        // College views department breakdown
        mockMvc.perform(get("/api/v1/colleges/departments")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].departmentCode", is("CSE")))
                .andExpect(jsonPath("$[0].studentCount", is(1)));

        // College views aggregated company feedback
        mockMvc.perform(get("/api/v1/colleges/feedback")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].companyName", is("Google LLC")));

        // ==========================================
        // 7. DOMAIN 4: Analytics & Skill-Gap Intelligence
        // ==========================================
        // College views skill availability
        mockMvc.perform(get("/api/v1/analytics/skills/availability")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].availabilityPercent", is(100.0))) // Java
                .andExpect(jsonPath("$[1].availabilityPercent", is(100.0))) // Spring Boot
                .andExpect(jsonPath("$[2].availabilityPercent", is(0.0)));   // React

        // College views skill demand
        mockMvc.perform(get("/api/v1/analytics/skills/demand")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].demandPercent", is(100.0))) // Java
                .andExpect(jsonPath("$[1].demandPercent", is(100.0))) // Spring Boot
                .andExpect(jsonPath("$[2].demandPercent", is(0.0)));   // React

        // College views skill gap dashboard
        mockMvc.perform(get("/api/v1/analytics/skills/gap")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalStudents", is(1)))
                .andExpect(jsonPath("$.totalOpenOpportunities", is(1)))
                .andExpect(jsonPath("$.gaps", hasSize(3)));

        // College views placement funnel
        mockMvc.perform(get("/api/v1/analytics/placement-funnel")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.selected", is(1)))
                .andExpect(jsonPath("$.totalApplications", is(1)));

        // ==========================================
        // 8. DOMAIN 5: Admin Moderation
        // ==========================================
        // Admin closes opportunity
        ModerateOpportunityRequest moderateReq = ModerateOpportunityRequest.builder().status(OpportunityStatus.CLOSED).build();
        mockMvc.perform(patch("/api/v1/admin/opportunities/" + oppId + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(moderateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CLOSED")));

        // Admin deactivates student
        UpdateUserStatusRequest deactReq = UpdateUserStatusRequest.builder().active(false).build();
        mockMvc.perform(patch("/api/v1/admin/users/" + stuUser.getId() + "/status")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deactReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isActive", is(false)));
    }
}

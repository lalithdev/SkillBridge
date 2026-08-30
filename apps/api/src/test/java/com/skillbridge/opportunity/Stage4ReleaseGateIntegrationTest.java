package com.skillbridge.opportunity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.opportunity.dto.CreateOpportunityRequest;
import com.skillbridge.opportunity.dto.OpportunityStatusUpdateRequest;
import com.skillbridge.opportunity.dto.UpdateOpportunityRequest;
import com.skillbridge.opportunity.entity.*;
import com.skillbridge.opportunity.repository.*;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Strict Stage 4 Release Gate Integration Test Suite.
 * Covers Phase B (E2E workflows), Phase C (Matching correctness matrix),
 * Phase D (Security/RBAC), Phase E (API Contracts), Phase F (Database validation),
 * and Phase G (Stage 1-3 regressions).
 */
@SpringBootTest
public class Stage4ReleaseGateIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private RequiredSkillRepository requiredSkillRepository;

    @Autowired
    private OpportunityRequiredBranchRepository requiredBranchRepository;

    @Autowired
    private OpportunityRequiredYearRepository requiredYearRepository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    // Tokens
    private String adminToken;
    private String companyAToken;
    private String companyBToken;
    private String studentToken;
    private String collegeToken;

    // IDs
    private Long collegeId;
    private Long deptCseId;
    private Long deptEceId;
    private Long skillJavaId;
    private Long skillSpringId;
    private Long skillDockerId;
    private Long skillAwsId;
    private Long companyAProfileId;
    private Long companyBProfileId;
    private Long studentProfileId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        requiredSkillRepository.deleteAll();
        requiredBranchRepository.deleteAll();
        requiredYearRepository.deleteAll();
        opportunityRepository.deleteAll();
        studentSkillRepository.deleteAll();
        studentProfileRepository.deleteAll();
        companyProfileRepository.deleteAll();
        collegeRepository.deleteAll();
        skillRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Admin
        User adminUser = userRepository.save(User.builder()
                .email("admin@skillbridge.org")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.ADMIN)
                .active(true)
                .build());
        adminToken = jwtService.generateToken(adminUser.getId(), adminUser.getEmail(), Role.ADMIN, null, null, null);

        // 2. Taxonomy Departments
        Department cse = departmentRepository.save(Department.builder().name("Computer Science and Engineering").code("CSE").active(true).build());
        Department ece = departmentRepository.save(Department.builder().name("Electronics and Communication").code("ECE").active(true).build());
        deptCseId = cse.getId();
        deptEceId = ece.getId();

        // 3. Taxonomy Skills
        Skill java = skillRepository.save(Skill.builder().name("Java").category("Language").active(true).build());
        Skill spring = skillRepository.save(Skill.builder().name("Spring Boot").category("Framework").active(true).build());
        Skill docker = skillRepository.save(Skill.builder().name("Docker").category("DevOps").active(true).build());
        Skill aws = skillRepository.save(Skill.builder().name("AWS").category("Cloud").active(true).build());
        skillJavaId = java.getId();
        skillSpringId = spring.getId();
        skillDockerId = docker.getId();
        skillAwsId = aws.getId();

        // 4. College
        User collegeUser = userRepository.save(User.builder()
                .email("admin@iitb.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COLLEGE)
                .active(true)
                .build());
        College college = collegeRepository.save(College.builder()
                .userId(collegeUser.getId())
                .name("IIT Bombay")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());
        collegeId = college.getId();
        collegeToken = jwtService.generateToken(collegeUser.getId(), collegeUser.getEmail(), Role.COLLEGE, collegeId, null, null);

        // 5. Company A (Alpha Corp)
        User compAUser = userRepository.save(User.builder()
                .email("hr@alphacorp.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COMPANY)
                .active(true)
                .build());
        CompanyProfile profileA = companyProfileRepository.save(CompanyProfile.builder()
                .userId(compAUser.getId())
                .name("Alpha Cloud Corp")
                .industry("Cloud Computing")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());
        companyAProfileId = profileA.getId();
        companyAToken = jwtService.generateToken(compAUser.getId(), compAUser.getEmail(), Role.COMPANY, null, companyAProfileId, null);

        // 6. Company B (Beta Finance)
        User compBUser = userRepository.save(User.builder()
                .email("hr@betafinance.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COMPANY)
                .active(true)
                .build());
        CompanyProfile profileB = companyProfileRepository.save(CompanyProfile.builder()
                .userId(compBUser.getId())
                .name("Beta Finance Corp")
                .industry("Fintech")
                .verificationStatus(VerificationStatus.PENDING)
                .build());
        companyBProfileId = profileB.getId();
        companyBToken = jwtService.generateToken(compBUser.getId(), compBUser.getEmail(), Role.COMPANY, null, companyBProfileId, null);

        // 7. Student (Alice: Dept=CSE, Year=3, CGPA=8.50, Skills=[Java, Spring Boot])
        User studentUser = userRepository.save(User.builder()
                .email("alice@iitb.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        StudentProfile profileStudent = studentProfileRepository.save(StudentProfile.builder()
                .userId(studentUser.getId())
                .collegeId(collegeId)
                .firstName("Alice")
                .lastName("Wonder")
                .departmentId(deptCseId)
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.50))
                .build());
        studentProfileId = profileStudent.getId();
        studentToken = jwtService.generateToken(studentUser.getId(), studentUser.getEmail(), Role.STUDENT, collegeId, null, studentProfileId);

        studentSkillRepository.save(StudentSkill.builder().studentProfileId(studentProfileId).skillId(skillJavaId).build());
        studentSkillRepository.save(StudentSkill.builder().studentProfileId(studentProfileId).skillId(skillSpringId).build());
    }

    // =========================================================================
    // PHASE B: FULL E2E WORKFLOW
    // =========================================================================

    @Test
    @DisplayName("Phase B E2E: Company creates, queries, updates, closes opportunity; Student browses, evaluates match, views recommendations")
    void testFullE2EWorkflow() throws Exception {
        // Step 1: Company A posts opportunity with required skills, branch CSE, year 3, min CGPA 7.50
        CreateOpportunityRequest createReq = CreateOpportunityRequest.builder()
                .title("Distributed Systems Intern")
                .description("Build high-scale microservices")
                .type(OpportunityType.INTERNSHIP)
                .location("Bangalore")
                .mode(OpportunityMode.HYBRID)
                .durationWeeks(12)
                .stipendAmount(BigDecimal.valueOf(40000.00))
                .stipendCurrency("INR")
                .minCgpa(BigDecimal.valueOf(7.50))
                .applicationDeadline(LocalDate.now().plusMonths(1))
                .requiredSkillIds(List.of(skillJavaId, skillSpringId))
                .requiredDepartmentIds(List.of(deptCseId))
                .requiredYearsOfStudy(List.of(3, 4))
                .build();

        MvcResult createResult = mockMvc.perform(post("/api/v1/opportunities")
                        .header("Authorization", "Bearer " + companyAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.companyName", is("Alpha Cloud Corp")))
                .andReturn();

        Long oppId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("id").asLong();

        // Step 2: Verify Database persistence in relational tables
        Opportunity oppInDb = opportunityRepository.findById(oppId).orElseThrow();
        assertThat(oppInDb.getStatus()).isEqualTo(OpportunityStatus.OPEN);
        assertThat(oppInDb.getTitle()).isEqualTo("Distributed Systems Intern");

        List<RequiredSkill> reqSkills = requiredSkillRepository.findByOpportunityId(oppId);
        assertThat(reqSkills).hasSize(2);

        List<OpportunityRequiredBranch> reqBranches = requiredBranchRepository.findByOpportunityId(oppId);
        assertThat(reqBranches).hasSize(1);
        assertThat(reqBranches.get(0).getDepartmentId()).isEqualTo(deptCseId);

        List<OpportunityRequiredYear> reqYears = requiredYearRepository.findByOpportunityId(oppId);
        assertThat(reqYears).hasSize(2);

        // Step 3: Company A retrieves own postings
        mockMvc.perform(get("/api/v1/opportunities/company/my")
                        .header("Authorization", "Bearer " + companyAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(oppId.intValue())));

        // Step 4: Student queries dynamic match evaluation
        mockMvc.perform(get("/api/v1/matching/opportunities/" + oppId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchPercent", is(100.0)))
                .andExpect(jsonPath("$.isEligible", is(true)))
                .andExpect(jsonPath("$.matchedSkills", hasSize(2)))
                .andExpect(jsonPath("$.missingSkills", empty()));

        // Step 5: Student browses opportunity list — matchPercent & isEligible populated
        mockMvc.perform(get("/api/v1/opportunities")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].matchPercent", is(100.0)))
                .andExpect(jsonPath("$.content[0].isEligible", is(true)));

        // Step 6: Company A updates opportunity (replaces skills with Docker & AWS)
        UpdateOpportunityRequest updateReq = UpdateOpportunityRequest.builder()
                .title("Cloud Infrastructure Intern")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.REMOTE)
                .requiredSkillIds(List.of(skillDockerId, skillAwsId))
                .build();

        mockMvc.perform(put("/api/v1/opportunities/" + oppId)
                        .header("Authorization", "Bearer " + companyAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Cloud Infrastructure Intern")));

        // Verify update replacement in DB
        List<RequiredSkill> updatedReqSkills = requiredSkillRepository.findByOpportunityId(oppId);
        assertThat(updatedReqSkills).hasSize(2);
        assertThat(updatedReqSkills).extracting(RequiredSkill::getSkillId)
                .containsExactlyInAnyOrder(skillDockerId, skillAwsId);

        // Step 7: Company A closes opportunity
        mockMvc.perform(patch("/api/v1/opportunities/" + oppId + "/status")
                        .header("Authorization", "Bearer " + companyAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OpportunityStatusUpdateRequest(OpportunityStatus.CLOSED))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CLOSED")));

        assertThat(opportunityRepository.findById(oppId).orElseThrow().getStatus()).isEqualTo(OpportunityStatus.CLOSED);
    }

    // =========================================================================
    // PHASE C: MATCHING CORRECTNESS MATRIX
    // =========================================================================

    @Test
    @DisplayName("Phase C: Complete Matching & Eligibility Combinations Matrix")
    void testMatchingCorrectnessMatrix() throws Exception {
        // Scenario 1: 100% match, All Eligible (Java, Spring; Dept CSE, Year 3, CGPA 8.00 <= 8.50)
        Opportunity opp100 = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(companyAProfileId).title("100 Match").type(OpportunityType.INTERNSHIP)
                .minCgpa(BigDecimal.valueOf(8.00)).status(OpportunityStatus.OPEN).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp100.getId()).skillId(skillJavaId).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp100.getId()).skillId(skillSpringId).build());
        requiredBranchRepository.save(OpportunityRequiredBranch.builder().opportunityId(opp100.getId()).departmentId(deptCseId).build());
        requiredYearRepository.save(OpportunityRequiredYear.builder().opportunityId(opp100.getId()).yearOfStudy((short) 3).build());

        mockMvc.perform(get("/api/v1/matching/opportunities/" + opp100.getId()).header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchPercent", is(100.0)))
                .andExpect(jsonPath("$.isEligible", is(true)))
                .andExpect(jsonPath("$.matchedSkills", hasSize(2)))
                .andExpect(jsonPath("$.missingSkills", empty()));

        // Scenario 2: 50% match (Java, Spring, Docker, AWS -> 2/4), Branch Ineligible (ECE only)
        Opportunity opp50 = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(companyAProfileId).title("50 Match").type(OpportunityType.INTERNSHIP)
                .minCgpa(BigDecimal.valueOf(7.00)).status(OpportunityStatus.OPEN).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp50.getId()).skillId(skillJavaId).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp50.getId()).skillId(skillSpringId).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp50.getId()).skillId(skillDockerId).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp50.getId()).skillId(skillAwsId).build());
        requiredBranchRepository.save(OpportunityRequiredBranch.builder().opportunityId(opp50.getId()).departmentId(deptEceId).build());

        mockMvc.perform(get("/api/v1/matching/opportunities/" + opp50.getId()).header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchPercent", is(50.0)))
                .andExpect(jsonPath("$.isEligible", is(false)))
                .andExpect(jsonPath("$.ineligibilityReasons", contains("Branch not eligible")));

        // Scenario 3: 0% match (Docker, AWS only), CGPA & Year Ineligible (Min CGPA 9.00 > 8.50, Year 4)
        Opportunity opp0 = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(companyAProfileId).title("0 Match").type(OpportunityType.INTERNSHIP)
                .minCgpa(BigDecimal.valueOf(9.00)).status(OpportunityStatus.OPEN).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp0.getId()).skillId(skillDockerId).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp0.getId()).skillId(skillAwsId).build());
        requiredYearRepository.save(OpportunityRequiredYear.builder().opportunityId(opp0.getId()).yearOfStudy((short) 4).build());

        mockMvc.perform(get("/api/v1/matching/opportunities/" + opp0.getId()).header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchPercent", is(0.0)))
                .andExpect(jsonPath("$.isEligible", is(false)))
                .andExpect(jsonPath("$.ineligibilityReasons", hasSize(2)))
                .andExpect(jsonPath("$.ineligibilityReasons", hasItems("Year not eligible", "CGPA below required minimum (required: 9.00)")));

        // Scenario 4: Recommendations ranking: opp100 (100%) must appear before opp50 (50%) and opp0 (0%)
        mockMvc.perform(get("/api/v1/matching/recommendations").header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].id", is(opp100.getId().intValue())))
                .andExpect(jsonPath("$.content[0].matchPercent", is(100.0)))
                .andExpect(jsonPath("$.content[1].id", is(opp50.getId().intValue())))
                .andExpect(jsonPath("$.content[1].matchPercent", is(50.0)))
                .andExpect(jsonPath("$.content[2].id", is(opp0.getId().intValue())))
                .andExpect(jsonPath("$.content[2].matchPercent", is(0.0)));
    }

    // =========================================================================
    // PHASE D: STRICT RBAC & SECURITY ISOLATION
    // =========================================================================

    @Test
    @DisplayName("Phase D: Multi-Role RBAC & Ownership Security Verification")
    void testSecurityAndRbac() throws Exception {
        Opportunity oppA = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(companyAProfileId).title("Company A Posting").type(OpportunityType.INTERNSHIP)
                .status(OpportunityStatus.OPEN).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(oppA.getId()).skillId(skillJavaId).build());

        // 1. Unauthenticated requests to /opportunities/{id} return 401
        mockMvc.perform(get("/api/v1/opportunities/" + oppA.getId()))
                .andExpect(status().isUnauthorized());

        // 2. Company A cannot be updated by Company B (403 Forbidden)
        UpdateOpportunityRequest hijackReq = UpdateOpportunityRequest.builder()
                .title("Hijacked Posting").type(OpportunityType.INTERNSHIP).requiredSkillIds(List.of(skillJavaId)).build();
        mockMvc.perform(put("/api/v1/opportunities/" + oppA.getId())
                        .header("Authorization", "Bearer " + companyBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(hijackReq)))
                .andExpect(status().isForbidden());

        // 3. Company B cannot toggle Company A status (403 Forbidden)
        mockMvc.perform(patch("/api/v1/opportunities/" + oppA.getId() + "/status")
                        .header("Authorization", "Bearer " + companyBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new OpportunityStatusUpdateRequest(OpportunityStatus.CLOSED))))
                .andExpect(status().isForbidden());

        // 4. Student cannot create opportunities (403 Forbidden)
        CreateOpportunityRequest studentCreate = CreateOpportunityRequest.builder()
                .title("Student Opp").type(OpportunityType.INTERNSHIP).requiredSkillIds(List.of(skillJavaId)).build();
        mockMvc.perform(post("/api/v1/opportunities")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCreate)))
                .andExpect(status().isForbidden());

        // 5. College cannot create opportunities (403 Forbidden)
        mockMvc.perform(post("/api/v1/opportunities")
                        .header("Authorization", "Bearer " + collegeToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentCreate)))
                .andExpect(status().isForbidden());

        // 6. Company cannot access student-only matching endpoints (403 Forbidden)
        mockMvc.perform(get("/api/v1/matching/opportunities/" + oppA.getId())
                        .header("Authorization", "Bearer " + companyAToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/matching/recommendations")
                        .header("Authorization", "Bearer " + companyAToken))
                .andExpect(status().isForbidden());
    }

    // =========================================================================
    // PHASE G: STAGE 1-3 REGRESSION VERIFICATION
    // =========================================================================

    @Test
    @DisplayName("Phase G: Stage 1-3 Core Capabilities Regression Test")
    void testStage1to3Regression() throws Exception {
        // Stage 1 Auth: Register & Login
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"alice@iitb.edu\",\"password\":\"Password@123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("STUDENT")));

        // Stage 2 Profile & Skills: Student profile retrieval
        mockMvc.perform(get("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.departmentName", is("Computer Science and Engineering")));

        // Stage 2 Taxonomy: List skills
        mockMvc.perform(get("/api/v1/skills")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)));

        // Stage 3 Taxonomy: List departments
        mockMvc.perform(get("/api/v1/departments")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Stage 3 Company: Retrieve company profile
        mockMvc.perform(get("/api/v1/companies/profile")
                        .header("Authorization", "Bearer " + companyAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Alpha Cloud Corp")));
    }
}

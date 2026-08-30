package com.skillbridge.opportunity.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.college.entity.Department;
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
class OpportunityControllerIntegrationTest {

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
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String companyAToken;
    private String companyBToken;
    private String studentToken;
    private Long companyAProfileId;
    private Long companyBProfileId;
    private Long studentProfileId;
    private Long javaSkillId;
    private Long pythonSkillId;
    private Long cseDeptId;
    private Long opportunityId;

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
        skillRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Create taxonomy skills
        Skill java = skillRepository.save(Skill.builder().name("Java").category("Language").active(true).build());
        Skill python = skillRepository.save(Skill.builder().name("Python").category("Language").active(true).build());
        javaSkillId = java.getId();
        pythonSkillId = python.getId();

        // 2. Create taxonomy department
        Department cse = departmentRepository.save(Department.builder().name("Computer Science and Engineering").code("CSE").active(true).build());
        cseDeptId = cse.getId();

        // 3. Create Company A
        User userCompanyA = userRepository.save(User.builder()
                .email("hr@companya.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COMPANY)
                .active(true)
                .build());

        CompanyProfile profileA = companyProfileRepository.save(CompanyProfile.builder()
                .userId(userCompanyA.getId())
                .name("Company A Global")
                .industry("IT")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());
        companyAProfileId = profileA.getId();
        companyAToken = jwtService.generateToken(userCompanyA.getId(), userCompanyA.getEmail(), userCompanyA.getRole(), null, companyAProfileId, null);

        // 4. Create Company B
        User userCompanyB = userRepository.save(User.builder()
                .email("hr@companyb.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COMPANY)
                .active(true)
                .build());

        CompanyProfile profileB = companyProfileRepository.save(CompanyProfile.builder()
                .userId(userCompanyB.getId())
                .name("Company B Inc")
                .industry("Finance")
                .verificationStatus(VerificationStatus.PENDING)
                .build());
        companyBProfileId = profileB.getId();
        companyBToken = jwtService.generateToken(userCompanyB.getId(), userCompanyB.getEmail(), userCompanyB.getRole(), null, companyBProfileId, null);

        // 5. Create Student with Java skill
        User userStudent = userRepository.save(User.builder()
                .email("student@college.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());

        StudentProfile profileStudent = studentProfileRepository.save(StudentProfile.builder()
                .userId(userStudent.getId())
                .collegeId(1L)
                .firstName("Alice")
                .lastName("Smith")
                .departmentId(cseDeptId)
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.50))
                .build());
        studentProfileId = profileStudent.getId();
        studentToken = jwtService.generateToken(userStudent.getId(), userStudent.getEmail(), userStudent.getRole(), 1L, null, studentProfileId);

        studentSkillRepository.save(StudentSkill.builder().studentProfileId(studentProfileId).skillId(javaSkillId).build());

        // 6. Create existing opportunity owned by Company A
        Opportunity opp = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(companyAProfileId)
                .title("Full Stack Intern")
                .description("Build awesome apps")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.HYBRID)
                .minCgpa(BigDecimal.valueOf(7.00))
                .status(OpportunityStatus.OPEN)
                .build());
        opportunityId = opp.getId();

        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opportunityId).skillId(javaSkillId).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opportunityId).skillId(pythonSkillId).build());
        requiredBranchRepository.save(OpportunityRequiredBranch.builder().opportunityId(opportunityId).departmentId(cseDeptId).build());
        requiredYearRepository.save(OpportunityRequiredYear.builder().opportunityId(opportunityId).yearOfStudy((short) 3).build());
    }

    @Test
    @DisplayName("POST /api/v1/opportunities - COMPANY creates opportunity successfully")
    void createOpportunityAsCompany() throws Exception {
        CreateOpportunityRequest req = CreateOpportunityRequest.builder()
                .title("Software Engineer 2026")
                .description("Core product development")
                .type(OpportunityType.PLACEMENT)
                .location("Bangalore")
                .mode(OpportunityMode.ONSITE)
                .durationWeeks(24)
                .stipendAmount(BigDecimal.valueOf(50000.00))
                .minCgpa(BigDecimal.valueOf(8.00))
                .applicationDeadline(LocalDate.now().plusMonths(2))
                .requiredSkillIds(List.of(javaSkillId))
                .requiredDepartmentIds(List.of(cseDeptId))
                .requiredYearsOfStudy(List.of(4))
                .build();

        mockMvc.perform(post("/api/v1/opportunities")
                        .header("Authorization", "Bearer " + companyAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Software Engineer 2026")))
                .andExpect(jsonPath("$.type", is("PLACEMENT")))
                .andExpect(jsonPath("$.status", is("OPEN")))
                .andExpect(jsonPath("$.companyName", is("Company A Global")))
                .andExpect(jsonPath("$.requiredSkills", hasSize(1)))
                .andExpect(jsonPath("$.eligibleDepartments", hasSize(1)))
                .andExpect(jsonPath("$.eligibleYears", contains(4)));
    }

    @Test
    @DisplayName("POST /api/v1/opportunities - STUDENT is forbidden from creating opportunity (403)")
    void createOpportunityAsStudentForbidden() throws Exception {
        CreateOpportunityRequest req = CreateOpportunityRequest.builder()
                .title("Hacked Opportunity")
                .type(OpportunityType.INTERNSHIP)
                .requiredSkillIds(List.of(javaSkillId))
                .build();

        mockMvc.perform(post("/api/v1/opportunities")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /api/v1/opportunities/{id} - Owning company updates opportunity successfully")
    void updateOpportunityAsOwner() throws Exception {
        UpdateOpportunityRequest req = UpdateOpportunityRequest.builder()
                .title("Updated Full Stack Intern")
                .description("Updated description")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.REMOTE)
                .requiredSkillIds(List.of(javaSkillId))
                .build();

        mockMvc.perform(put("/api/v1/opportunities/" + opportunityId)
                        .header("Authorization", "Bearer " + companyAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Full Stack Intern")))
                .andExpect(jsonPath("$.mode", is("REMOTE")));
    }

    @Test
    @DisplayName("PUT /api/v1/opportunities/{id} - Other company is forbidden from updating opportunity (403)")
    void updateOpportunityAsOtherCompanyForbidden() throws Exception {
        UpdateOpportunityRequest req = UpdateOpportunityRequest.builder()
                .title("Hijacked Opportunity")
                .type(OpportunityType.INTERNSHIP)
                .requiredSkillIds(List.of(javaSkillId))
                .build();

        mockMvc.perform(put("/api/v1/opportunities/" + opportunityId)
                        .header("Authorization", "Bearer " + companyBToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /api/v1/opportunities/{id}/status - Toggle status to CLOSED")
    void updateOpportunityStatusSuccess() throws Exception {
        OpportunityStatusUpdateRequest req = new OpportunityStatusUpdateRequest(OpportunityStatus.CLOSED);

        mockMvc.perform(patch("/api/v1/opportunities/" + opportunityId + "/status")
                        .header("Authorization", "Bearer " + companyAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("CLOSED")));
    }

    @Test
    @DisplayName("GET /api/v1/opportunities/{id} - Student sees dynamic match breakdown")
    void getOpportunityByIdAsStudent() throws Exception {
        // Opportunity requires Java and Python. Student has Java (1 of 2 -> 50.0%)
        // Student is CSE, Year 3, CGPA 8.50 >= 7.00 -> Eligible = true
        mockMvc.perform(get("/api/v1/opportunities/" + opportunityId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(opportunityId.intValue())))
                .andExpect(jsonPath("$.matchBreakdown", notNullValue()))
                .andExpect(jsonPath("$.matchBreakdown.matchPercent", is(50.0)))
                .andExpect(jsonPath("$.matchBreakdown.isEligible", is(true)))
                .andExpect(jsonPath("$.matchBreakdown.matchedSkills", hasSize(1)))
                .andExpect(jsonPath("$.matchBreakdown.missingSkills", hasSize(1)));
    }

    @Test
    @DisplayName("GET /api/v1/opportunities - Browse opportunities as Student populates matchPercent and isEligible")
    void searchOpportunitiesAsStudent() throws Exception {
        mockMvc.perform(get("/api/v1/opportunities")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].matchPercent", is(50.0)))
                .andExpect(jsonPath("$.content[0].isEligible", is(true)));
    }

    @Test
    @DisplayName("GET /api/v1/opportunities/company/my - Company views its own postings")
    void getCompanyOpportunitiesSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/opportunities/company/my")
                        .header("Authorization", "Bearer " + companyAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].companyId", is(companyAProfileId.intValue())));
    }
}

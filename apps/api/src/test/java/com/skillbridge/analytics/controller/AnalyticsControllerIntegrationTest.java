package com.skillbridge.analytics.controller;

import com.skillbridge.application.entity.Application;
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
import com.skillbridge.internship.repository.CompanyFeedbackRepository;
import com.skillbridge.internship.repository.InternshipRecordRepository;
import com.skillbridge.opportunity.entity.Opportunity;
import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.opportunity.entity.RequiredSkill;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AnalyticsControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

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
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String collegeToken;
    private Long collegeId;
    private Long javaSkillId;
    private Long reactSkillId;

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

        // 1. Skills
        Skill java = skillRepository.save(Skill.builder().name("Java").category("Language").active(true).build());
        Skill react = skillRepository.save(Skill.builder().name("React").category("Frontend").active(true).build());
        javaSkillId = java.getId();
        reactSkillId = react.getId();

        // 2. Department
        Department cse = departmentRepository.save(Department.builder().name("Computer Science").code("CSE").active(true).build());

        // 3. College
        User collegeUser = userRepository.save(User.builder()
                .email("admin@univ.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COLLEGE)
                .active(true)
                .build());
        College college = collegeRepository.save(College.builder()
                .userId(collegeUser.getId())
                .name("State University")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());
        collegeId = college.getId();
        collegeToken = jwtService.generateToken(collegeUser.getId(), collegeUser.getEmail(), collegeUser.getRole(), collegeId, null, null);

        // 4. Student (has Java skill)
        User stuUser = userRepository.save(User.builder().email("stu@univ.edu").password(passwordEncoder.encode("Password@123")).role(Role.STUDENT).active(true).build());
        StudentProfile stu = studentProfileRepository.save(StudentProfile.builder()
                .userId(stuUser.getId())
                .collegeId(collegeId)
                .firstName("Alice")
                .lastName("Smith")
                .departmentId(cse.getId())
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.5))
                .build());
        studentSkillRepository.save(StudentSkill.builder().studentProfileId(stu.getId()).skillId(javaSkillId).build());

        // 5. Company & Opportunity (requires Java and React)
        User compUser = userRepository.save(User.builder().email("hr@corp.com").password(passwordEncoder.encode("Password@123")).role(Role.COMPANY).active(true).build());
        CompanyProfile comp = companyProfileRepository.save(CompanyProfile.builder().userId(compUser.getId()).name("Corp").verificationStatus(VerificationStatus.VERIFIED).build());

        Opportunity opp = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(comp.getId())
                .title("Full Stack Dev")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.REMOTE)
                .status(OpportunityStatus.OPEN)
                .build());

        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp.getId()).skillId(javaSkillId).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opp.getId()).skillId(reactSkillId).build());

        // 6. Application
        applicationRepository.save(Application.builder()
                .studentProfileId(stu.getId())
                .opportunityId(opp.getId())
                .status(ApplicationStatus.APPLIED)
                .matchPercentAtApply(BigDecimal.valueOf(50.0))
                .build());
    }

    @Test
    @DisplayName("GET /api/v1/analytics/skills/availability - College skill availability %")
    void getSkillAvailability() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/skills/availability")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].skillName", is("Java")))
                .andExpect(jsonPath("$[0].availabilityPercent", is(100.0)))
                .andExpect(jsonPath("$[1].skillName", is("React")))
                .andExpect(jsonPath("$[1].availabilityPercent", is(0.0)));
    }

    @Test
    @DisplayName("GET /api/v1/analytics/skills/demand - Platform-wide industry demand %")
    void getSkillDemand() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/skills/demand")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].demandPercent", is(100.0)))
                .andExpect(jsonPath("$[1].demandPercent", is(100.0)));
    }

    @Test
    @DisplayName("GET /api/v1/analytics/skills/gap - Skill gap dashboard with severity classification")
    void getSkillGapDashboard() throws Exception {
        // Java: demand 100%, availability 100% -> gap = 0% -> SURPLUS
        // React: demand 100%, availability 0% -> gap = 100% -> HIGH (>= 30)
        mockMvc.perform(get("/api/v1/analytics/skills/gap")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.collegeId", is(collegeId.intValue())))
                .andExpect(jsonPath("$.gaps", hasSize(2)))
                .andExpect(jsonPath("$.gaps[0].skillName", is("Java")))
                .andExpect(jsonPath("$.gaps[0].gapPercent", is(0.0)))
                .andExpect(jsonPath("$.gaps[0].severity", is("SURPLUS")))
                .andExpect(jsonPath("$.gaps[1].skillName", is("React")))
                .andExpect(jsonPath("$.gaps[1].gapPercent", is(100.0)))
                .andExpect(jsonPath("$.gaps[1].severity", is("HIGH")));
    }

    @Test
    @DisplayName("GET /api/v1/analytics/placement-funnel - Recruitment placement funnel")
    void getPlacementFunnel() throws Exception {
        mockMvc.perform(get("/api/v1/analytics/placement-funnel")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.applied", is(1)))
                .andExpect(jsonPath("$.underReview", is(0)))
                .andExpect(jsonPath("$.totalApplications", is(1)));
    }
}

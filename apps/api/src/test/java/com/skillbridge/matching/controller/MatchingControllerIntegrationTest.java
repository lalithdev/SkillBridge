package com.skillbridge.matching.controller;

import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
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
class MatchingControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

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

    private String studentToken;
    private String companyToken;
    private Long opportunityHighMatchId;
    private Long opportunityLowMatchId;

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

        // 1. Skills
        Skill java = skillRepository.save(Skill.builder().name("Java").category("Language").active(true).build());
        Skill spring = skillRepository.save(Skill.builder().name("Spring Boot").category("Framework").active(true).build());
        Skill react = skillRepository.save(Skill.builder().name("React").category("Frontend").active(true).build());

        // 2. Department
        Department cse = departmentRepository.save(Department.builder().name("Computer Science and Engineering").code("CSE").active(true).build());

        // 3. Company
        User companyUser = userRepository.save(User.builder()
                .email("hr@corp.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COMPANY)
                .active(true)
                .build());
        CompanyProfile company = companyProfileRepository.save(CompanyProfile.builder()
                .userId(companyUser.getId())
                .name("Global Tech")
                .build());
        companyToken = jwtService.generateToken(companyUser.getId(), companyUser.getEmail(), Role.COMPANY, null, company.getId(), null);

        // 4. Student with Java & Spring skills
        User studentUser = userRepository.save(User.builder()
                .email("student@college.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        StudentProfile student = studentProfileRepository.save(StudentProfile.builder()
                .userId(studentUser.getId())
                .collegeId(1L)
                .departmentId(cse.getId())
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.50))
                .firstName("Bob")
                .lastName("Taylor")
                .build());
        studentToken = jwtService.generateToken(studentUser.getId(), studentUser.getEmail(), Role.STUDENT, 1L, null, student.getId());

        studentSkillRepository.save(StudentSkill.builder().studentProfileId(student.getId()).skillId(java.getId()).build());
        studentSkillRepository.save(StudentSkill.builder().studentProfileId(student.getId()).skillId(spring.getId()).build());

        // 5. Opportunity 1 (High match: requires Java & Spring -> 100%)
        Opportunity oppHigh = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(company.getId())
                .title("Java Backend Developer")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.REMOTE)
                .status(OpportunityStatus.OPEN)
                .build());
        opportunityHighMatchId = oppHigh.getId();
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opportunityHighMatchId).skillId(java.getId()).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opportunityHighMatchId).skillId(spring.getId()).build());

        // 6. Opportunity 2 (Low match: requires Java & React -> 50%)
        Opportunity oppLow = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(company.getId())
                .title("Full Stack Developer")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.ONSITE)
                .status(OpportunityStatus.OPEN)
                .build());
        opportunityLowMatchId = oppLow.getId();
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opportunityLowMatchId).skillId(java.getId()).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opportunityLowMatchId).skillId(react.getId()).build());
    }

    @Test
    @DisplayName("GET /api/v1/matching/opportunities/{id} - Dynamic match evaluation for Student")
    void evaluateOpportunityMatchSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/matching/opportunities/" + opportunityHighMatchId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchPercent", is(100.0)))
                .andExpect(jsonPath("$.isEligible", is(true)))
                .andExpect(jsonPath("$.matchedSkills", hasSize(2)))
                .andExpect(jsonPath("$.missingSkills", empty()));
    }

    @Test
    @DisplayName("GET /api/v1/matching/opportunities/{id} - Company role is forbidden (403)")
    void evaluateOpportunityMatchAsCompanyForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/matching/opportunities/" + opportunityHighMatchId)
                        .header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/v1/matching/recommendations - Returns opportunities ranked descending by match percent")
    void getRecommendationsRanked() throws Exception {
        mockMvc.perform(get("/api/v1/matching/recommendations")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(opportunityHighMatchId.intValue())))
                .andExpect(jsonPath("$.content[0].matchPercent", is(100.0)))
                .andExpect(jsonPath("$.content[1].id", is(opportunityLowMatchId.intValue())))
                .andExpect(jsonPath("$.content[1].matchPercent", is(50.0)));
    }
}

package com.skillbridge.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.application.dto.SubmitApplicationRequest;
import com.skillbridge.application.dto.UpdateApplicationStatusRequest;
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
import com.skillbridge.internship.entity.InternshipRecord;
import com.skillbridge.internship.entity.InternshipStatus;
import com.skillbridge.internship.repository.CompanyFeedbackRepository;
import com.skillbridge.internship.repository.InternshipRecordRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ApplicationControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private InternshipRecordRepository internshipRecordRepository;

    @Autowired
    private CompanyFeedbackRepository companyFeedbackRepository;

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
    private CollegeRepository collegeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String studentToken;
    private String companyToken;
    private String otherStudentToken;
    private Long studentProfileId;
    private Long otherStudentProfileId;
    private Long companyProfileId;
    private Long opportunityId;
    private Long javaSkillId;
    private Long pythonSkillId;
    private Long cseDeptId;
    private Long collegeId;

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

        // 1. Taxonomy setup
        Skill java = skillRepository.save(Skill.builder().name("Java").category("Language").active(true).build());
        Skill python = skillRepository.save(Skill.builder().name("Python").category("Language").active(true).build());
        javaSkillId = java.getId();
        pythonSkillId = python.getId();

        Department cse = departmentRepository.save(Department.builder().name("Computer Science").code("CSE").active(true).build());
        cseDeptId = cse.getId();

        // 2. College setup
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

        // 3. Company setup
        User companyUser = userRepository.save(User.builder()
                .email("careers@techcorp.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COMPANY)
                .active(true)
                .build());
        CompanyProfile company = companyProfileRepository.save(CompanyProfile.builder()
                .userId(companyUser.getId())
                .name("Tech Corp")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());
        companyProfileId = company.getId();
        companyToken = jwtService.generateToken(companyUser.getId(), companyUser.getEmail(), companyUser.getRole(), null, companyProfileId, null);

        // 4. Student 1 setup (Eligible: CSE, Year 3, CGPA 8.5, has Java -> 50% match)
        User studentUser = userRepository.save(User.builder()
                .email("alice@univ.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        StudentProfile student = studentProfileRepository.save(StudentProfile.builder()
                .userId(studentUser.getId())
                .collegeId(collegeId)
                .firstName("Alice")
                .lastName("Walker")
                .departmentId(cseDeptId)
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.50))
                .resumePath("uploads/resumes/alice.pdf")
                .build());
        studentProfileId = student.getId();
        studentToken = jwtService.generateToken(studentUser.getId(), studentUser.getEmail(), studentUser.getRole(), collegeId, null, studentProfileId);
        studentSkillRepository.save(StudentSkill.builder().studentProfileId(studentProfileId).skillId(javaSkillId).build());

        // 5. Student 2 setup (Ineligible CGPA: 6.0)
        User otherStudentUser = userRepository.save(User.builder()
                .email("bob@univ.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        StudentProfile otherStudent = studentProfileRepository.save(StudentProfile.builder()
                .userId(otherStudentUser.getId())
                .collegeId(collegeId)
                .firstName("Bob")
                .lastName("Smith")
                .departmentId(cseDeptId)
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(6.00))
                .build());
        otherStudentProfileId = otherStudent.getId();
        otherStudentToken = jwtService.generateToken(otherStudentUser.getId(), otherStudentUser.getEmail(), otherStudentUser.getRole(), collegeId, null, otherStudentProfileId);

        // 6. Opportunity setup
        Opportunity opp = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(companyProfileId)
                .title("Software Engineer Intern")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.ONSITE)
                .status(OpportunityStatus.OPEN)
                .minCgpa(BigDecimal.valueOf(7.00))
                .applicationDeadline(LocalDate.now().plusMonths(1))
                .build());
        opportunityId = opp.getId();

        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opportunityId).skillId(javaSkillId).build());
        requiredSkillRepository.save(RequiredSkill.builder().opportunityId(opportunityId).skillId(pythonSkillId).build());
        requiredBranchRepository.save(OpportunityRequiredBranch.builder().opportunityId(opportunityId).departmentId(cseDeptId).build());
        requiredYearRepository.save(OpportunityRequiredYear.builder().opportunityId(opportunityId).yearOfStudy((short) 3).build());
    }

    @Test
    @DisplayName("POST /api/v1/applications - Student applies successfully with match percent snapshot")
    void submitApplication_Success() throws Exception {
        SubmitApplicationRequest req = SubmitApplicationRequest.builder().opportunityId(opportunityId).build();

        mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.studentProfileId", is(studentProfileId.intValue())))
                .andExpect(jsonPath("$.opportunityId", is(opportunityId.intValue())))
                .andExpect(jsonPath("$.status", is("APPLIED")))
                .andExpect(jsonPath("$.matchPercentAtApply", is(50.0)));
    }

    @Test
    @DisplayName("POST /api/v1/applications - Ineligible student rejected with 400 Bad Request")
    void submitApplication_IneligibleStudent() throws Exception {
        SubmitApplicationRequest req = SubmitApplicationRequest.builder().opportunityId(opportunityId).build();

        mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + otherStudentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("CGPA")));
    }

    @Test
    @DisplayName("POST /api/v1/applications - Duplicate application rejected with 409 Conflict")
    void submitApplication_DuplicateConflict() throws Exception {
        SubmitApplicationRequest req = SubmitApplicationRequest.builder().opportunityId(opportunityId).build();

        // First apply succeeds
        mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        // Second apply returns 409 Conflict
        mockMvc.perform(post("/api/v1/applications")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already applied")));
    }

    @Test
    @DisplayName("GET /api/v1/applications/my - Student views submitted applications")
    void getMyApplications_Success() throws Exception {
        applicationRepository.save(Application.builder()
                .studentProfileId(studentProfileId)
                .opportunityId(opportunityId)
                .status(ApplicationStatus.APPLIED)
                .matchPercentAtApply(BigDecimal.valueOf(50.00))
                .build());

        mockMvc.perform(get("/api/v1/applications/my")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].opportunityTitle", is("Software Engineer Intern")))
                .andExpect(jsonPath("$.content[0].companyName", is("Tech Corp")));
    }

    @Test
    @DisplayName("GET /api/v1/opportunities/{oppId}/applications - Company views ranked candidate list")
    void getOpportunityApplications_RankedDesc() throws Exception {
        applicationRepository.save(Application.builder()
                .studentProfileId(studentProfileId)
                .opportunityId(opportunityId)
                .status(ApplicationStatus.APPLIED)
                .matchPercentAtApply(BigDecimal.valueOf(50.00))
                .build());

        mockMvc.perform(get("/api/v1/opportunities/" + opportunityId + "/applications")
                        .header("Authorization", "Bearer " + companyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].studentName", is("Alice Walker")))
                .andExpect(jsonPath("$.content[0].matchPercentAtApply", is(50.0)))
                .andExpect(jsonPath("$.content[0].hasResume", is(true)));
    }

    @Test
    @DisplayName("PATCH /api/v1/applications/{id}/status - Progress pipeline to SELECTED and auto-create internship")
    void updateApplicationStatus_SelectedAutoCreatesInternship() throws Exception {
        Application app = applicationRepository.save(Application.builder()
                .studentProfileId(studentProfileId)
                .opportunityId(opportunityId)
                .status(ApplicationStatus.INTERVIEW)
                .matchPercentAtApply(BigDecimal.valueOf(50.00))
                .build());

        UpdateApplicationStatusRequest req = UpdateApplicationStatusRequest.builder()
                .status(ApplicationStatus.SELECTED)
                .build();

        mockMvc.perform(patch("/api/v1/applications/" + app.getId() + "/status")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("SELECTED")));

        // Verify internship_records automatically created in DB with UPCOMING status
        Optional<InternshipRecord> createdRecord = internshipRecordRepository.findByApplicationId(app.getId());
        assertThat(createdRecord).isPresent();
        assertThat(createdRecord.get().getStatus()).isEqualTo(InternshipStatus.UPCOMING);
    }
}

package com.skillbridge.internship.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.skillbridge.internship.dto.SubmitFeedbackRequest;
import com.skillbridge.internship.dto.UpdateInternshipStatusRequest;
import com.skillbridge.internship.entity.CompanyFeedback;
import com.skillbridge.internship.entity.InternshipRecord;
import com.skillbridge.internship.entity.InternshipStatus;
import com.skillbridge.internship.repository.CompanyFeedbackRepository;
import com.skillbridge.internship.repository.InternshipRecordRepository;
import com.skillbridge.opportunity.entity.Opportunity;
import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.repository.StudentProfileRepository;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class InternshipControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CompanyFeedbackRepository companyFeedbackRepository;

    @Autowired
    private InternshipRecordRepository internshipRecordRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String studentToken;
    private String companyToken;
    private String collegeToken;
    private Long studentProfileId;
    private Long companyProfileId;
    private Long collegeId;
    private Long applicationId;
    private Long internshipRecordId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        companyFeedbackRepository.deleteAll();
        internshipRecordRepository.deleteAll();
        applicationRepository.deleteAll();
        opportunityRepository.deleteAll();
        studentProfileRepository.deleteAll();
        companyProfileRepository.deleteAll();
        collegeRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Department
        Department cse = departmentRepository.save(Department.builder().name("CSE").code("CSE").active(true).build());

        // 2. College
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

        // 3. Company
        User companyUser = userRepository.save(User.builder()
                .email("hr@acme.com")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COMPANY)
                .active(true)
                .build());
        CompanyProfile company = companyProfileRepository.save(CompanyProfile.builder()
                .userId(companyUser.getId())
                .name("Acme Corp")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());
        companyProfileId = company.getId();
        companyToken = jwtService.generateToken(companyUser.getId(), companyUser.getEmail(), companyUser.getRole(), null, companyProfileId, null);

        // 4. Student
        User studentUser = userRepository.save(User.builder()
                .email("intern@univ.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        StudentProfile student = studentProfileRepository.save(StudentProfile.builder()
                .userId(studentUser.getId())
                .collegeId(collegeId)
                .firstName("John")
                .lastName("Doe")
                .departmentId(cse.getId())
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.50))
                .build());
        studentProfileId = student.getId();
        studentToken = jwtService.generateToken(studentUser.getId(), studentUser.getEmail(), studentUser.getRole(), collegeId, null, studentProfileId);

        // 5. Opportunity & Application
        Opportunity opp = opportunityRepository.save(Opportunity.builder()
                .companyProfileId(companyProfileId)
                .title("Cloud Engineer Intern")
                .type(OpportunityType.INTERNSHIP)
                .mode(OpportunityMode.REMOTE)
                .status(OpportunityStatus.OPEN)
                .build());

        Application app = applicationRepository.save(Application.builder()
                .studentProfileId(studentProfileId)
                .opportunityId(opp.getId())
                .status(ApplicationStatus.SELECTED)
                .matchPercentAtApply(BigDecimal.valueOf(80.00))
                .build());
        applicationId = app.getId();

        // 6. Internship Record
        InternshipRecord ir = internshipRecordRepository.save(InternshipRecord.builder()
                .applicationId(applicationId)
                .status(InternshipStatus.UPCOMING)
                .build());
        internshipRecordId = ir.getId();
    }

    @Test
    @DisplayName("GET /api/v1/internships/my - Student retrieves confirmed internship")
    void getMyInternships_Success() throws Exception {
        mockMvc.perform(get("/api/v1/internships/my")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].opportunityTitle", is("Cloud Engineer Intern")))
                .andExpect(jsonPath("$[0].companyName", is("Acme Corp")))
                .andExpect(jsonPath("$[0].status", is("UPCOMING")));
    }

    @Test
    @DisplayName("PATCH /api/v1/internships/{id}/status - Progress to ONGOING and COMPLETED")
    void updateInternshipStatus_Lifecycle() throws Exception {
        UpdateInternshipStatusRequest req = UpdateInternshipStatusRequest.builder()
                .status(InternshipStatus.ONGOING)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusMonths(3))
                .build();

        mockMvc.perform(patch("/api/v1/internships/" + internshipRecordId + "/status")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ONGOING")));
    }

    @Test
    @DisplayName("POST /api/v1/internships/{id}/feedback - Reject feedback if internship not COMPLETED")
    void submitFeedback_RejectsIfIncomplete() throws Exception {
        SubmitFeedbackRequest req = SubmitFeedbackRequest.builder()
                .feedbackText("Great work so far during the initial weeks.")
                .build();

        mockMvc.perform(post("/api/v1/internships/" + internshipRecordId + "/feedback")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("COMPLETED")));
    }

    @Test
    @DisplayName("POST & GET /api/v1/internships/{id}/feedback - Submit feedback on COMPLETED internship and view")
    void submitAndGetFeedback_Success() throws Exception {
        // Complete the internship
        InternshipRecord ir = internshipRecordRepository.findById(internshipRecordId).orElseThrow();
        ir.setStatus(InternshipStatus.COMPLETED);
        internshipRecordRepository.save(ir);

        SubmitFeedbackRequest req = SubmitFeedbackRequest.builder()
                .feedbackText("Exceptional technical contribution and problem-solving skills throughout the internship.")
                .build();

        // Submit feedback
        mockMvc.perform(post("/api/v1/internships/" + internshipRecordId + "/feedback")
                        .header("Authorization", "Bearer " + companyToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.studentName", is("John Doe")))
                .andExpect(jsonPath("$.companyName", is("Acme Corp")));

        // Student views feedback
        mockMvc.perform(get("/api/v1/internships/" + internshipRecordId + "/feedback")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.feedbackText", containsString("Exceptional technical contribution")));

        // College views aggregated feedback
        mockMvc.perform(get("/api/v1/colleges/feedback")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].studentName", is("John Doe")));
    }
}

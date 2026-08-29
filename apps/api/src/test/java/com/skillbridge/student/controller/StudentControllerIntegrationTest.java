package com.skillbridge.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.dto.*;
import com.skillbridge.student.entity.Certification;
import com.skillbridge.student.entity.Project;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.entity.StudentSkill;
import com.skillbridge.student.repository.CertificationRepository;
import com.skillbridge.student.repository.ProjectRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class StudentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CertificationRepository certificationRepository;

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

    private String studentToken;
    private String collegeToken;
    private String otherCollegeToken;
    private Long studentProfileId;
    private Long collegeId;
    private Long otherCollegeId;
    private Long departmentId;
    private Long skillId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        studentSkillRepository.deleteAll();
        projectRepository.deleteAll();
        certificationRepository.deleteAll();
        studentProfileRepository.deleteAll();
        skillRepository.deleteAll();
        departmentRepository.deleteAll();
        collegeRepository.deleteAll();
        userRepository.deleteAll();

        // 1. College 1
        User collegeUser = userRepository.save(User.builder()
                .email("college@nit.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COLLEGE)
                .active(true)
                .build());
        College college = collegeRepository.save(College.builder()
                .userId(collegeUser.getId())
                .name("NIT Trichy")
                .build());
        collegeId = college.getId();
        collegeToken = jwtService.generateToken(collegeUser.getId(), collegeUser.getEmail(), collegeUser.getRole(), collegeId, null, null);

        // 2. College 2 (Other College)
        User otherCollegeUser = userRepository.save(User.builder()
                .email("tpo@other.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COLLEGE)
                .active(true)
                .build());
        College otherCollege = collegeRepository.save(College.builder()
                .userId(otherCollegeUser.getId())
                .name("Other Institute")
                .build());
        otherCollegeId = otherCollege.getId();
        otherCollegeToken = jwtService.generateToken(otherCollegeUser.getId(), otherCollegeUser.getEmail(), otherCollegeUser.getRole(), otherCollegeId, null, null);

        // 3. Department
        Department dept = departmentRepository.save(Department.builder()
                .name("Computer Science and Engineering")
                .code("CSE")
                .active(true)
                .build());
        departmentId = dept.getId();

        // 4. Skill
        Skill skill = skillRepository.save(Skill.builder()
                .name("Java")
                .category("Language")
                .active(true)
                .build());
        skillId = skill.getId();

        // 5. Student
        User studentUser = userRepository.save(User.builder()
                .email("alice@student.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());

        StudentProfile profile = studentProfileRepository.save(StudentProfile.builder()
                .userId(studentUser.getId())
                .collegeId(collegeId)
                .firstName("Alice")
                .lastName("Smith")
                .departmentId(departmentId)
                .yearOfStudy((short) 3)
                .cgpa(new BigDecimal("8.50"))
                .build());
        studentProfileId = profile.getId();

        studentToken = jwtService.generateToken(studentUser.getId(), studentUser.getEmail(), studentUser.getRole(), collegeId, null, studentProfileId);
    }

    @Test
    @DisplayName("GET /api/v1/students/profile - Should return own profile for authenticated STUDENT")
    void getStudentProfileSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(studentProfileId.intValue())))
                .andExpect(jsonPath("$.firstName", is("Alice")))
                .andExpect(jsonPath("$.lastName", is("Smith")))
                .andExpect(jsonPath("$.collegeName", is("NIT Trichy")))
                .andExpect(jsonPath("$.departmentCode", is("CSE")));
    }

    @Test
    @DisplayName("PUT /api/v1/students/profile - Should update student profile details")
    void updateStudentProfileSuccess() throws Exception {
        UpdateStudentProfileRequest request = UpdateStudentProfileRequest.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .departmentId(departmentId)
                .yearOfStudy(4)
                .cgpa(new BigDecimal("9.25"))
                .careerInterests("Full Stack Development")
                .portfolioUrl("https://alice.dev")
                .githubUrl("https://github.com/alice")
                .build();

        mockMvc.perform(put("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Johnson")))
                .andExpect(jsonPath("$.cgpa", is(9.25)))
                .andExpect(jsonPath("$.careerInterests", is("Full Stack Development")));
    }

    @Test
    @DisplayName("PUT /api/v1/students/profile - Should reject invalid CGPA > 10.0 with 400 Bad Request")
    void updateStudentProfileInvalidCgpa() throws Exception {
        UpdateStudentProfileRequest request = UpdateStudentProfileRequest.builder()
                .firstName("Alice")
                .lastName("Smith")
                .cgpa(new BigDecimal("11.50"))
                .build();

        mockMvc.perform(put("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/profile - Should allow affiliated COLLEGE to view student profile")
    void getStudentProfileByIdAffiliatedCollegeSuccess() throws Exception {
        mockMvc.perform(get("/api/v1/students/" + studentProfileId + "/profile")
                        .header("Authorization", "Bearer " + collegeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(studentProfileId.intValue())))
                .andExpect(jsonPath("$.firstName", is("Alice")));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id}/profile - Should reject non-affiliated COLLEGE with 403 Forbidden")
    void getStudentProfileByIdNonAffiliatedCollegeForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/students/" + studentProfileId + "/profile")
                        .header("Authorization", "Bearer " + otherCollegeToken))
                .andExpect(status().isForbidden());
    }

    // --- Skills Management ---

    @Test
    @DisplayName("POST & GET & DELETE /api/v1/students/profile/skills - Skill CRUD operations")
    void studentSkillsFlow() throws Exception {
        // 1. Add skill
        mockMvc.perform(post("/api/v1/students/profile/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddStudentSkillRequest(skillId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(skillId.intValue())))
                .andExpect(jsonPath("$.name", is("Java")));

        // 2. Duplicate skill throws 409
        mockMvc.perform(post("/api/v1/students/profile/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddStudentSkillRequest(skillId))))
                .andExpect(status().isConflict());

        // 3. List skills
        mockMvc.perform(get("/api/v1/students/profile/skills")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Java")));

        // 4. Delete skill
        mockMvc.perform(delete("/api/v1/students/profile/skills/" + skillId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNoContent());

        // 5. List skills is empty
        mockMvc.perform(get("/api/v1/students/profile/skills")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // --- Projects CRUD ---

    @Test
    @DisplayName("Projects CRUD flow")
    void studentProjectsFlow() throws Exception {
        ProjectRequest createReq = ProjectRequest.builder()
                .title("Smart Campus App")
                .description("Campus portal for students")
                .projectUrl("https://github.com/alice/campus")
                .build();

        // Create project
        String createRes = mockMvc.perform(post("/api/v1/students/profile/projects")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Smart Campus App")))
                .andReturn().getResponse().getContentAsString();

        ProjectDto created = objectMapper.readValue(createRes, ProjectDto.class);

        // Update project
        ProjectRequest updateReq = ProjectRequest.builder()
                .title("Smart Campus App v2")
                .description("Updated description")
                .projectUrl("https://github.com/alice/campus-v2")
                .build();

        mockMvc.perform(put("/api/v1/students/profile/projects/" + created.getId())
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Smart Campus App v2")));

        // Delete project
        mockMvc.perform(delete("/api/v1/students/profile/projects/" + created.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNoContent());
    }

    // --- Certifications CRUD ---

    @Test
    @DisplayName("Certifications CRUD flow")
    void studentCertificationsFlow() throws Exception {
        CertificationRequest createReq = CertificationRequest.builder()
                .title("Oracle Certified Professional: Java SE 17")
                .issuer("Oracle")
                .issuedDate(LocalDate.of(2025, 3, 15))
                .certificateUrl("https://oracle.com/cert/12345")
                .build();

        // Create certification
        String createRes = mockMvc.perform(post("/api/v1/students/profile/certifications")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Oracle Certified Professional: Java SE 17")))
                .andReturn().getResponse().getContentAsString();

        CertificationDto created = objectMapper.readValue(createRes, CertificationDto.class);

        // Delete certification
        mockMvc.perform(delete("/api/v1/students/profile/certifications/" + created.getId())
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNoContent());
    }

    // --- Resume Upload, Download, Delete ---

    @Test
    @DisplayName("Resume upload, download, and delete flow")
    void studentResumeFlow() throws Exception {
        MockMultipartFile resumeFile = new MockMultipartFile(
                "file",
                "resume.pdf",
                "application/pdf",
                "%PDF-1.4 sample resume content".getBytes()
        );

        // Upload resume
        mockMvc.perform(multipart("/api/v1/students/profile/resume")
                        .file(resumeFile)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resumePath", notNullValue()))
                .andExpect(jsonPath("$.fileName", is("resume.pdf")));

        // Download resume
        mockMvc.perform(get("/api/v1/students/" + studentProfileId + "/resume")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));

        // Delete resume
        mockMvc.perform(delete("/api/v1/students/profile/resume")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNoContent());

        // Download after delete gives 404
        mockMvc.perform(get("/api/v1/students/" + studentProfileId + "/resume")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isNotFound());
    }
}

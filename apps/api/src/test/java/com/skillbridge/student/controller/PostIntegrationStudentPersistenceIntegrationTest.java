package com.skillbridge.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.auth.dto.AuthResponse;
import com.skillbridge.auth.dto.RegisterRequest;
import com.skillbridge.auth.dto.RegisterRole;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.dto.AddStudentSkillRequest;
import com.skillbridge.student.dto.UpdateStudentProfileRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class PostIntegrationStudentPersistenceIntegrationTest {

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
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        studentSkillRepository.deleteAll();
        projectRepository.deleteAll();
        certificationRepository.deleteAll();
        studentProfileRepository.deleteAll();
        skillRepository.deleteAll();
        collegeRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("End-to-End: Public colleges, Student registration, Phone/Graduation/College persistence, Profile update, and Skills lifecycle")
    void testFullStudentPersistenceLifecycle() throws Exception {
        // 1. Create 2 Colleges in DB
        User collegeUser1 = userRepository.save(User.builder()
                .email("nit@test.edu")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.COLLEGE)
                .active(true)
                .build());

        College college1 = collegeRepository.save(College.builder()
                .userId(collegeUser1.getId())
                .name("National Institute of Technology")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());

        User collegeUser2 = userRepository.save(User.builder()
                .email("dtu@test.edu")
                .password(passwordEncoder.encode("Password123!"))
                .role(Role.COLLEGE)
                .active(true)
                .build());

        College college2 = collegeRepository.save(College.builder()
                .userId(collegeUser2.getId())
                .name("Delhi Technological University")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());

        // Create Department and Master Skills
        Department department = departmentRepository.save(Department.builder()
                .name("Computer Science and Engineering")
                .code("CSE")
                .active(true)
                .build());

        Skill skillJava = skillRepository.save(Skill.builder()
                .name("Java")
                .category("Backend")
                .active(true)
                .build());

        Skill skillReact = skillRepository.save(Skill.builder()
                .name("React")
                .category("Frontend")
                .active(true)
                .build());

        // 2. Verify GET /api/v1/colleges/public returns both registered colleges publicly
        mockMvc.perform(get("/api/v1/colleges/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Delhi Technological University")))
                .andExpect(jsonPath("$[1].name", is("National Institute of Technology")));

        // 3. Register Student 1 with College 1 (NIT), phone, graduationYear
        RegisterRequest student1Req = RegisterRequest.builder()
                .name("Aarav Sharma")
                .email("aarav@nit.test.edu")
                .password("Password123!")
                .role(RegisterRole.STUDENT)
                .collegeId(college1.getId())
                .departmentId(department.getId())
                .phone("+91 9876543210")
                .graduationYear(2026)
                .yearOfStudy(4)
                .build();

        String res1 = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student1Req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role", is("STUDENT")))
                .andExpect(jsonPath("$.collegeId", is(college1.getId().intValue())))
                .andReturn().getResponse().getContentAsString();

        AuthResponse auth1 = objectMapper.readValue(res1, AuthResponse.class);

        // 4. Register Student 2 with College 2 (DTU), phone, graduationYear
        RegisterRequest student2Req = RegisterRequest.builder()
                .name("Neha Gupta")
                .email("neha@dtu.test.edu")
                .password("Password123!")
                .role(RegisterRole.STUDENT)
                .collegeId(college2.getId())
                .departmentId(department.getId())
                .phone("+91 9123456780")
                .graduationYear(2027)
                .yearOfStudy(3)
                .build();

        String res2 = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student2Req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role", is("STUDENT")))
                .andExpect(jsonPath("$.collegeId", is(college2.getId().intValue())))
                .andReturn().getResponse().getContentAsString();

        AuthResponse auth2 = objectMapper.readValue(res2, AuthResponse.class);

        // 5. Call GET /api/v1/students/profile for Student 1
        mockMvc.perform(get("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + auth1.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Aarav")))
                .andExpect(jsonPath("$.lastName", is("Sharma")))
                .andExpect(jsonPath("$.email", is("aarav@nit.test.edu")))
                .andExpect(jsonPath("$.phone", is("+91 9876543210")))
                .andExpect(jsonPath("$.collegeName", is("National Institute of Technology")))
                .andExpect(jsonPath("$.collegeId", is(college1.getId().intValue())))
                .andExpect(jsonPath("$.departmentName", is("Computer Science and Engineering")))
                .andExpect(jsonPath("$.yearOfStudy", is(4)));

        // 6. Call GET /api/v1/students/profile for Student 2 -> Verify college is DTU (NEVER hardcoded MIT)
        mockMvc.perform(get("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + auth2.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Neha")))
                .andExpect(jsonPath("$.lastName", is("Gupta")))
                .andExpect(jsonPath("$.email", is("neha@dtu.test.edu")))
                .andExpect(jsonPath("$.phone", is("+91 9123456780")))
                .andExpect(jsonPath("$.collegeName", is("Delhi Technological University")))
                .andExpect(jsonPath("$.collegeId", is(college2.getId().intValue())))
                .andExpect(jsonPath("$.yearOfStudy", is(3)));

        // 7. Student 1 updates profile via PUT /api/v1/students/profile
        UpdateStudentProfileRequest updateReq = UpdateStudentProfileRequest.builder()
                .firstName("Aarav")
                .lastName("Sharma Updated")
                .phone("+91 9998887776")
                .yearOfStudy(3)
                .cgpa(new BigDecimal("9.25"))
                .careerInterests("AI and Distributed Systems")
                .portfolioUrl("https://aarav.dev")
                .githubUrl("https://github.com/aarav")
                .build();

        mockMvc.perform(put("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + auth1.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Aarav")))
                .andExpect(jsonPath("$.lastName", is("Sharma Updated")))
                .andExpect(jsonPath("$.phone", is("+91 9998887776")))
                .andExpect(jsonPath("$.cgpa", is(9.25)))
                .andExpect(jsonPath("$.careerInterests", is("AI and Distributed Systems")))
                .andExpect(jsonPath("$.portfolioUrl", is("https://aarav.dev")))
                .andExpect(jsonPath("$.githubUrl", is("https://github.com/aarav")))
                .andExpect(jsonPath("$.yearOfStudy", is(3)));

        // 8. Reload GET /api/v1/students/profile to verify persistence
        mockMvc.perform(get("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + auth1.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Sharma Updated")))
                .andExpect(jsonPath("$.phone", is("+91 9998887776")))
                .andExpect(jsonPath("$.cgpa", is(9.25)))
                .andExpect(jsonPath("$.careerInterests", is("AI and Distributed Systems")))
                .andExpect(jsonPath("$.portfolioUrl", is("https://aarav.dev")))
                .andExpect(jsonPath("$.githubUrl", is("https://github.com/aarav")));

        // 9. Add Skill 1 (Java)
        mockMvc.perform(post("/api/v1/students/profile/skills")
                        .header("Authorization", "Bearer " + auth1.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddStudentSkillRequest(skillJava.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Java")));

        // Add Skill 2 (React)
        mockMvc.perform(post("/api/v1/students/profile/skills")
                        .header("Authorization", "Bearer " + auth1.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AddStudentSkillRequest(skillReact.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("React")));

        // 10. Reload skills list GET /api/v1/students/profile/skills
        mockMvc.perform(get("/api/v1/students/profile/skills")
                        .header("Authorization", "Bearer " + auth1.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Java", "React")));

        // Also check skills array inside GET /api/v1/students/profile
        mockMvc.perform(get("/api/v1/students/profile")
                        .header("Authorization", "Bearer " + auth1.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills", hasSize(2)))
                .andExpect(jsonPath("$.skills[*].name", containsInAnyOrder("Java", "React")));

        // 11. Remove Skill 1 (Java) via DELETE /api/v1/students/profile/skills/{skillId}
        mockMvc.perform(delete("/api/v1/students/profile/skills/" + skillJava.getId())
                        .header("Authorization", "Bearer " + auth1.getToken()))
                .andExpect(status().isNoContent());

        // 12. Reload skills list: Java must be gone, React must still be present
        mockMvc.perform(get("/api/v1/students/profile/skills")
                        .header("Authorization", "Bearer " + auth1.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("React")));
    }
}

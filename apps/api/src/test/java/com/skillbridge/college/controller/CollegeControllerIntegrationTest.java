package com.skillbridge.college.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.college.dto.UpdateCollegeProfileRequest;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.student.entity.StudentProfile;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class CollegeControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String collegeAToken;
    private String collegeBToken;
    private Long collegeAId;
    private Long collegeBId;
    private Long cseDeptId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        studentSkillRepository.deleteAll();
        studentProfileRepository.deleteAll();
        collegeRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();

        // 1. Department
        Department cse = departmentRepository.save(Department.builder().name("Computer Science").code("CSE").active(true).build());
        cseDeptId = cse.getId();

        // 2. College A
        User userCollegeA = userRepository.save(User.builder()
                .email("admin@collegea.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COLLEGE)
                .active(true)
                .build());
        College collegeA = collegeRepository.save(College.builder()
                .userId(userCollegeA.getId())
                .name("College A")
                .address("Campus A")
                .website("https://collegea.edu")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build());
        collegeAId = collegeA.getId();
        collegeAToken = jwtService.generateToken(userCollegeA.getId(), userCollegeA.getEmail(), userCollegeA.getRole(), collegeAId, null, null);

        // 3. College B
        User userCollegeB = userRepository.save(User.builder()
                .email("admin@collegeb.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COLLEGE)
                .active(true)
                .build());
        College collegeB = collegeRepository.save(College.builder()
                .userId(userCollegeB.getId())
                .name("College B")
                .verificationStatus(VerificationStatus.PENDING)
                .build());
        collegeBId = collegeB.getId();
        collegeBToken = jwtService.generateToken(userCollegeB.getId(), userCollegeB.getEmail(), userCollegeB.getRole(), collegeBId, null, null);

        // 4. Student enrolled in College A
        User userStudentA = userRepository.save(User.builder()
                .email("studenta@collegea.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        studentProfileRepository.save(StudentProfile.builder()
                .userId(userStudentA.getId())
                .collegeId(collegeAId)
                .firstName("Alice")
                .lastName("Walker")
                .departmentId(cseDeptId)
                .yearOfStudy((short) 3)
                .cgpa(BigDecimal.valueOf(8.50))
                .build());

        // 5. Student enrolled in College B
        User userStudentB = userRepository.save(User.builder()
                .email("studentb@collegeb.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        studentProfileRepository.save(StudentProfile.builder()
                .userId(userStudentB.getId())
                .collegeId(collegeBId)
                .firstName("Bob")
                .lastName("Smith")
                .departmentId(cseDeptId)
                .yearOfStudy((short) 4)
                .cgpa(BigDecimal.valueOf(9.00))
                .build());
    }

    @Test
    @DisplayName("GET & PUT /api/v1/colleges/profile - View and update college profile")
    void profileGetAndUpdate() throws Exception {
        // GET profile
        mockMvc.perform(get("/api/v1/colleges/profile")
                        .header("Authorization", "Bearer " + collegeAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("College A")))
                .andExpect(jsonPath("$.verificationStatus", is("VERIFIED")));

        // PUT profile
        UpdateCollegeProfileRequest updateReq = UpdateCollegeProfileRequest.builder()
                .name("College A Institute of Technology")
                .address("New Campus 100")
                .website("https://collegea-tech.edu")
                .contactEmail("contact@collegea.edu")
                .contactPhone("1234567890")
                .build();

        mockMvc.perform(put("/api/v1/colleges/profile")
                        .header("Authorization", "Bearer " + collegeAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("College A Institute of Technology")))
                .andExpect(jsonPath("$.address", is("New Campus 100")));
    }

    @Test
    @DisplayName("GET /api/v1/colleges/students - Institutional isolation: College A sees only its own students")
    void getCollegeStudents_InstitutionalIsolation() throws Exception {
        mockMvc.perform(get("/api/v1/colleges/students")
                        .header("Authorization", "Bearer " + collegeAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName", is("Alice")))
                .andExpect(jsonPath("$.content[0].lastName", is("Walker")));

        mockMvc.perform(get("/api/v1/colleges/students")
                        .header("Authorization", "Bearer " + collegeBToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].firstName", is("Bob")))
                .andExpect(jsonPath("$.content[0].lastName", is("Smith")));
    }

    @Test
    @DisplayName("GET /api/v1/colleges/departments - College enrollment breakdown")
    void getCollegeDepartmentsBreakdown() throws Exception {
        mockMvc.perform(get("/api/v1/colleges/departments")
                        .header("Authorization", "Bearer " + collegeAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].departmentCode", is("CSE")))
                .andExpect(jsonPath("$[0].studentCount", is(1)));
    }
}

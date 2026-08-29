package com.skillbridge.college.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.college.dto.CreateDepartmentRequest;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.security.JwtService;
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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DepartmentControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    private String adminToken;
    private String studentToken;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        departmentRepository.deleteAll();
        userRepository.deleteAll();

        User admin = userRepository.save(User.builder()
                .email("admin@skillbridge.org")
                .password(passwordEncoder.encode("AdminPass123!"))
                .role(Role.ADMIN)
                .active(true)
                .build());
        adminToken = jwtService.generateToken(admin.getId(), admin.getEmail(), admin.getRole(), null, null, null);

        User student = userRepository.save(User.builder()
                .email("student@test.edu")
                .password(passwordEncoder.encode("StudentPass123!"))
                .role(Role.STUDENT)
                .active(true)
                .build());
        studentToken = jwtService.generateToken(student.getId(), student.getEmail(), student.getRole(), null, null, 1L);
    }

    @Test
    @DisplayName("GET /api/v1/departments - Should return active departments for authenticated user")
    void listDepartmentsAuthenticated() throws Exception {
        departmentRepository.save(Department.builder().name("Computer Science").code("CSE").active(true).build());
        departmentRepository.save(Department.builder().name("Electrical Engineering").code("EEE").active(true).build());

        mockMvc.perform(get("/api/v1/departments")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].code", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/departments - Should allow ADMIN to create department")
    void createDepartmentAsAdmin() throws Exception {
        CreateDepartmentRequest req = CreateDepartmentRequest.builder()
                .name("Civil Engineering")
                .code("CIVIL")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.code", is("CIVIL")))
                .andExpect(jsonPath("$.isActive", is(true)));
    }

    @Test
    @DisplayName("POST /api/v1/departments - Should return 403 Forbidden when STUDENT tries to create department")
    void createDepartmentAsStudentForbidden() throws Exception {
        CreateDepartmentRequest req = CreateDepartmentRequest.builder()
                .name("Biotechnology")
                .code("BIOTECH")
                .build();

        mockMvc.perform(post("/api/v1/departments")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }
}

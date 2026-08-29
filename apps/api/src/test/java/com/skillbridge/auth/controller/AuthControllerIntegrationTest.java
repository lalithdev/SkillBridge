package com.skillbridge.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.auth.dto.LoginRequest;
import com.skillbridge.auth.dto.RegisterRequest;
import com.skillbridge.auth.dto.RegisterRole;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.repository.CollegeRepository;
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Long sampleCollegeId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        userRepository.deleteAll();
        collegeRepository.deleteAll();

        // Create a test college owner user and college entity for student affiliation tests
        User collegeUser = userRepository.save(User.builder()
                .email("admin@testcollege.edu")
                .password(passwordEncoder.encode("Password@123"))
                .role(Role.COLLEGE)
                .active(true)
                .build());

        College college = collegeRepository.save(College.builder()
                .userId(collegeUser.getId())
                .name("National Institute of Technology")
                .build());

        sampleCollegeId = college.getId();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should register new student with direct JSON payload containing collegeId")
    void registerStudentRawJsonPayload() throws Exception {
        String rawJson = String.format("""
            {
              "name": "Test Student",
              "email": "student1@test.com",
              "password": "Password@123",
              "role": "STUDENT",
              "collegeId": %d
            }
            """, sampleCollegeId);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(rawJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("STUDENT")))
                .andExpect(jsonPath("$.studentProfileId", notNullValue()))
                .andExpect(jsonPath("$.collegeId", is(sampleCollegeId.intValue())));
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Should register new student and return 201 Created")
    void registerStudentEndpoint() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("student@testcollege.edu")
                .password("Password@123")
                .role(RegisterRole.STUDENT)
                .name("John Doe")
                .collegeId(sampleCollegeId)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("STUDENT")))
                .andExpect(jsonPath("$.studentProfileId", notNullValue()))
                .andExpect(jsonPath("$.collegeId", is(sampleCollegeId.intValue())));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should authenticate valid user and return 200 OK")
    void loginEndpoint() throws Exception {
        // First register student
        RegisterRequest registerReq = RegisterRequest.builder()
                .email("alice@testcollege.edu")
                .password("Password@123")
                .role(RegisterRole.STUDENT)
                .name("Alice Smith")
                .collegeId(sampleCollegeId)
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated());

        LoginRequest loginReq = LoginRequest.builder()
                .email("alice@testcollege.edu")
                .password("Password@123")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.role", is("STUDENT")));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Should return 401 Unauthorized for incorrect credentials")
    void loginInvalidCredentials() throws Exception {
        LoginRequest loginReq = LoginRequest.builder()
                .email("nonexistent@domain.com")
                .password("WrongPassword")
                .build();

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)))
                .andExpect(jsonPath("$.error", is("Unauthorized")));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me - Should return current user context with valid Bearer token")
    void getCurrentUserEndpoint() throws Exception {
        RegisterRequest registerReq = RegisterRequest.builder()
                .email("bob@techcorp.com")
                .password("Password@123")
                .role(RegisterRole.COMPANY)
                .name("Tech Corp")
                .build();

        String registerResponseJson = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(registerResponseJson).get("token").asText();

        mockMvc.perform(get("/api/v1/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("bob@techcorp.com")))
                .andExpect(jsonPath("$.role", is("COMPANY")))
                .andExpect(jsonPath("$.isActive", is(true)))
                .andExpect(jsonPath("$.companyProfileId", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout - Should acknowledge logout with 204 No Content")
    void logoutEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isNoContent());
    }
}

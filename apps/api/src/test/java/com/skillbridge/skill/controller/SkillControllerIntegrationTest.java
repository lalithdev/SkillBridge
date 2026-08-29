package com.skillbridge.skill.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.skill.dto.CreateSkillRequest;
import com.skillbridge.skill.dto.UpdateSkillRequest;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class SkillControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkillRepository skillRepository;

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

        skillRepository.deleteAll();
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
    @DisplayName("GET /api/v1/skills - Should return active skills for authenticated users")
    void listSkillsAuthenticated() throws Exception {
        skillRepository.save(Skill.builder().name("React").category("Frontend").active(true).build());
        skillRepository.save(Skill.builder().name("Node.js").category("Backend").active(true).build());
        skillRepository.save(Skill.builder().name("OldTool").category("Legacy").active(false).build());

        mockMvc.perform(get("/api/v1/skills")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Node.js")))
                .andExpect(jsonPath("$[1].name", is("React")));
    }

    @Test
    @DisplayName("POST /api/v1/skills - Should allow ADMIN to create a skill")
    void createSkillAsAdmin() throws Exception {
        CreateSkillRequest request = CreateSkillRequest.builder()
                .name("Spring Boot")
                .category("Backend")
                .build();

        mockMvc.perform(post("/api/v1/skills")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Spring Boot")))
                .andExpect(jsonPath("$.category", is("Backend")))
                .andExpect(jsonPath("$.isActive", is(true)));
    }

    @Test
    @DisplayName("POST /api/v1/skills - Should return 403 Forbidden when STUDENT tries to create a skill")
    void createSkillAsStudentForbidden() throws Exception {
        CreateSkillRequest request = CreateSkillRequest.builder()
                .name("Python")
                .category("Language")
                .build();

        mockMvc.perform(post("/api/v1/skills")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/v1/skills - Should return 400 Bad Request on invalid input")
    void createSkillValidationFailure() throws Exception {
        CreateSkillRequest request = CreateSkillRequest.builder()
                .name("") // blank name
                .build();

        mockMvc.perform(post("/api/v1/skills")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details", notNullValue()));
    }

    @Test
    @DisplayName("POST /api/v1/skills - Should return 409 Conflict on duplicate skill name")
    void createSkillDuplicateConflict() throws Exception {
        skillRepository.save(Skill.builder().name("Docker").category("DevOps").active(true).build());

        CreateSkillRequest request = CreateSkillRequest.builder()
                .name("Docker")
                .category("Containers")
                .build();

        mockMvc.perform(post("/api/v1/skills")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists")));
    }

    @Test
    @DisplayName("PUT /api/v1/skills/{id} - Should update skill as ADMIN")
    void updateSkillAsAdmin() throws Exception {
        Skill skill = skillRepository.save(Skill.builder().name("K8s").category("DevOps").active(true).build());

        UpdateSkillRequest request = UpdateSkillRequest.builder()
                .name("Kubernetes")
                .category("Cloud")
                .active(true)
                .build();

        mockMvc.perform(put("/api/v1/skills/" + skill.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Kubernetes")))
                .andExpect(jsonPath("$.category", is("Cloud")));
    }

    @Test
    @DisplayName("DELETE /api/v1/skills/{id} - Should soft-delete skill as ADMIN")
    void deleteSkillAsAdmin() throws Exception {
        Skill skill = skillRepository.save(Skill.builder().name("SVN").category("VCS").active(true).build());

        mockMvc.perform(delete("/api/v1/skills/" + skill.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        Skill updated = skillRepository.findById(skill.getId()).orElseThrow();
        org.assertj.core.api.Assertions.assertThat(updated.isActive()).isFalse();
    }
}

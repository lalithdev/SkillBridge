package com.skillbridge;

import com.skillbridge.auth.dto.AuthResponse;
import com.skillbridge.auth.dto.LoginRequest;
import com.skillbridge.auth.service.AuthService;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.matching.dto.MatchResultDto;
import com.skillbridge.matching.service.MatchingService;
import com.skillbridge.opportunity.repository.OpportunityRepository;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import com.skillbridge.user.entity.Role;
import com.skillbridge.user.entity.User;
import com.skillbridge.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SeedDataVerificationIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private StudentSkillRepository studentSkillRepository;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Verify BCrypt password compatibility for all demo accounts")
    void testPasswordVerification() {
        // Verify Student Passwords match
        String studentHash = "$2a$10$udV2hQ1/hWZ0VdVH1Sj8L.URgMVOpExgmAIw.F3gcCAIb.lLPdk6u";
        assertThat(passwordEncoder.matches("Skill@Lalith2026", studentHash)).isTrue();

        String msftHash = "$2a$10$fr58CbTZYLBOY27gyzn6COuVQY/cyC8BH5Vx.EXPi4HVVbxh49LvK";
        assertThat(passwordEncoder.matches("MSFT@Hire2026!", msftHash)).isTrue();

        String adminHash = "$2a$10$9dFN9nUKlj.eNqWZgNBPxuah.JMIExYWcWA4zlPLk113WfoLD8p5q";
        assertThat(passwordEncoder.matches("Admin@SkillBridge2026!", adminHash)).isTrue();

        String collegeHash = "$2a$10$NMP2UNLrO.f5AkzcZTMBjuMXcMJzZ9QvvUAcPsxs0WZBS0rYUl88S";
        assertThat(passwordEncoder.matches("IITH@Placement2026", collegeHash)).isTrue();
    }
}

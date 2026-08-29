package com.skillbridge.common.security;

import com.skillbridge.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;
    private static final String TEST_SECRET = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long TEST_EXPIRY_MS = 86400000; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, TEST_EXPIRY_MS);
    }

    @Test
    @DisplayName("Should generate valid JWT and correctly extract all claims for student role")
    void shouldGenerateAndExtractStudentClaims() {
        Long userId = 101L;
        String email = "student@college.edu";
        Role role = Role.STUDENT;
        Long collegeId = 5L;
        Long studentProfileId = 20L;

        String token = jwtService.generateToken(userId, email, role, collegeId, null, studentProfileId);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
        assertThat(jwtService.extractRole(token)).isEqualTo(role);
        assertThat(jwtService.extractCollegeId(token)).isEqualTo(collegeId);
        assertThat(jwtService.extractCompanyProfileId(token)).isNull();
        assertThat(jwtService.extractStudentProfileId(token)).isEqualTo(studentProfileId);
    }

    @Test
    @DisplayName("Should generate valid JWT and correctly extract claims for company role")
    void shouldGenerateAndExtractCompanyClaims() {
        Long userId = 202L;
        String email = "recruiter@techcorp.com";
        Role role = Role.COMPANY;
        Long companyProfileId = 15L;

        String token = jwtService.generateToken(userId, email, role, null, companyProfileId, null);

        assertThat(token).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUserId(token)).isEqualTo(userId);
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
        assertThat(jwtService.extractRole(token)).isEqualTo(role);
        assertThat(jwtService.extractCollegeId(token)).isNull();
        assertThat(jwtService.extractCompanyProfileId(token)).isEqualTo(companyProfileId);
    }

    @Test
    @DisplayName("Should reject invalid or expired tokens")
    void shouldRejectInvalidToken() {
        JwtService shortLivedJwtService = new JwtService(TEST_SECRET, -1000); // Expired immediately
        String expiredToken = shortLivedJwtService.generateToken(1L, "user@domain.com", Role.ADMIN, null, null, null);

        assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
        assertThat(jwtService.isTokenValid("malformed.jwt.token")).isFalse();
    }
}

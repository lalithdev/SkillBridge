package com.skillbridge.auth.service;

import com.skillbridge.auth.dto.AuthResponse;
import com.skillbridge.auth.dto.LoginRequest;
import com.skillbridge.auth.dto.RegisterRequest;
import com.skillbridge.auth.dto.RegisterRole;
import com.skillbridge.auth.service.impl.AuthServiceImpl;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.DuplicateResourceException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.exception.UnauthorizedException;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.user.entity.Role;
import com.skillbridge.user.entity.User;
import com.skillbridge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                studentProfileRepository,
                companyProfileRepository,
                collegeRepository,
                departmentRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    @DisplayName("Should successfully register a new student and create student profile")
    void registerStudentSuccessfully() {
        RegisterRequest request = RegisterRequest.builder()
                .email("student@college.edu")
                .password("Password@123")
                .role(RegisterRole.STUDENT)
                .name("Jane Doe")
                .collegeId(10L)
                .build();

        when(userRepository.existsByEmail("student@college.edu")).thenReturn(false);
        when(collegeRepository.existsById(10L)).thenReturn(true);
        when(passwordEncoder.encode("Password@123")).thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id(1L)
                .email("student@college.edu")
                .password("hashedPassword")
                .role(Role.STUDENT)
                .active(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        StudentProfile savedProfile = StudentProfile.builder()
                .id(100L)
                .userId(1L)
                .collegeId(10L)
                .firstName("Jane")
                .lastName("Doe")
                .build();
        when(studentProfileRepository.save(any(StudentProfile.class))).thenReturn(savedProfile);
        when(jwtService.generateToken(1L, "student@college.edu", Role.STUDENT, 10L, null, 100L)).thenReturn("mockJwtToken");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mockJwtToken");
        assertThat(response.getRole()).isEqualTo(Role.STUDENT);
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getStudentProfileId()).isEqualTo(100L);
        assertThat(response.getCollegeId()).isEqualTo(10L);

        verify(userRepository).save(any(User.class));
        verify(studentProfileRepository).save(any(StudentProfile.class));
    }

    @Test
    @DisplayName("Should successfully register a company profile")
    void registerCompanySuccessfully() {
        RegisterRequest request = RegisterRequest.builder()
                .email("recruiter@techcorp.com")
                .password("Password@123")
                .role(RegisterRole.COMPANY)
                .name("Tech Corp")
                .build();

        when(userRepository.existsByEmail("recruiter@techcorp.com")).thenReturn(false);
        when(passwordEncoder.encode("Password@123")).thenReturn("hashedPassword");

        User savedUser = User.builder()
                .id(2L)
                .email("recruiter@techcorp.com")
                .password("hashedPassword")
                .role(Role.COMPANY)
                .active(true)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        CompanyProfile savedCompany = CompanyProfile.builder()
                .id(200L)
                .userId(2L)
                .name("Tech Corp")
                .build();
        when(companyProfileRepository.save(any(CompanyProfile.class))).thenReturn(savedCompany);
        when(jwtService.generateToken(2L, "recruiter@techcorp.com", Role.COMPANY, null, 200L, null)).thenReturn("companyJwt");

        AuthResponse response = authService.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("companyJwt");
        assertThat(response.getRole()).isEqualTo(Role.COMPANY);
        assertThat(response.getCompanyProfileId()).isEqualTo(200L);
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException on registering with existing email")
    void registerWithExistingEmailThrowsDuplicateException() {
        RegisterRequest request = RegisterRequest.builder()
                .email("existing@domain.com")
                .password("Password@123")
                .role(RegisterRole.STUDENT)
                .name("Alice")
                .collegeId(1L)
                .build();

        when(userRepository.existsByEmail("existing@domain.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Email already registered");
    }

    @Test
    @DisplayName("Should throw BadRequestException if student registration lacks collegeId")
    void studentRegistrationWithoutCollegeIdThrowsBadRequest() {
        RegisterRequest request = RegisterRequest.builder()
                .email("student@domain.com")
                .password("Password@123")
                .role(RegisterRole.STUDENT)
                .name("Bob")
                .collegeId(null)
                .build();

        when(userRepository.existsByEmail("student@domain.com")).thenReturn(false);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("collegeId is required");
    }

    @Test
    @DisplayName("Should successfully login and return JWT token")
    void loginSuccessfully() {
        LoginRequest request = LoginRequest.builder()
                .email("user@domain.com")
                .password("Password@123")
                .build();

        User user = User.builder()
                .id(5L)
                .email("user@domain.com")
                .password("hashedPassword")
                .role(Role.COLLEGE)
                .active(true)
                .build();

        when(userRepository.findByEmail("user@domain.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Password@123", "hashedPassword")).thenReturn(true);

        College college = College.builder().id(50L).userId(5L).name("Engineering College").build();
        when(collegeRepository.findByUserId(5L)).thenReturn(Optional.of(college));
        when(jwtService.generateToken(5L, "user@domain.com", Role.COLLEGE, 50L, null, null)).thenReturn("collegeJwt");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("collegeJwt");
        assertThat(response.getRole()).isEqualTo(Role.COLLEGE);
        assertThat(response.getCollegeId()).isEqualTo(50L);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException for incorrect password")
    void loginWithWrongPasswordThrowsUnauthorized() {
        LoginRequest request = LoginRequest.builder()
                .email("user@domain.com")
                .password("WrongPassword")
                .build();

        User user = User.builder()
                .id(5L)
                .email("user@domain.com")
                .password("hashedPassword")
                .role(Role.STUDENT)
                .active(true)
                .build();

        when(userRepository.findByEmail("user@domain.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("WrongPassword", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("Invalid email or password");
    }
}

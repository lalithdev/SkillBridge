package com.skillbridge.auth.service.impl;

import com.skillbridge.auth.dto.*;
import com.skillbridge.auth.service.AuthService;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.exception.BadRequestException;
import com.skillbridge.common.exception.DuplicateResourceException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.exception.UnauthorizedException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.common.security.JwtService;
import com.skillbridge.company.entity.CompanyProfile;
import com.skillbridge.company.repository.CompanyProfileRepository;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.user.entity.Role;
import com.skillbridge.user.entity.User;
import com.skillbridge.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final CompanyProfileRepository companyProfileRepository;
    private final CollegeRepository collegeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserRepository userRepository,
            StudentProfileRepository studentProfileRepository,
            CompanyProfileRepository companyProfileRepository,
            CollegeRepository collegeRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.companyProfileRepository = companyProfileRepository;
        this.collegeRepository = collegeRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Email already registered: " + email);
        }

        Role role = Role.valueOf(request.getRole().name());

        if (role == Role.STUDENT) {
            if (request.getCollegeId() == null) {
                throw new BadRequestException("collegeId is required for student registration");
            }
            if (!collegeRepository.existsById(request.getCollegeId())) {
                throw new ResourceNotFoundException("College", "id", request.getCollegeId());
            }
            if (request.getDepartmentId() != null && !departmentRepository.existsById(request.getDepartmentId())) {
                throw new ResourceNotFoundException("Department", "id", request.getDepartmentId());
            }
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .active(true)
                .build();

        user = userRepository.save(user);

        Long studentProfileId = null;
        Long companyProfileId = null;
        Long collegeId = null;

        switch (role) {
            case STUDENT -> {
                String fullName = request.getName().trim();
                String[] nameParts = fullName.split("\\s+", 2);
                String firstName = nameParts[0];
                String lastName = nameParts.length > 1 ? nameParts[1] : "";

                StudentProfile studentProfile = StudentProfile.builder()
                        .userId(user.getId())
                        .collegeId(request.getCollegeId())
                        .departmentId(request.getDepartmentId())
                        .firstName(firstName)
                        .lastName(lastName)
                        .build();

                studentProfile = studentProfileRepository.save(studentProfile);
                studentProfileId = studentProfile.getId();
                collegeId = studentProfile.getCollegeId();
            }
            case COMPANY -> {
                CompanyProfile companyProfile = CompanyProfile.builder()
                        .userId(user.getId())
                        .name(request.getName().trim())
                        .verificationStatus(VerificationStatus.PENDING)
                        .build();

                companyProfile = companyProfileRepository.save(companyProfile);
                companyProfileId = companyProfile.getId();
            }
            case COLLEGE -> {
                College college = College.builder()
                        .userId(user.getId())
                        .name(request.getName().trim())
                        .verificationStatus(VerificationStatus.PENDING)
                        .build();

                college = collegeRepository.save(college);
                collegeId = college.getId();
            }
            case ADMIN -> throw new BadRequestException("Admin accounts cannot be self-registered");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                collegeId,
                companyProfileId,
                studentProfileId
        );

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .userId(user.getId())
                .studentProfileId(studentProfileId)
                .companyProfileId(companyProfileId)
                .collegeId(collegeId)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated");
        }

        Long studentProfileId = null;
        Long companyProfileId = null;
        Long collegeId = null;

        switch (user.getRole()) {
            case STUDENT -> {
                var studentOpt = studentProfileRepository.findByUserId(user.getId());
                if (studentOpt.isPresent()) {
                    studentProfileId = studentOpt.get().getId();
                    collegeId = studentOpt.get().getCollegeId();
                }
            }
            case COMPANY -> {
                var companyOpt = companyProfileRepository.findByUserId(user.getId());
                if (companyOpt.isPresent()) {
                    companyProfileId = companyOpt.get().getId();
                }
            }
            case COLLEGE -> {
                var collegeOpt = collegeRepository.findByUserId(user.getId());
                if (collegeOpt.isPresent()) {
                    collegeId = collegeOpt.get().getId();
                }
            }
            case ADMIN -> {}
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                collegeId,
                companyProfileId,
                studentProfileId
        );

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole())
                .userId(user.getId())
                .studentProfileId(studentProfileId)
                .companyProfileId(companyProfileId)
                .collegeId(collegeId)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CurrentUserResponse getCurrentUser(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new UnauthorizedException("Authentication required");
        }

        User user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDetails.getUserId()));

        return CurrentUserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .studentProfileId(userDetails.getStudentProfileId())
                .companyProfileId(userDetails.getCompanyProfileId())
                .collegeId(userDetails.getCollegeId())
                .build();
    }
}

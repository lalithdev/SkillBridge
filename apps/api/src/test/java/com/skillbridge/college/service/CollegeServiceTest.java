package com.skillbridge.college.service;

import com.skillbridge.college.dto.CollegeDepartmentSummaryDto;
import com.skillbridge.college.dto.CollegeProfileDto;
import com.skillbridge.college.dto.StudentSummaryDto;
import com.skillbridge.college.dto.UpdateCollegeProfileRequest;
import com.skillbridge.college.entity.College;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.dto.PageResponse;
import com.skillbridge.common.entity.VerificationStatus;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import com.skillbridge.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollegeServiceTest {

    @Mock
    private CollegeRepository collegeRepository;
    @Mock
    private StudentProfileRepository studentProfileRepository;
    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private StudentSkillRepository studentSkillRepository;

    @InjectMocks
    private CollegeServiceImpl collegeService;

    private CustomUserDetails collegeUser;
    private College mockCollege;

    @BeforeEach
    void setUp() {
        collegeUser = CustomUserDetails.builder()
                .userId(10L)
                .email("dean@univ.edu")
                .role(Role.COLLEGE)
                .active(true)
                .collegeId(1L)
                .build();

        mockCollege = College.builder()
                .id(1L)
                .userId(10L)
                .name("National Engineering College")
                .address("123 Tech Park")
                .website("https://nec.edu")
                .contactEmail("contact@nec.edu")
                .contactPhone("9876543210")
                .verificationStatus(VerificationStatus.VERIFIED)
                .build();
    }

    @Test
    @DisplayName("Get college profile - success")
    void getCollegeProfile_Success() {
        when(collegeRepository.findById(1L)).thenReturn(Optional.of(mockCollege));

        CollegeProfileDto profile = collegeService.getCollegeProfile(collegeUser);

        assertThat(profile).isNotNull();
        assertThat(profile.getName()).isEqualTo("National Engineering College");
        assertThat(profile.getVerificationStatus()).isEqualTo(VerificationStatus.VERIFIED);
    }

    @Test
    @DisplayName("Update college profile - success")
    void updateCollegeProfile_Success() {
        when(collegeRepository.findById(1L)).thenReturn(Optional.of(mockCollege));
        when(collegeRepository.save(any(College.class))).thenAnswer(i -> i.getArgument(0));

        UpdateCollegeProfileRequest request = UpdateCollegeProfileRequest.builder()
                .name("National Institute of Technology")
                .address("New Campus Avenue")
                .website("https://nit.edu")
                .contactEmail("admin@nit.edu")
                .contactPhone("9998887776")
                .build();

        CollegeProfileDto updated = collegeService.updateCollegeProfile(request, collegeUser);

        assertThat(updated.getName()).isEqualTo("National Institute of Technology");
        assertThat(updated.getAddress()).isEqualTo("New Campus Avenue");
    }

    @Test
    @DisplayName("Get college students - returns paginated student list")
    void getCollegeStudents_Success() {
        StudentProfile sp = StudentProfile.builder()
                .id(100L)
                .collegeId(1L)
                .firstName("Bob")
                .lastName("Jones")
                .departmentId(5L)
                .yearOfStudy((short) 4)
                .cgpa(BigDecimal.valueOf(9.10))
                .build();

        Page<StudentProfile> studentPage = new PageImpl<>(List.of(sp));
        when(studentProfileRepository.findCollegeStudents(eq(1L), any(), any(), any(), any(Pageable.class)))
                .thenReturn(studentPage);

        Department dept = Department.builder().id(5L).name("Computer Science").code("CSE").build();
        when(departmentRepository.findById(5L)).thenReturn(Optional.of(dept));
        when(studentSkillRepository.findByStudentProfileId(100L)).thenReturn(Collections.emptyList());

        PageResponse<StudentSummaryDto> response = collegeService.getCollegeStudents(
                null, null, null, null, 0, 10, collegeUser);

        assertThat(response).isNotNull();
        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).getFirstName()).isEqualTo("Bob");
        assertThat(response.getContent().get(0).getDepartmentName()).isEqualTo("Computer Science");
    }

    @Test
    @DisplayName("Get college department breakdown - returns counts per department")
    void getCollegeDepartmentBreakdown_Success() {
        Department dept1 = Department.builder().id(1L).name("CSE").code("CSE").build();
        Department dept2 = Department.builder().id(2L).name("ECE").code("ECE").build();

        when(departmentRepository.findAll()).thenReturn(List.of(dept1, dept2));
        when(studentProfileRepository.countByCollegeIdAndDepartmentId(1L, 1L)).thenReturn(120L);
        when(studentProfileRepository.countByCollegeIdAndDepartmentId(1L, 2L)).thenReturn(85L);

        List<CollegeDepartmentSummaryDto> breakdown = collegeService.getCollegeDepartmentBreakdown(null, collegeUser);

        assertThat(breakdown).hasSize(2);
        assertThat(breakdown.get(0).getStudentCount()).isEqualTo(120);
        assertThat(breakdown.get(1).getStudentCount()).isEqualTo(85);
    }
}

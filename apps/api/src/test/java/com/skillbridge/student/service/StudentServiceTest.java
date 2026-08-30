package com.skillbridge.student.service;

import com.skillbridge.college.entity.College;
import com.skillbridge.college.entity.Department;
import com.skillbridge.college.repository.CollegeRepository;
import com.skillbridge.college.repository.DepartmentRepository;
import com.skillbridge.common.exception.DuplicateResourceException;
import com.skillbridge.common.exception.ForbiddenException;
import com.skillbridge.common.exception.ResourceNotFoundException;
import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.file.service.FileStorageService;
import com.skillbridge.skill.dto.SkillDto;
import com.skillbridge.skill.entity.Skill;
import com.skillbridge.skill.repository.SkillRepository;
import com.skillbridge.student.dto.*;
import com.skillbridge.student.entity.Certification;
import com.skillbridge.student.entity.Project;
import com.skillbridge.student.entity.StudentProfile;
import com.skillbridge.student.entity.StudentSkill;
import com.skillbridge.student.repository.CertificationRepository;
import com.skillbridge.student.repository.ProjectRepository;
import com.skillbridge.student.repository.StudentProfileRepository;
import com.skillbridge.student.repository.StudentSkillRepository;
import com.skillbridge.user.entity.Role;
import com.skillbridge.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentProfileRepository studentProfileRepository;

    @Mock
    private StudentSkillRepository studentSkillRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CertificationRepository certificationRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CollegeRepository collegeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    private StudentService studentService;

    private CustomUserDetails studentUser;
    private StudentProfile sampleProfile;

    @BeforeEach
    void setUp() {
        studentService = new StudentServiceImpl(
                studentProfileRepository,
                studentSkillRepository,
                projectRepository,
                certificationRepository,
                skillRepository,
                departmentRepository,
                collegeRepository,
                userRepository,
                fileStorageService
        );

        studentUser = CustomUserDetails.builder()
                .userId(1L)
                .email("student@test.edu")
                .role(Role.STUDENT)
                .studentProfileId(10L)
                .collegeId(20L)
                .authorities(Collections.emptyList())
                .build();

        sampleProfile = StudentProfile.builder()
                .id(10L)
                .userId(1L)
                .collegeId(20L)
                .firstName("John")
                .lastName("Doe")
                .departmentId(30L)
                .yearOfStudy((short) 3)
                .cgpa(new BigDecimal("8.50"))
                .careerInterests("Software Engineering")
                .portfolioUrl("https://johndoe.dev")
                .githubUrl("https://github.com/johndoe")
                .resumePath("resume_10.pdf")
                .build();
    }

    @Test
    @DisplayName("getStudentProfile - returns fully populated StudentProfileResponse")
    void getStudentProfileSuccess() {
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(sampleProfile));
        when(collegeRepository.findById(20L)).thenReturn(Optional.of(College.builder().id(20L).name("NIT Trichy").build()));
        when(departmentRepository.findById(30L)).thenReturn(Optional.of(Department.builder().id(30L).name("Computer Science").code("CSE").build()));

        StudentSkill ss = StudentSkill.builder().id(1L).studentProfileId(10L).skillId(100L).build();
        Skill skill = Skill.builder().id(100L).name("Java").category("Language").active(true).build();
        when(studentSkillRepository.findByStudentProfileId(10L)).thenReturn(List.of(ss));
        when(skillRepository.findAllById(List.of(100L))).thenReturn(List.of(skill));

        Project proj = Project.builder().id(5L).studentProfileId(10L).title("Web App").build();
        when(projectRepository.findByStudentProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(proj));

        Certification cert = Certification.builder().id(6L).studentProfileId(10L).title("AWS Certified").build();
        when(certificationRepository.findByStudentProfileIdOrderByCreatedAtDesc(10L)).thenReturn(List.of(cert));

        StudentProfileResponse response = studentService.getStudentProfile(studentUser);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getCollegeName()).isEqualTo("NIT Trichy");
        assertThat(response.getDepartmentCode()).isEqualTo("CSE");
        assertThat(response.isHasResume()).isTrue();
        assertThat(response.getSkills()).hasSize(1);
        assertThat(response.getProjects()).hasSize(1);
        assertThat(response.getCertifications()).hasSize(1);
    }

    @Test
    @DisplayName("updateStudentProfile - successfully updates student details")
    void updateStudentProfileSuccess() {
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(sampleProfile));
        when(departmentRepository.existsById(30L)).thenReturn(true);
        when(studentProfileRepository.save(any(StudentProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateStudentProfileRequest updateReq = UpdateStudentProfileRequest.builder()
                .firstName("Johnny")
                .lastName("Doe")
                .departmentId(30L)
                .yearOfStudy(4)
                .cgpa(new BigDecimal("9.10"))
                .careerInterests("AI & ML")
                .build();

        StudentProfileResponse response = studentService.updateStudentProfile(studentUser, updateReq);

        assertThat(response.getFirstName()).isEqualTo("Johnny");
        assertThat(response.getYearOfStudy()).isEqualTo(4);
        assertThat(response.getCgpa()).isEqualTo(new BigDecimal("9.10"));
        assertThat(response.getCareerInterests()).isEqualTo("AI & ML");
    }

    @Test
    @DisplayName("getStudentProfileById - allows affiliated college to view student")
    void getStudentProfileByIdAffiliatedCollegeAllowed() {
        CustomUserDetails collegeUser = CustomUserDetails.builder()
                .userId(2L)
                .email("tpo@nit.edu")
                .role(Role.COLLEGE)
                .collegeId(20L)
                .authorities(Collections.emptyList())
                .build();

        when(studentProfileRepository.findById(10L)).thenReturn(Optional.of(sampleProfile));

        StudentProfileResponse response = studentService.getStudentProfileById(10L, collegeUser);

        assertThat(response.getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("getStudentProfileById - denies non-affiliated college with 403 Forbidden")
    void getStudentProfileByIdNonAffiliatedCollegeForbidden() {
        CustomUserDetails otherCollegeUser = CustomUserDetails.builder()
                .userId(3L)
                .email("tpo@other.edu")
                .role(Role.COLLEGE)
                .collegeId(99L) // Different college
                .authorities(Collections.emptyList())
                .build();

        when(studentProfileRepository.findById(10L)).thenReturn(Optional.of(sampleProfile));

        assertThatThrownBy(() -> studentService.getStudentProfileById(10L, otherCollegeUser))
                .isInstanceOf(ForbiddenException.class)
                .hasMessageContaining("does not belong to your institution");
    }

    @Test
    @DisplayName("addStudentSkill - successfully adds skill to profile")
    void addStudentSkillSuccess() {
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(sampleProfile));
        Skill skill = Skill.builder().id(101L).name("Spring Boot").category("Framework").active(true).build();
        when(skillRepository.findById(101L)).thenReturn(Optional.of(skill));
        when(studentSkillRepository.existsByStudentProfileIdAndSkillId(10L, 101L)).thenReturn(false);

        SkillDto result = studentService.addStudentSkill(studentUser, new AddStudentSkillRequest(101L));

        assertThat(result.getId()).isEqualTo(101L);
        assertThat(result.getName()).isEqualTo("Spring Boot");
        verify(studentSkillRepository).save(any(StudentSkill.class));
    }

    @Test
    @DisplayName("addStudentSkill - throws DuplicateResourceException if already exists")
    void addStudentSkillDuplicateThrows() {
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(sampleProfile));
        Skill skill = Skill.builder().id(101L).name("Spring Boot").build();
        when(skillRepository.findById(101L)).thenReturn(Optional.of(skill));
        when(studentSkillRepository.existsByStudentProfileIdAndSkillId(10L, 101L)).thenReturn(true);

        assertThatThrownBy(() -> studentService.addStudentSkill(studentUser, new AddStudentSkillRequest(101L)))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already present on profile");
    }

    @Test
    @DisplayName("removeStudentSkill - successfully removes skill")
    void removeStudentSkillSuccess() {
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(sampleProfile));
        StudentSkill ss = StudentSkill.builder().id(1L).studentProfileId(10L).skillId(101L).build();
        when(studentSkillRepository.findByStudentProfileIdAndSkillId(10L, 101L)).thenReturn(Optional.of(ss));

        studentService.removeStudentSkill(studentUser, 101L);

        verify(studentSkillRepository).delete(ss);
    }

    @Test
    @DisplayName("createStudentProject - creates project for student")
    void createStudentProjectSuccess() {
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(sampleProfile));
        when(projectRepository.save(any(Project.class))).thenAnswer(inv -> {
            Project p = inv.getArgument(0);
            p.setId(50L);
            return p;
        });

        ProjectRequest req = ProjectRequest.builder()
                .title("Face Recognition App")
                .description("OpenCV project")
                .projectUrl("https://github.com/test/face")
                .build();

        ProjectDto result = studentService.createStudentProject(studentUser, req);

        assertThat(result.getId()).isEqualTo(50L);
        assertThat(result.getTitle()).isEqualTo("Face Recognition App");
    }

    @Test
    @DisplayName("createStudentCertification - creates certification for student")
    void createStudentCertificationSuccess() {
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(sampleProfile));
        when(certificationRepository.save(any(Certification.class))).thenAnswer(inv -> {
            Certification c = inv.getArgument(0);
            c.setId(60L);
            return c;
        });

        CertificationRequest req = CertificationRequest.builder()
                .title("AWS Solutions Architect")
                .issuer("Amazon")
                .issuedDate(LocalDate.of(2025, 5, 10))
                .build();

        CertificationDto result = studentService.createStudentCertification(studentUser, req);

        assertThat(result.getId()).isEqualTo(60L);
        assertThat(result.getTitle()).isEqualTo("AWS Solutions Architect");
        assertThat(result.getIssuedDate()).isEqualTo(LocalDate.of(2025, 5, 10));
    }

    @Test
    @DisplayName("uploadResume - stores file and updates resume path")
    void uploadResumeSuccess() {
        when(studentProfileRepository.findByUserId(1L)).thenReturn(Optional.of(sampleProfile));
        when(fileStorageService.storeResume(any(), eq(10L))).thenReturn("resume_student_10_uuid.pdf");

        MockMultipartFile file = new MockMultipartFile("file", "cv.pdf", "application/pdf", "content".getBytes());

        ResumeUploadResponse response = studentService.uploadResume(studentUser, file);

        assertThat(response.getResumePath()).isEqualTo("resume_student_10_uuid.pdf");
        assertThat(response.getFileName()).isEqualTo("cv.pdf");
        verify(studentProfileRepository).save(sampleProfile);
    }

    @Test
    @DisplayName("downloadResume - downloads resume for authorized user")
    void downloadResumeSuccess() {
        when(studentProfileRepository.findById(10L)).thenReturn(Optional.of(sampleProfile));
        Resource dummyResource = new ByteArrayResource("pdf-bytes".getBytes());
        when(fileStorageService.loadAsResource("resume_10.pdf")).thenReturn(dummyResource);

        Resource result = studentService.downloadResume(10L, studentUser);

        assertThat(result).isNotNull();
    }
}

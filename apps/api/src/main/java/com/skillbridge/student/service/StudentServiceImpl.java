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
import com.skillbridge.user.entity.User;
import com.skillbridge.user.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentProfileRepository studentProfileRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ProjectRepository projectRepository;
    private final CertificationRepository certificationRepository;
    private final SkillRepository skillRepository;
    private final DepartmentRepository departmentRepository;
    private final CollegeRepository collegeRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public StudentServiceImpl(
            StudentProfileRepository studentProfileRepository,
            StudentSkillRepository studentSkillRepository,
            ProjectRepository projectRepository,
            CertificationRepository certificationRepository,
            SkillRepository skillRepository,
            DepartmentRepository departmentRepository,
            CollegeRepository collegeRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {
        this.studentProfileRepository = studentProfileRepository;
        this.studentSkillRepository = studentSkillRepository;
        this.projectRepository = projectRepository;
        this.certificationRepository = certificationRepository;
        this.skillRepository = skillRepository;
        this.departmentRepository = departmentRepository;
        this.collegeRepository = collegeRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    private StudentProfile getStudentProfileFromUser(CustomUserDetails user) {
        if (user == null || user.getUserId() == null) {
            throw new ForbiddenException("Authentication required");
        }
        return studentProfileRepository.findByUserId(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for user: " + user.getEmail()));
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfile(CustomUserDetails user) {
        StudentProfile profile = getStudentProfileFromUser(user);
        return buildFullProfileResponse(profile);
    }

    @Override
    @Transactional
    public StudentProfileResponse updateStudentProfile(CustomUserDetails user, UpdateStudentProfileRequest request) {
        StudentProfile profile = getStudentProfileFromUser(user);

        if (request.getCollegeId() != null) {
            if (!collegeRepository.existsById(request.getCollegeId())) {
                throw new ResourceNotFoundException("College not found with id: " + request.getCollegeId());
            }
            profile.setCollegeId(request.getCollegeId());
        }

        if (request.getDepartmentId() != null) {
            if (!departmentRepository.existsById(request.getDepartmentId())) {
                throw new ResourceNotFoundException("Department not found with id: " + request.getDepartmentId());
            }
            profile.setDepartmentId(request.getDepartmentId());
        }

        if (request.getName() != null && !request.getName().trim().isEmpty()
                && (request.getFirstName() == null || request.getFirstName().trim().isEmpty())) {
            String[] parts = request.getName().trim().split("\\s+", 2);
            profile.setFirstName(parts[0]);
            profile.setLastName(parts.length > 1 ? parts[1] : "");
        } else {
            if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
                profile.setFirstName(request.getFirstName().trim());
            }
            if (request.getLastName() != null) {
                profile.setLastName(request.getLastName().trim());
            }
        }

        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone().trim());
        }

        if (request.getYearOfStudy() != null) {
            profile.setYearOfStudy(request.getYearOfStudy().shortValue());
        } else if (request.getGraduationYear() != null) {
            int currentYear = java.time.Year.now().getValue();
            int diff = request.getGraduationYear() - currentYear;
            int calculated = 4 - diff;
            profile.setYearOfStudy((short) Math.max(1, Math.min(8, calculated > 0 ? calculated : 4)));
        }

        if (request.getCgpa() != null) {
            profile.setCgpa(request.getCgpa());
        }

        if (request.getCareerInterests() != null) {
            profile.setCareerInterests(request.getCareerInterests());
        } else if (request.getBio() != null) {
            profile.setCareerInterests(request.getBio());
        }

        if (request.getPortfolioUrl() != null) {
            profile.setPortfolioUrl(request.getPortfolioUrl());
        }

        if (request.getGithubUrl() != null) {
            profile.setGithubUrl(request.getGithubUrl());
        }

        StudentProfile updated = studentProfileRepository.save(profile);
        return buildFullProfileResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentProfileResponse getStudentProfileById(Long studentProfileId, CustomUserDetails currentUser) {
        StudentProfile profile = studentProfileRepository.findById(studentProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found with id: " + studentProfileId));

        if (currentUser.getRole() == Role.COLLEGE) {
            if (currentUser.getCollegeId() == null || !currentUser.getCollegeId().equals(profile.getCollegeId())) {
                throw new ForbiddenException("Access denied: student does not belong to your institution");
            }
        } else if (currentUser.getRole() == Role.STUDENT) {
            if (currentUser.getStudentProfileId() == null || !currentUser.getStudentProfileId().equals(studentProfileId)) {
                throw new ForbiddenException("Access denied: you can only view your own profile");
            }
        }
        // COMPANY and ADMIN have access as defined in openapi.yaml

        return buildFullProfileResponse(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SkillDto> getStudentSkills(CustomUserDetails user) {
        StudentProfile profile = getStudentProfileFromUser(user);
        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentProfileId(profile.getId());
        if (studentSkills.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> skillIds = studentSkills.stream().map(StudentSkill::getSkillId).toList();
        List<Skill> skills = skillRepository.findAllById(skillIds);

        return skills.stream()
                .map(s -> SkillDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .category(s.getCategory())
                        .active(s.isActive())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillDto addStudentSkill(CustomUserDetails user, AddStudentSkillRequest request) {
        StudentProfile profile = getStudentProfileFromUser(user);
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + request.getSkillId()));

        if (studentSkillRepository.existsByStudentProfileIdAndSkillId(profile.getId(), skill.getId())) {
            throw new DuplicateResourceException("Skill already present on profile: " + skill.getName());
        }

        StudentSkill studentSkill = StudentSkill.builder()
                .studentProfileId(profile.getId())
                .skillId(skill.getId())
                .build();

        studentSkillRepository.save(studentSkill);

        return SkillDto.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .active(skill.isActive())
                .build();
    }

    @Override
    @Transactional
    public void removeStudentSkill(CustomUserDetails user, Long skillId) {
        StudentProfile profile = getStudentProfileFromUser(user);

        StudentSkill studentSkill = studentSkillRepository.findByStudentProfileIdAndSkillId(profile.getId(), skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found on student profile with skillId: " + skillId));

        studentSkillRepository.delete(studentSkill);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> getStudentProjects(CustomUserDetails user) {
        StudentProfile profile = getStudentProfileFromUser(user);
        List<Project> projects = projectRepository.findByStudentProfileIdOrderByCreatedAtDesc(profile.getId());
        return projects.stream().map(this::mapToProjectDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProjectDto createStudentProject(CustomUserDetails user, ProjectRequest request) {
        StudentProfile profile = getStudentProfileFromUser(user);

        Project project = Project.builder()
                .studentProfileId(profile.getId())
                .title(request.getTitle().trim())
                .description(request.getDescription())
                .projectUrl(request.getProjectUrl())
                .build();

        Project saved = projectRepository.save(project);
        return mapToProjectDto(saved);
    }

    @Override
    @Transactional
    public ProjectDto updateStudentProject(CustomUserDetails user, Long projectId, ProjectRequest request) {
        StudentProfile profile = getStudentProfileFromUser(user);

        Project project = projectRepository.findByIdAndStudentProfileId(projectId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        project.setTitle(request.getTitle().trim());
        project.setDescription(request.getDescription());
        project.setProjectUrl(request.getProjectUrl());

        Project updated = projectRepository.save(project);
        return mapToProjectDto(updated);
    }

    @Override
    @Transactional
    public void deleteStudentProject(CustomUserDetails user, Long projectId) {
        StudentProfile profile = getStudentProfileFromUser(user);

        Project project = projectRepository.findByIdAndStudentProfileId(projectId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        projectRepository.delete(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CertificationDto> getStudentCertifications(CustomUserDetails user) {
        StudentProfile profile = getStudentProfileFromUser(user);
        List<Certification> certs = certificationRepository.findByStudentProfileIdOrderByCreatedAtDesc(profile.getId());
        return certs.stream().map(this::mapToCertificationDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CertificationDto createStudentCertification(CustomUserDetails user, CertificationRequest request) {
        StudentProfile profile = getStudentProfileFromUser(user);

        Certification cert = Certification.builder()
                .studentProfileId(profile.getId())
                .title(request.getTitle().trim())
                .issuer(request.getIssuer())
                .issuedDate(request.getIssuedDate())
                .certificateUrl(request.getCertificateUrl())
                .build();

        Certification saved = certificationRepository.save(cert);
        return mapToCertificationDto(saved);
    }

    @Override
    @Transactional
    public CertificationDto updateStudentCertification(CustomUserDetails user, Long certificationId, CertificationRequest request) {
        StudentProfile profile = getStudentProfileFromUser(user);

        Certification cert = certificationRepository.findByIdAndStudentProfileId(certificationId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + certificationId));

        cert.setTitle(request.getTitle().trim());
        cert.setIssuer(request.getIssuer());
        cert.setIssuedDate(request.getIssuedDate());
        cert.setCertificateUrl(request.getCertificateUrl());

        Certification updated = certificationRepository.save(cert);
        return mapToCertificationDto(updated);
    }

    @Override
    @Transactional
    public void deleteStudentCertification(CustomUserDetails user, Long certificationId) {
        StudentProfile profile = getStudentProfileFromUser(user);

        Certification cert = certificationRepository.findByIdAndStudentProfileId(certificationId, profile.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + certificationId));

        certificationRepository.delete(cert);
    }

    @Override
    @Transactional
    public ResumeUploadResponse uploadResume(CustomUserDetails user, MultipartFile file) {
        StudentProfile profile = getStudentProfileFromUser(user);

        // If previous resume exists, delete it
        if (profile.getResumePath() != null && !profile.getResumePath().trim().isEmpty()) {
            fileStorageService.deleteFile(profile.getResumePath());
        }

        String storedFileName = fileStorageService.storeResume(file, profile.getId());
        profile.setResumePath(storedFileName);
        studentProfileRepository.save(profile);

        return ResumeUploadResponse.builder()
                .resumePath(storedFileName)
                .fileName(file.getOriginalFilename())
                .build();
    }

    @Override
    @Transactional
    public void deleteResume(CustomUserDetails user) {
        StudentProfile profile = getStudentProfileFromUser(user);
        if (profile.getResumePath() == null || profile.getResumePath().trim().isEmpty()) {
            throw new ResourceNotFoundException("No resume found for student profile");
        }

        fileStorageService.deleteFile(profile.getResumePath());
        profile.setResumePath(null);
        studentProfileRepository.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public Resource downloadResume(Long studentId, CustomUserDetails currentUser) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found with id: " + studentId));

        validateResumeAccess(profile, currentUser);

        if (profile.getResumePath() == null || profile.getResumePath().trim().isEmpty()) {
            throw new ResourceNotFoundException("Resume not uploaded for student: " + studentId);
        }

        return fileStorageService.loadAsResource(profile.getResumePath());
    }

    @Override
    @Transactional(readOnly = true)
    public MediaType getResumeMediaType(Long studentId, CustomUserDetails currentUser) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found with id: " + studentId));

        validateResumeAccess(profile, currentUser);

        if (profile.getResumePath() == null) {
            throw new ResourceNotFoundException("Resume not found");
        }

        return fileStorageService.determineMediaType(profile.getResumePath());
    }

    @Override
    @Transactional(readOnly = true)
    public String getResumeFileName(Long studentId, CustomUserDetails currentUser) {
        StudentProfile profile = studentProfileRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found with id: " + studentId));

        validateResumeAccess(profile, currentUser);
        return profile.getResumePath();
    }

    private void validateResumeAccess(StudentProfile profile, CustomUserDetails currentUser) {
        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (currentUser.getRole() == Role.STUDENT) {
            if (currentUser.getStudentProfileId() == null || !currentUser.getStudentProfileId().equals(profile.getId())) {
                throw new ForbiddenException("Access denied: you can only download your own resume");
            }
            return;
        }
        if (currentUser.getRole() == Role.COLLEGE) {
            if (currentUser.getCollegeId() == null || !currentUser.getCollegeId().equals(profile.getCollegeId())) {
                throw new ForbiddenException("Access denied: student does not belong to your college");
            }
            return;
        }
        if (currentUser.getRole() == Role.COMPANY) {
            // Permitted for company candidate review
            return;
        }
        throw new ForbiddenException("Access denied");
    }

    private StudentProfileResponse buildFullProfileResponse(StudentProfile profile) {
        String collegeName = null;
        if (profile.getCollegeId() != null) {
            collegeName = collegeRepository.findById(profile.getCollegeId())
                    .map(College::getName)
                    .orElse(null);
        }

        String departmentName = null;
        String departmentCode = null;
        if (profile.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(profile.getDepartmentId()).orElse(null);
            if (dept != null) {
                departmentName = dept.getName();
                departmentCode = dept.getCode();
            }
        }

        List<StudentSkill> studentSkills = studentSkillRepository.findByStudentProfileId(profile.getId());
        List<SkillDto> skillDtos = Collections.emptyList();
        if (!studentSkills.isEmpty()) {
            List<Long> skillIds = studentSkills.stream().map(StudentSkill::getSkillId).toList();
            Map<Long, Skill> skillMap = skillRepository.findAllById(skillIds).stream()
                    .collect(Collectors.toMap(Skill::getId, Function.identity()));
            skillDtos = studentSkills.stream()
                    .map(ss -> skillMap.get(ss.getSkillId()))
                    .filter(s -> s != null)
                    .map(s -> SkillDto.builder()
                            .id(s.getId())
                            .name(s.getName())
                            .category(s.getCategory())
                            .active(s.isActive())
                            .build())
                    .collect(Collectors.toList());
        }

        List<ProjectDto> projectDtos = projectRepository.findByStudentProfileIdOrderByCreatedAtDesc(profile.getId())
                .stream().map(this::mapToProjectDto).collect(Collectors.toList());

        List<CertificationDto> certDtos = certificationRepository.findByStudentProfileIdOrderByCreatedAtDesc(profile.getId())
                .stream().map(this::mapToCertificationDto).collect(Collectors.toList());

        boolean hasResume = (profile.getResumePath() != null && !profile.getResumePath().trim().isEmpty());

        String email = null;
        if (profile.getUserId() != null) {
            email = userRepository.findById(profile.getUserId())
                    .map(User::getEmail)
                    .orElse(null);
        }

        Integer graduationYear = profile.getYearOfStudy() != null
                ? Integer.valueOf(java.time.Year.now().getValue() + (4 - profile.getYearOfStudy().intValue()))
                : null;

        return StudentProfileResponse.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .email(email)
                .phone(profile.getPhone())
                .collegeId(profile.getCollegeId())
                .collegeName(collegeName)
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .departmentId(profile.getDepartmentId())
                .departmentName(departmentName)
                .departmentCode(departmentCode)
                .yearOfStudy(profile.getYearOfStudy() != null ? profile.getYearOfStudy().intValue() : null)
                .graduationYear(graduationYear)
                .cgpa(profile.getCgpa())
                .careerInterests(profile.getCareerInterests())
                .portfolioUrl(profile.getPortfolioUrl())
                .githubUrl(profile.getGithubUrl())
                .resumePath(profile.getResumePath())
                .hasResume(hasResume)
                .skills(skillDtos)
                .projects(projectDtos)
                .certifications(certDtos)
                .build();
    }

    private ProjectDto mapToProjectDto(Project project) {
        return ProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .projectUrl(project.getProjectUrl())
                .build();
    }

    private CertificationDto mapToCertificationDto(Certification cert) {
        return CertificationDto.builder()
                .id(cert.getId())
                .title(cert.getTitle())
                .issuer(cert.getIssuer())
                .issuedDate(cert.getIssuedDate())
                .certificateUrl(cert.getCertificateUrl())
                .build();
    }
}

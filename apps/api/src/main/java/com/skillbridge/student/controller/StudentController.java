package com.skillbridge.student.controller;

import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.skill.dto.SkillDto;
import com.skillbridge.student.dto.*;
import com.skillbridge.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // --- Profile ---

    @GetMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> getStudentProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        StudentProfileResponse profile = studentService.getStudentProfile(currentUser);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentProfileResponse> updateStudentProfile(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody UpdateStudentProfileRequest request) {
        StudentProfileResponse profile = studentService.updateStudentProfile(currentUser, request);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}/profile")
    @PreAuthorize("hasAnyRole('COLLEGE', 'COMPANY', 'ADMIN')")
    public ResponseEntity<StudentProfileResponse> getStudentProfileById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        StudentProfileResponse profile = studentService.getStudentProfileById(id, currentUser);
        return ResponseEntity.ok(profile);
    }

    // --- Skills ---

    @GetMapping("/profile/skills")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<SkillDto>> getStudentSkills(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<SkillDto> skills = studentService.getStudentSkills(currentUser);
        return ResponseEntity.ok(skills);
    }

    @PostMapping("/profile/skills")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SkillDto> addStudentSkill(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody AddStudentSkillRequest request) {
        SkillDto skill = studentService.addStudentSkill(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(skill);
    }

    @DeleteMapping("/profile/skills/{skillId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> removeStudentSkill(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long skillId) {
        studentService.removeStudentSkill(currentUser, skillId);
        return ResponseEntity.noContent().build();
    }

    // --- Projects ---

    @GetMapping("/profile/projects")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<ProjectDto>> getStudentProjects(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<ProjectDto> projects = studentService.getStudentProjects(currentUser);
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/profile/projects")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProjectDto> createStudentProject(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody ProjectRequest request) {
        ProjectDto project = studentService.createStudentProject(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(project);
    }

    @PutMapping("/profile/projects/{projectId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProjectDto> updateStudentProject(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long projectId,
            @Valid @RequestBody ProjectRequest request) {
        ProjectDto project = studentService.updateStudentProject(currentUser, projectId, request);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/profile/projects/{projectId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteStudentProject(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long projectId) {
        studentService.deleteStudentProject(currentUser, projectId);
        return ResponseEntity.noContent().build();
    }

    // --- Certifications ---

    @GetMapping("/profile/certifications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<CertificationDto>> getStudentCertifications(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        List<CertificationDto> certs = studentService.getStudentCertifications(currentUser);
        return ResponseEntity.ok(certs);
    }

    @PostMapping("/profile/certifications")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CertificationDto> createStudentCertification(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @RequestBody CertificationRequest request) {
        CertificationDto cert = studentService.createStudentCertification(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cert);
    }

    @PutMapping("/profile/certifications/{certificationId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<CertificationDto> updateStudentCertification(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long certificationId,
            @Valid @RequestBody CertificationRequest request) {
        CertificationDto cert = studentService.updateStudentCertification(currentUser, certificationId, request);
        return ResponseEntity.ok(cert);
    }

    @DeleteMapping("/profile/certifications/{certificationId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteStudentCertification(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @PathVariable Long certificationId) {
        studentService.deleteStudentCertification(currentUser, certificationId);
        return ResponseEntity.noContent().build();
    }

    // --- Resume ---

    @PostMapping(value = "/profile/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ResumeUploadResponse> uploadResume(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            @RequestParam("file") MultipartFile file) {
        ResumeUploadResponse response = studentService.uploadResume(currentUser, file);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/profile/resume")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> deleteResume(
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        studentService.deleteResume(currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{studentId}/resume")
    @PreAuthorize("hasAnyRole('STUDENT', 'COMPANY', 'ADMIN', 'COLLEGE')")
    public ResponseEntity<Resource> downloadResume(
            @PathVariable Long studentId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        Resource file = studentService.downloadResume(studentId, currentUser);
        MediaType mediaType = studentService.getResumeMediaType(studentId, currentUser);
        String fileName = studentService.getResumeFileName(studentId, currentUser);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(file);
    }
}

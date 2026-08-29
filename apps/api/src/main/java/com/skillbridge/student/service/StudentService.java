package com.skillbridge.student.service;

import com.skillbridge.common.security.CustomUserDetails;
import com.skillbridge.skill.dto.SkillDto;
import com.skillbridge.student.dto.*;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {

    StudentProfileResponse getStudentProfile(CustomUserDetails user);

    StudentProfileResponse updateStudentProfile(CustomUserDetails user, UpdateStudentProfileRequest request);

    StudentProfileResponse getStudentProfileById(Long studentProfileId, CustomUserDetails currentUser);

    List<SkillDto> getStudentSkills(CustomUserDetails user);

    SkillDto addStudentSkill(CustomUserDetails user, AddStudentSkillRequest request);

    void removeStudentSkill(CustomUserDetails user, Long skillId);

    List<ProjectDto> getStudentProjects(CustomUserDetails user);

    ProjectDto createStudentProject(CustomUserDetails user, ProjectRequest request);

    ProjectDto updateStudentProject(CustomUserDetails user, Long projectId, ProjectRequest request);

    void deleteStudentProject(CustomUserDetails user, Long projectId);

    List<CertificationDto> getStudentCertifications(CustomUserDetails user);

    CertificationDto createStudentCertification(CustomUserDetails user, CertificationRequest request);

    CertificationDto updateStudentCertification(CustomUserDetails user, Long certificationId, CertificationRequest request);

    void deleteStudentCertification(CustomUserDetails user, Long certificationId);

    ResumeUploadResponse uploadResume(CustomUserDetails user, MultipartFile file);

    void deleteResume(CustomUserDetails user);

    Resource downloadResume(Long studentId, CustomUserDetails currentUser);

    MediaType getResumeMediaType(Long studentId, CustomUserDetails currentUser);

    String getResumeFileName(Long studentId, CustomUserDetails currentUser);
}

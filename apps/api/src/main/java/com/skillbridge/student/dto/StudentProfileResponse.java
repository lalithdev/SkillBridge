package com.skillbridge.student.dto;

import com.skillbridge.skill.dto.SkillDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProfileResponse {

    private Long id;
    private Long userId;
    private String email;
    private String phone;
    private Long collegeId;
    private String collegeName;
    private String firstName;
    private String lastName;
    private Long departmentId;
    private String departmentName;
    private String departmentCode;
    private Integer yearOfStudy;
    private Integer graduationYear;
    private BigDecimal cgpa;
    private String careerInterests;
    private String portfolioUrl;
    private String githubUrl;
    private String resumePath;
    private boolean hasResume;
    private List<SkillDto> skills;
    private List<ProjectDto> projects;
    private List<CertificationDto> certifications;
}

package com.skillbridge.student.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStudentProfileRequest {

    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    private String name;

    @Size(max = 30, message = "Phone number must not exceed 30 characters")
    private String phone;

    private Long collegeId;

    private Long departmentId;

    @Min(value = 1, message = "Year of study must be between 1 and 8")
    @Max(value = 8, message = "Year of study must be between 1 and 8")
    private Integer yearOfStudy;

    private Integer graduationYear;

    @DecimalMin(value = "0.0", message = "CGPA must be between 0.0 and 10.0")
    @DecimalMax(value = "10.0", message = "CGPA must be between 0.0 and 10.0")
    @Digits(integer = 2, fraction = 2, message = "CGPA can have at most 2 integer digits and 2 decimal digits")
    private BigDecimal cgpa;

    private String careerInterests;

    private String bio;

    @Size(max = 500, message = "Portfolio URL must not exceed 500 characters")
    private String portfolioUrl;

    @Size(max = 500, message = "GitHub URL must not exceed 500 characters")
    private String githubUrl;
}

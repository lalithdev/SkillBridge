package com.skillbridge.student.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "Project title is required")
    @Size(max = 255, message = "Project title must not exceed 255 characters")
    private String title;

    private String description;

    @Size(max = 500, message = "Project URL must not exceed 500 characters")
    private String projectUrl;
}

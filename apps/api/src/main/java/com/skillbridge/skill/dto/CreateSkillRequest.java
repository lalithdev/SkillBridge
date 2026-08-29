package com.skillbridge.skill.dto;

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
public class CreateSkillRequest {

    @NotBlank(message = "Skill name is required")
    @Size(max = 150, message = "Skill name must not exceed 150 characters")
    private String name;

    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;
}

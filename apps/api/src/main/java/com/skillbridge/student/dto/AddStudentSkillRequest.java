package com.skillbridge.student.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddStudentSkillRequest {

    @NotNull(message = "Skill ID is required")
    private Long skillId;
}

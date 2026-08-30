package com.skillbridge.internship.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollegeFeedbackItemDto {

    private Long id;
    private String studentName;
    private String departmentName;
    private String companyName;
    private String opportunityTitle;
    private String feedbackText;
    private Instant submittedAt;
}

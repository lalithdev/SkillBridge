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
public class FeedbackDto {

    private Long id;
    private Long internshipRecordId;
    private String studentName;
    private String companyName;
    private String feedbackText;
    private Instant submittedAt;
}

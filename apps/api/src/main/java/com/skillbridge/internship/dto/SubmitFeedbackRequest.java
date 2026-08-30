package com.skillbridge.internship.dto;

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
public class SubmitFeedbackRequest {

    @NotBlank(message = "Feedback text is required")
    @Size(min = 10, message = "Feedback text must be at least 10 characters")
    private String feedbackText;
}

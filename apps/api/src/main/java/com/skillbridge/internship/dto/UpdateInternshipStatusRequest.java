package com.skillbridge.internship.dto;

import com.skillbridge.internship.entity.InternshipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInternshipStatusRequest {

    @NotNull(message = "Status is required")
    private InternshipStatus status;

    private LocalDate startDate;
    private LocalDate endDate;
}

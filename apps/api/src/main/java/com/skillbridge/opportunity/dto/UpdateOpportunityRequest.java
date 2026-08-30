package com.skillbridge.opportunity.dto;

import com.skillbridge.opportunity.entity.OpportunityMode;
import com.skillbridge.opportunity.entity.OpportunityType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Request body for PUT /opportunities/{id}.
 * Same fields as CreateOpportunityRequest — full replacement of all mutable fields.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOpportunityRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    private String description;

    @NotNull(message = "Opportunity type is required")
    private OpportunityType type;

    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    private OpportunityMode mode;

    @Min(value = 1, message = "Duration must be at least 1 week")
    private Integer durationWeeks;

    @DecimalMin(value = "0.0", inclusive = true, message = "Stipend amount must be non-negative")
    private BigDecimal stipendAmount;

    @Size(max = 10, message = "Currency code must not exceed 10 characters")
    private String stipendCurrency;

    @DecimalMin(value = "0.0", inclusive = true, message = "Minimum CGPA must be at least 0.0")
    @DecimalMax(value = "10.0", inclusive = true, message = "Minimum CGPA must not exceed 10.0")
    private BigDecimal minCgpa;

    private LocalDate applicationDeadline;

    @NotNull(message = "Required skill IDs are required")
    @Size(min = 1, message = "At least one required skill must be specified")
    private List<Long> requiredSkillIds;

    private List<Long> requiredDepartmentIds;

    private List<Integer> requiredYearsOfStudy;
}

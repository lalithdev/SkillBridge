package com.skillbridge.matching.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * Eligibility check result for FR-MATCH-02.
 * Independent from skill match score — an ineligible student can still see their match %.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EligibilityResultDto {

    @JsonProperty("isEligible")
    private boolean eligible;

    /** Human-readable reasons, e.g. "Branch not eligible", "CGPA below minimum (required: 8.0)". */
    private List<String> failureReasons;
}

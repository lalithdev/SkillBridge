package com.skillbridge.internship.dto;

import com.skillbridge.internship.entity.InternshipStatus;
import com.skillbridge.opportunity.entity.OpportunityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternshipRecordDto {

    private Long id;
    private Long applicationId;
    private Long studentId;
    private String studentName;
    private Long companyId;
    private String companyName;
    private String opportunityTitle;
    private OpportunityType type;
    private InternshipStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean feedbackSubmitted;
}

package com.skillbridge.admin.dto;

import com.skillbridge.opportunity.entity.OpportunityStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerateOpportunityRequest {

    @NotNull(message = "Status is required")
    private OpportunityStatus status;
}
